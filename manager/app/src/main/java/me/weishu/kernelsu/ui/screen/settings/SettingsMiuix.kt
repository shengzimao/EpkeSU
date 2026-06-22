package me.weishu.kernelsu.ui.screen.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Article
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Adb
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContactPage
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DeveloperMode
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.ElectricalServices
import androidx.compose.material.icons.rounded.Fence
import androidx.compose.material.icons.rounded.FolderDelete
import androidx.compose.material.icons.rounded.ImageSearch
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material.icons.rounded.RemoveCircle
import androidx.compose.material.icons.rounded.RemoveModerator
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.weishu.kernelsu.R
import me.weishu.kernelsu.ui.InterfaceStyle
import me.weishu.kernelsu.ui.component.CustomWallpaperBackground
import me.weishu.kernelsu.ui.component.KsuIsValid
import me.weishu.kernelsu.ui.component.dialog.rememberLoadingDialog
import me.weishu.kernelsu.ui.component.liquid.globalLiquidGlassSurface
import me.weishu.kernelsu.ui.component.liquid.liquidGlassMiuixCardColors
import me.weishu.kernelsu.ui.component.miuix.SendLogDialog
import me.weishu.kernelsu.ui.component.uninstalldialog.UninstallDialog
import me.weishu.kernelsu.ui.theme.LocalEnableBlur
import me.weishu.kernelsu.ui.theme.skrootproTopBarColors
import me.weishu.kernelsu.ui.util.BlurredBar
import me.weishu.kernelsu.ui.util.MAX_CUSTOM_WALLPAPER_OPACITY
import me.weishu.kernelsu.ui.util.MAX_CUSTOM_WALLPAPER_PASSTHROUGH_OPACITY
import me.weishu.kernelsu.ui.util.MIN_CUSTOM_WALLPAPER_OPACITY
import me.weishu.kernelsu.ui.util.MIN_CUSTOM_WALLPAPER_PASSTHROUGH_OPACITY
import me.weishu.kernelsu.ui.util.rememberBlurBackdrop
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.SliderDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.OverlayDropdownPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic
import kotlin.math.roundToInt

/**
 * @author weishu
 * @date 2023/1/1.
 */
