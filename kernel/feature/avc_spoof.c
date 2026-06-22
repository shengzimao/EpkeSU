#include "feature/avc_spoof.h"

#include <linux/atomic.h>
#include <linux/kprobes.h>
#include <linux/mutex.h>
#include <linux/security.h>
#include <linux/slab.h>
#include <linux/string.h>
#include <linux/version.h>

#include "arch.h"
#include "klog.h" // IWYU pragma: keep
#include "policy/feature.h"
#include "selinux/selinux.h"

#define KSU_AVC_SPOOF_CONTEXT "u:r:priv_app:s0:c512,c768"

static DEFINE_MUTEX(avc_spoof_mutex);

static u32 su_sid __read_mostly;
static u32 ksu_sid __read_mostly;
static u32 priv_app_sid __read_mostly;

static atomic_t avc_spoof_disabled = ATOMIC_INIT(1);
static bool ksu_avc_spoof_enabled __read_mostly = true;
static bool ksu_avc_spoof_running __read_mostly;
static bool ksu_avc_spoof_boot_completed __read_mostly;

#ifdef CONFIG_KPROBES
static struct kprobe *slow_avc_audit_kp;

static int ksu_handle_slow_avc_audit(u32 *tsid)
{
    if (atomic_read(&avc_spoof_disabled))
        return 0;

    if ((su_sid && *tsid == su_sid) || *tsid == ksu_sid) {
        pr_debug("avc_spoof: replace tsid %u with %u\n", *tsid, priv_app_sid);
        *tsid = priv_app_sid;
    }

    return 0;
}

static int slow_avc_audit_pre_handler(struct kprobe *p, struct pt_regs *regs)
{
    if (atomic_read(&avc_spoof_disabled))
        return 0;

    /*
     * slow_avc_audit has used both (ssid, tsid, ...) and
     * (state, ssid, tsid, ...) ABIs across Android kernel versions.
     */
#if LINUX_VERSION_CODE < KERNEL_VERSION(4, 17, 0) || LINUX_VERSION_CODE >= KERNEL_VERSION(6, 4, 0)
    ksu_handle_slow_avc_audit((u32 *)&PT_REGS_PARM2(regs));
#else
    ksu_handle_slow_avc_audit((u32 *)&PT_REGS_PARM3(regs));
#endif

    return 0;
}

static struct kprobe *ksu_init_kprobe(const char *name, kprobe_pre_handler_t handler)
{
    struct kprobe *kp;
    int ret;

    kp = kzalloc(sizeof(*kp), GFP_KERNEL);
    if (!kp)
        return NULL;

    kp->symbol_name = name;
    kp->pre_handler = handler;

    ret = register_kprobe(kp);
    pr_info("avc_spoof: register_%s kprobe: %d\n", name, ret);
    if (ret) {
        kfree(kp);
        return NULL;
    }

    return kp;
}

static void ksu_destroy_kprobe(struct kprobe **kp_ptr)
{
    struct kprobe *kp = *kp_ptr;

    if (!kp)
        return;

    unregister_kprobe(kp);
    synchronize_rcu();
    kfree(kp);
    *kp_ptr = NULL;
}
#endif

static int ksu_avc_spoof_cache_sids(void)
{
    int err;

    err = security_secctx_to_secid("u:r:su:s0", strlen("u:r:su:s0"), &su_sid);
    if (err) {
        pr_info("avc_spoof: su sid not found: %d\n", err);
        su_sid = 0;
    }

    err = security_secctx_to_secid(KERNEL_SU_CONTEXT, strlen(KERNEL_SU_CONTEXT), &ksu_sid);
    if (err) {
        pr_info("avc_spoof: ksu sid not found: %d\n", err);
        return err;
    }

    err = security_secctx_to_secid(KSU_AVC_SPOOF_CONTEXT, strlen(KSU_AVC_SPOOF_CONTEXT), &priv_app_sid);
    if (err) {
        pr_info("avc_spoof: priv_app sid not found: %d\n", err);
        return err;
    }

    pr_info("avc_spoof: cached su=%u ksu=%u priv_app=%u\n", su_sid, ksu_sid, priv_app_sid);
    return 0;
}

static int ksu_avc_spoof_enable_locked(void)
{
    int ret;

    if (ksu_avc_spoof_running)
        return 0;

    ret = ksu_avc_spoof_cache_sids();
    if (ret)
        return ret;

#ifndef CONFIG_KPROBES
    pr_info("avc_spoof: CONFIG_KPROBES is disabled\n");
    return -EOPNOTSUPP;
#else
    slow_avc_audit_kp = ksu_init_kprobe("slow_avc_audit", slow_avc_audit_pre_handler);
    if (!slow_avc_audit_kp)
        return -ENOENT;

    atomic_set(&avc_spoof_disabled, 0);
    ksu_avc_spoof_running = true;
    pr_info("avc_spoof: enabled\n");
    return 0;
#endif
}

static void ksu_avc_spoof_disable_locked(void)
{
    if (!ksu_avc_spoof_running)
        return;

    atomic_set(&avc_spoof_disabled, 1);
#ifdef CONFIG_KPROBES
    ksu_destroy_kprobe(&slow_avc_audit_kp);
#endif
    ksu_avc_spoof_running = false;
    pr_info("avc_spoof: disabled\n");
}

static int avc_spoof_feature_get(u64 *value)
{
    mutex_lock(&avc_spoof_mutex);
    *value = (ksu_avc_spoof_boot_completed ? ksu_avc_spoof_running : ksu_avc_spoof_enabled) ? 1 : 0;
    mutex_unlock(&avc_spoof_mutex);
    return 0;
}

static int avc_spoof_feature_set(u64 value)
{
    bool enable = value != 0;
    int ret = 0;

    mutex_lock(&avc_spoof_mutex);

    if (ksu_avc_spoof_boot_completed) {
        if (enable)
            ret = ksu_avc_spoof_enable_locked();
        else
            ksu_avc_spoof_disable_locked();
    }

    if (!ret)
        ksu_avc_spoof_enabled = enable;

    mutex_unlock(&avc_spoof_mutex);

    pr_info("avc_spoof: set to %d\n", enable);
    return ret;
}

static const struct ksu_feature_handler avc_spoof_handler = {
    .feature_id = KSU_FEATURE_AVC_SPOOF,
    .name = "avc_spoof",
    .get_handler = avc_spoof_feature_get,
    .set_handler = avc_spoof_feature_set,
};

void ksu_avc_spoof_handle_boot_completed(void)
{
    mutex_lock(&avc_spoof_mutex);
    ksu_avc_spoof_boot_completed = true;
    if (ksu_avc_spoof_enabled)
        ksu_avc_spoof_enable_locked();
    mutex_unlock(&avc_spoof_mutex);
}

void __init ksu_avc_spoof_init(void)
{
    if (ksu_register_feature_handler(&avc_spoof_handler)) {
        pr_err("Failed to register avc_spoof feature handler\n");
    }
}

void __exit ksu_avc_spoof_exit(void)
{
    ksu_unregister_feature_handler(KSU_FEATURE_AVC_SPOOF);

    mutex_lock(&avc_spoof_mutex);
    ksu_avc_spoof_disable_locked();
    mutex_unlock(&avc_spoof_mutex);
}