@Composable
fun SettingPagerMiuix(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    bottomInnerPadding: Dp,
) {
    val scrollBehavior = MiuixScrollBehavior()
    val enableBlur = LocalEnableBlur.current
    val backdrop = rememberBlurBackdrop(enableBlur)
    val blurActive = backdrop != null
    val barColor = if (blurActive) Color.Transparent else colorScheme.surface
    val topBarColors = skrootproTopBarColors(barColor, colorScheme.onSurface)
    val loadingDialog = rememberLoadingDialog()
    val showUninstallDialog = rememberSaveable { mutableStateOf(false) }
    val showSendLogDialog = rememberSaveable { mutableStateOf(false) }
    val showWallpaperOpacitySlider = rememberSaveable { mutableStateOf(false) }
    val showWallpaperPassthroughOpacitySlider = rememberSaveable { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            BlurredBar(backdrop) {
                TopAppBar(
                    color = topBarColors.container,
                    titleColor = topBarColors.content,
                    title = stringResource(R.string.settings),
                    scrollBehavior = scrollBehavior
                )
            }
        },
        popupHost = { },
        contentWindowInsets = WindowInsets.systemBars.add(WindowInsets.displayCutout).only(WindowInsetsSides.Horizontal),
    ) { innerPadding ->
        Box(modifier = if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .scrollEndHaptic()
                    .overScrollVertical()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .padding(horizontal = 12.dp),
                contentPadding = innerPadding,
                overscrollEffect = null,
            ) {
                item {
                    KsuIsValid {
                        SettingsSectionTitle(
                            text = stringResource(R.string.settings_section_updates),
                            topPadding = 12.dp,
                        )
                        Card(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .fillMaxWidth()
                                .settingsLiquidGlassSurface(),
                            colors = liquidGlassMiuixCardColors(),
                        ) {
                            SwitchPreference(
                                title = stringResource(id = R.string.settings_module_check_update),
                                summary = stringResource(id = R.string.settings_module_check_update_summary),
                                startAction = {
                                    Icon(
                                        Icons.Rounded.UploadFile,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_module_check_update),
                                        tint = colorScheme.onBackground
                                    )
                                },
                                checked = uiState.checkModuleUpdate,
                                onCheckedChange = actions.onSetCheckModuleUpdate
                            )
                        }
                    }

                    SettingsSectionTitle(text = stringResource(R.string.settings_section_appearance))
                    Card(
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .fillMaxWidth()
                            .settingsLiquidGlassSurface(),
                        colors = liquidGlassMiuixCardColors(),
                    ) {
                        OverlayDropdownPreference(
                            title = stringResource(id = R.string.settings_ui_mode),
                            summary = stringResource(id = R.string.settings_ui_mode_summary),
                            items = InterfaceStyle.entries.map { it.label },
                            startAction = {
                                Icon(
                                    Icons.Rounded.Dashboard,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = stringResource(id = R.string.settings_ui_mode),
                                    tint = colorScheme.onBackground
                                )
                            },
                            selectedIndex = InterfaceStyle.selectedIndex(uiState.uiMode),
                            onSelectedIndexChange = actions.onSetUiModeIndex
                        )
                        ArrowPreference(
                            title = stringResource(id = R.string.settings_theme),
                            summary = stringResource(id = R.string.settings_theme_summary),
                            startAction = {
                                Icon(
                                    Icons.Rounded.Palette,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = stringResource(id = R.string.settings_theme),
                                    tint = colorScheme.onBackground
                                )
                            },
                            onClick = actions.onOpenTheme
                        )
                        ArrowPreference(
                            title = stringResource(id = R.string.theme_store),
                            summary = stringResource(id = R.string.theme_store_settings_summary),
                            startAction = {
                                Icon(
                                    Icons.Rounded.Storefront,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = stringResource(id = R.string.theme_store),
                                    tint = colorScheme.onBackground
                                )
                            },
                            onClick = actions.onOpenThemeStore
                        )
                        ArrowPreference(
                            title = stringResource(id = R.string.settings_app_icon),
                            summary = stringResource(id = R.string.settings_app_icon_summary),
                            startAction = {
                                Icon(
                                    Icons.Rounded.Apps,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = stringResource(id = R.string.settings_app_icon),
                                    tint = colorScheme.onBackground
                                )
                            },
                            onClick = actions.onOpenLauncherIcon
                        )
                        ArrowPreference(
                            title = stringResource(id = R.string.settings_wallpaper),
                            summary = stringResource(
                                if (uiState.customWallpaperUri == null) {
                                    R.string.settings_wallpaper_summary
                                } else {
                                    R.string.settings_wallpaper_selected_summary
                                }
                            ),
                            startAction = {
                                Icon(
                                    Icons.Rounded.Wallpaper,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = stringResource(id = R.string.settings_wallpaper),
                                    tint = colorScheme.onBackground
                                )
                            },
                            onClick = actions.onPickWallpaper
                        )
                        if (uiState.customWallpaperUri != null) {
                            ArrowPreference(
                                title = stringResource(id = R.string.settings_wallpaper_crop),
                                summary = stringResource(id = R.string.settings_wallpaper_crop_summary),
                                startAction = {
                                    Icon(
                                        Icons.Rounded.ImageSearch,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_wallpaper_crop),
                                        tint = colorScheme.onBackground
                                    )
                                },
                                onClick = actions.onEditWallpaperCrop
                            )
                            var wallpaperOpacity by remember(uiState.customWallpaperOpacity) {
                                mutableFloatStateOf(uiState.customWallpaperOpacity)
                            }
                            ArrowPreference(
                                title = stringResource(id = R.string.settings_wallpaper_opacity),
                                summary = stringResource(id = R.string.settings_wallpaper_opacity_summary),
                                startAction = {
                                    Icon(
                                        Icons.Rounded.Wallpaper,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_wallpaper_opacity),
                                        tint = colorScheme.onBackground
                                    )
                                },
                                endActions = {
                                    Text(
                                        text = "${(wallpaperOpacity * 100).roundToInt()}%",
                                        color = colorScheme.onSurfaceVariantActions,
                                    )
                                },
                                onClick = {
                                    showWallpaperOpacitySlider.value = !showWallpaperOpacitySlider.value
                                },
                                holdDownState = showWallpaperOpacitySlider.value,
                                bottomAction = {
                                    Slider(
                                        value = wallpaperOpacity,
                                        onValueChange = {
                                            wallpaperOpacity = it
                                            actions.onSetWallpaperOpacity(it)
                                        },
                                        valueRange = MIN_CUSTOM_WALLPAPER_OPACITY..MAX_CUSTOM_WALLPAPER_OPACITY,
                                        showKeyPoints = true,
                                        keyPoints = listOf(0.1f, 0.3f, 0.5f, 0.8f),
                                        magnetThreshold = 0.01f,
                                        hapticEffect = SliderDefaults.SliderHapticEffect.Step,
                                    )
                                },
                            )
                            SwitchPreference(
                                title = stringResource(id = R.string.settings_wallpaper_passthrough),
                                summary = stringResource(id = R.string.settings_wallpaper_passthrough_summary),
                                startAction = {
                                    Icon(
                                        Icons.Rounded.Layers,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_wallpaper_passthrough),
                                        tint = colorScheme.onBackground
                                    )
                                },
                                checked = uiState.customWallpaperPassthroughEnabled,
                                onCheckedChange = actions.onSetWallpaperPassthroughEnabled
                            )
                            if (uiState.customWallpaperPassthroughEnabled) {
                                var passthroughOpacity by remember(uiState.customWallpaperPassthroughOpacity) {
                                    mutableFloatStateOf(uiState.customWallpaperPassthroughOpacity)
                                }
                                ArrowPreference(
                                    title = stringResource(id = R.string.settings_wallpaper_passthrough_opacity),
                                    summary = stringResource(id = R.string.settings_wallpaper_passthrough_opacity_summary),
                                    startAction = {
                                        Icon(
                                            Icons.Rounded.Layers,
                                            modifier = Modifier.padding(end = 6.dp),
                                            contentDescription = stringResource(id = R.string.settings_wallpaper_passthrough_opacity),
                                            tint = colorScheme.onBackground
                                        )
                                    },
                                    endActions = {
                                        Text(
                                            text = "${(passthroughOpacity * 100).roundToInt()}%",
                                            color = colorScheme.onSurfaceVariantActions,
                                        )
                                    },
                                    onClick = {
                                        showWallpaperPassthroughOpacitySlider.value =
                                            !showWallpaperPassthroughOpacitySlider.value
                                    },
                                    holdDownState = showWallpaperPassthroughOpacitySlider.value,
                                    bottomAction = {
                                        Slider(
                                            value = passthroughOpacity,
                                            onValueChange = {
                                                passthroughOpacity = it
                                                actions.onSetWallpaperPassthroughOpacity(it)
                                            },
                                            valueRange = MIN_CUSTOM_WALLPAPER_PASSTHROUGH_OPACITY..MAX_CUSTOM_WALLPAPER_PASSTHROUGH_OPACITY,
                                            showKeyPoints = true,
                                            keyPoints = listOf(0.0f, 0.25f, 0.45f, 0.65f),
                                            magnetThreshold = 0.01f,
                                            hapticEffect = SliderDefaults.SliderHapticEffect.Step,
                                        )
                                    },
                                )
                            }
                            ArrowPreference(
                                title = stringResource(id = R.string.settings_wallpaper_preview),
                                startAction = {
                                    Icon(
                                        Icons.Rounded.Visibility,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_wallpaper_preview),
                                        tint = colorScheme.onBackground
                                    )
                                },
                                onClick = actions.onPreviewWallpaper
                            )
                            ArrowPreference(
                                title = stringResource(id = R.string.settings_wallpaper_clear),
                                startAction = {
                                    Icon(
                                        Icons.Rounded.Close,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_wallpaper_clear),
                                        tint = colorScheme.onBackground
                                    )
                                },
                                onClick = actions.onClearWallpaper
                            )
                        }
                        ArrowPreference(
                            title = stringResource(id = R.string.settings_startup_sound),
                            summary = stringResource(
                                if (uiState.customStartupSoundUri == null) {
                                    R.string.settings_startup_sound_summary
                                } else {
                                    R.string.settings_startup_sound_selected_summary
                                }
                            ),
                            startAction = {
                                Icon(
                                    Icons.AutoMirrored.Rounded.VolumeUp,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = stringResource(id = R.string.settings_startup_sound),
                                    tint = colorScheme.onBackground
                                )
                            },
                            onClick = actions.onPickStartupSound
                        )
                        ArrowPreference(
                            title = stringResource(id = R.string.settings_startup_animation),
                            summary = stringResource(
                                if (uiState.customStartupAnimationUri == null) {
                                    R.string.settings_startup_animation_summary
                                } else {
                                    R.string.settings_startup_animation_selected_summary
                                }
                            ),
                            startAction = {
                                Icon(
                                    Icons.Rounded.PlayCircle,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = stringResource(id = R.string.settings_startup_animation),
                                    tint = colorScheme.onBackground
                                )
                            },
                            onClick = actions.onPickStartupAnimation
                        )
                        if (uiState.customStartupAnimationUri != null) {
                            ArrowPreference(
                                title = stringResource(id = R.string.settings_startup_animation_preview),
                                startAction = {
                                    Icon(
                                        Icons.Rounded.Visibility,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_startup_animation_preview),
                                        tint = colorScheme.onBackground
                                    )
                                },
                                onClick = actions.onPreviewStartupAnimation
                            )
                            ArrowPreference(
                                title = stringResource(id = R.string.settings_startup_animation_clear),
                                startAction = {
                                    Icon(
                                        Icons.Rounded.Close,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_startup_animation_clear),
                                        tint = colorScheme.onBackground
                                    )
                                },
                                onClick = actions.onClearStartupAnimation
                            )
                        }
                        if (uiState.customStartupSoundUri != null) {
                            ArrowPreference(
                                title = stringResource(id = R.string.settings_startup_sound_preview),
                                startAction = {
                                    Icon(
                                        Icons.AutoMirrored.Rounded.VolumeUp,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_startup_sound_preview),
                                        tint = colorScheme.onBackground
                                    )
                                },
                                onClick = actions.onPreviewStartupSound
                            )
                            ArrowPreference(
                                title = stringResource(id = R.string.settings_startup_sound_clear),
                                startAction = {
                                    Icon(
                                        Icons.Rounded.Close,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_startup_sound_clear),
                                        tint = colorScheme.onBackground
                                    )
                                },
                                onClick = actions.onClearStartupSound
                            )
                        }
                    }

                    KsuIsValid {
                        SettingsSectionTitle(text = stringResource(R.string.settings_section_profiles))
                        Card(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .fillMaxWidth()
                                .settingsLiquidGlassSurface(),
                            colors = liquidGlassMiuixCardColors(),
                        ) {
                            val profileTemplate = stringResource(id = R.string.settings_profile_template)
                            ArrowPreference(
                                title = profileTemplate,
                                summary = stringResource(id = R.string.settings_profile_template_summary),
                                startAction = {
                                    Icon(
                                        Icons.Rounded.Fence,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = profileTemplate,
                                        tint = colorScheme.onBackground
                                    )
                                },
                                onClick = actions.onOpenProfileTemplate
                            )
                        }
                    }

                    KsuIsValid {
                        SettingsSectionTitle(text = stringResource(R.string.settings_section_root_features))
                        Card(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .fillMaxWidth()
                                .settingsLiquidGlassSurface(),
                            colors = liquidGlassMiuixCardColors(),
                        ) {
                            val suCompatModeItems = listOf(
                                stringResource(id = R.string.settings_mode_enable_by_default),
                                stringResource(id = R.string.settings_mode_disable_until_reboot),
                                stringResource(id = R.string.settings_mode_disable_always),
                            )

                            val suSummary = when (uiState.suCompatStatus) {
                                "unsupported" -> stringResource(id = R.string.feature_status_unsupported_summary)
                                "managed" -> stringResource(id = R.string.feature_status_managed_summary)
                                else -> stringResource(id = R.string.settings_sucompat_summary)
                            }
                            OverlayDropdownPreference(
                                title = stringResource(id = R.string.settings_sucompat),
                                summary = suSummary,
                                items = suCompatModeItems,
                                startAction = {
                                    Icon(
                                        Icons.Rounded.RemoveModerator,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_sucompat),
                                        tint = colorScheme.onBackground
                                    )
                                },
                                enabled = uiState.suCompatStatus == "supported",
                                selectedIndex = uiState.suCompatMode,
                                onSelectedIndexChange = actions.onSetSuCompatMode
                            )

                            val umountSummary = when (uiState.kernelUmountStatus) {
                                "unsupported" -> stringResource(id = R.string.feature_status_unsupported_summary)
                                "managed" -> stringResource(id = R.string.feature_status_managed_summary)
                                else -> stringResource(id = R.string.settings_kernel_umount_summary)
                            }
                            SwitchPreference(
                                title = stringResource(id = R.string.settings_kernel_umount),
                                summary = umountSummary,
                                startAction = {
                                    Icon(
                                        Icons.Rounded.RemoveCircle,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_kernel_umount),
                                        tint = colorScheme.onBackground
                                    )
                                },
                                enabled = uiState.kernelUmountStatus == "supported",
                                checked = uiState.isKernelUmountEnabled,
                                onCheckedChange = actions.onSetKernelUmountEnabled
                            )

                            val selinuxHideSummary = when (uiState.selinuxHideStatus) {
                                "unsupported" -> stringResource(id = R.string.feature_status_unsupported_summary)
                                "managed" -> stringResource(id = R.string.feature_status_managed_summary)
                                else -> stringResource(id = R.string.settings_selinux_hide_summary)
                            }
                            SwitchPreference(
                                title = stringResource(id = R.string.settings_selinux_hide),
                                summary = selinuxHideSummary,
                                startAction = {
                                    Icon(
                                        Icons.Rounded.Policy,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_selinux_hide),
                                        tint = colorScheme.onBackground
                                    )
                                },
                                enabled = uiState.selinuxHideStatus == "supported",
                                checked = uiState.isSelinuxHideEnabled,
                                onCheckedChange = actions.onSetSelinuxHideEnabled
                            )

                            val sulogSummary = when (uiState.sulogStatus) {
                                "unsupported" -> stringResource(id = R.string.feature_status_unsupported_summary)
                                "managed" -> stringResource(id = R.string.feature_status_managed_summary)
                                else -> stringResource(id = R.string.settings_sulog_summary)
                            }
                            SwitchPreference(
                                title = stringResource(id = R.string.settings_sulog),
                                summary = sulogSummary,
                                startAction = {
                                    Icon(
                                        Icons.AutoMirrored.Rounded.Article,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_sulog),
                                        tint = if (uiState.sulogStatus == "supported") colorScheme.onBackground else colorScheme.disabledOnSecondaryVariant
                                    )
                                },
                                enabled = uiState.sulogStatus == "supported",
                                checked = uiState.isSulogEnabled,
                                onCheckedChange = actions.onSetSulogEnabled
                            )

                            val adbRootSummary = when (uiState.adbRootStatus) {
                                "unsupported" -> stringResource(id = R.string.feature_status_unsupported_summary)
                                "managed" -> stringResource(id = R.string.feature_status_managed_summary)
                                else -> stringResource(id = R.string.settings_adb_root_summary)
                            }
                            SwitchPreference(
                                title = stringResource(id = R.string.settings_adb_root),
                                summary = adbRootSummary,
                                startAction = {
                                    Icon(
                                        Icons.Rounded.Adb,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_adb_root),
                                        tint = colorScheme.onBackground
                                    )
                                },
                                enabled = uiState.adbRootStatus == "supported",
                                checked = uiState.isAdbRootEnabled,
                                onCheckedChange = actions.onSetAdbRootEnabled
                            )

                            val avcSpoofSummary = when (uiState.avcSpoofStatus) {
                                "unsupported" -> stringResource(id = R.string.feature_status_unsupported_summary)
                                "managed" -> stringResource(id = R.string.feature_status_managed_summary)
                                else -> stringResource(id = R.string.settings_avc_spoof_summary)
                            }
                            SwitchPreference(
                                title = stringResource(id = R.string.settings_avc_spoof),
                                summary = avcSpoofSummary,
                                startAction = {
                                    Icon(
                                        Icons.Rounded.EditNote,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_avc_spoof),
                                        tint = colorScheme.onBackground
                                    )
                                },
                                enabled = uiState.avcSpoofStatus == "supported",
                                checked = uiState.isAvcSpoofEnabled,
                                onCheckedChange = actions.onSetAvcSpoofEnabled
                            )
                        }

                        SettingsSectionTitle(text = stringResource(R.string.settings_section_advanced))
                        Card(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .fillMaxWidth()
                                .settingsLiquidGlassSurface(),
                            colors = liquidGlassMiuixCardColors(),
                        ) {
                            SwitchPreference(
                                title = stringResource(id = R.string.settings_umount_modules_default),
                                summary = stringResource(id = R.string.settings_umount_modules_default_summary),
                                startAction = {
                                    Icon(
                                        Icons.Rounded.FolderDelete,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_umount_modules_default),
                                        tint = colorScheme.onBackground
                                    )
                                },
                                checked = uiState.isDefaultUmountModules,
                                onCheckedChange = actions.onSetDefaultUmountModules
                            )

                            SwitchPreference(
                                title = stringResource(id = R.string.enable_web_debugging),
                                summary = stringResource(id = R.string.enable_web_debugging_summary),
                                startAction = {
                                    Icon(
                                        Icons.Rounded.DeveloperMode,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.enable_web_debugging),
                                        tint = colorScheme.onBackground
                                    )
                                },
                                checked = uiState.enableWebDebugging,
                                onCheckedChange = actions.onSetEnableWebDebugging
                            )
                            SwitchPreference(
                                title = stringResource(id = R.string.settings_auto_jailbreak),
                                summary = stringResource(id = R.string.settings_auto_jailbreak_summary),
                                startAction = {
                                    Icon(
                                        Icons.Rounded.ElectricalServices,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_auto_jailbreak),
                                        tint = if (uiState.isLateLoadMode) colorScheme.onBackground else colorScheme.disabledOnSecondaryVariant
                                    )
                                },
                                enabled = uiState.isLateLoadMode,
                                checked = uiState.autoJailbreak,
                                onCheckedChange = actions.onSetAutoJailbreak
                            )
                        }
                    }

                    if (uiState.isLkmMode) {
                        SettingsSectionTitle(text = stringResource(R.string.settings_section_maintenance))
                        Card(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .fillMaxWidth()
                                .settingsLiquidGlassSurface(),
                            colors = liquidGlassMiuixCardColors(),
                        ) {
                            val uninstall = stringResource(id = R.string.settings_uninstall)
                            ArrowPreference(
                                title = uninstall,
                                enabled = !uiState.isLateLoadMode,
                                startAction = {
                                    Icon(
                                        Icons.Rounded.Delete,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = uninstall,
                                        tint = colorScheme.onBackground,
                                    )
                                },
                                onClick = { showUninstallDialog.value = true },
                            )
                            UninstallDialog(
                                show = showUninstallDialog.value,
                                onDismissRequest = { showUninstallDialog.value = false }
                            )
                        }
                    } else {
                        SettingsSectionTitle(text = stringResource(R.string.settings_section_maintenance))
                    }

                    Card(
                        modifier = Modifier
                            .padding(top = 6.dp, bottom = 12.dp)
                            .fillMaxWidth()
                            .settingsLiquidGlassSurface(),
                        colors = liquidGlassMiuixCardColors(),
                    ) {
                        ArrowPreference(
                            title = stringResource(id = R.string.send_log),
                            startAction = {
                                Icon(
                                    Icons.Rounded.BugReport,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = stringResource(id = R.string.send_log),
                                    tint = colorScheme.onBackground
                                )
                            },
                            onClick = { showSendLogDialog.value = true },
                        )
                        SendLogDialog(
                            show = showSendLogDialog.value,
                            onDismissRequest = { showSendLogDialog.value = false },
                            loadingDialog = loadingDialog
                        )
                        val about = stringResource(id = R.string.about)
                        ArrowPreference(
                            title = about,
                            startAction = {
                                Icon(
                                    Icons.Rounded.ContactPage,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = about,
                                    tint = colorScheme.onBackground
                                )
                            },
                            onClick = actions.onOpenAbout,
                        )
                    }
                    Spacer(Modifier.height(bottomInnerPadding))
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionTitle(
    text: String,
    topPadding: Dp = 18.dp,
) {
    Text(
        modifier = Modifier.padding(start = 16.dp, top = topPadding, bottom = 2.dp),
        text = text,
        color = colorScheme.primary,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
    )
}

@Composable
private fun Modifier.settingsLiquidGlassSurface(): Modifier {
    return globalLiquidGlassSurface(
        shape = RoundedCornerShape(18.dp),
        surfaceAlpha = 0.58f,
        blurRadius = 10.dp,
        refractionHeight = 14.dp,
        refractionAmount = 9.dp,
        strokeAlpha = 0.66f,
    )
}
