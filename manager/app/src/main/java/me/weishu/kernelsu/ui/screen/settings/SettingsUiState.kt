package me.weishu.kernelsu.ui.screen.settings

import androidx.compose.runtime.Immutable
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import me.weishu.kernelsu.ui.theme.CustomThemePreset
import me.weishu.kernelsu.ui.theme.ThemeAppearanceDefaults
import me.weishu.kernelsu.ui.UiMode
import me.weishu.kernelsu.ui.theme.ThemePreset
import me.weishu.kernelsu.ui.theme.ThemeSyncStrategy
import me.weishu.kernelsu.ui.util.CustomWallpaperCrop
import me.weishu.kernelsu.ui.util.DEFAULT_CUSTOM_WALLPAPER_OPACITY
import me.weishu.kernelsu.ui.util.DEFAULT_CUSTOM_WALLPAPER_PASSTHROUGH_OPACITY
import me.weishu.kernelsu.ui.util.LauncherIconOption

@Immutable
data class SettingsUiState(
    val uiMode: String = UiMode.DEFAULT_VALUE,
    val checkModuleUpdate: Boolean = true,
    val themeMode: Int = 0,
    val miuixMonet: Boolean = false,
    val keyColor: Int = 0,
    val colorStyle: String = PaletteStyle.TonalSpot.name,
    val colorSpec: String = ColorSpec.SpecVersion.Default.name,
    val themePreset: String = ThemePreset.CLEAN_TOOL.value,
    val enablePredictiveBack: Boolean = false,
    val enableBlur: Boolean = true,
    val enableFloatingBottomBar: Boolean = false,
    val enableFloatingBottomBarBlur: Boolean = false,
    val pageScale: Float = 1.0f,
    val fontScale: Float = ThemeAppearanceDefaults.FONT_SCALE,
    val blurIntensity: Float = ThemeAppearanceDefaults.BLUR_INTENSITY,
    val themeSyncStrategy: ThemeSyncStrategy = ThemeSyncStrategy.SHARED,
    val customThemePresets: List<CustomThemePreset> = emptyList(),
    val enableWebDebugging: Boolean = false,
    val launcherIcon: String = LauncherIconOption.DEFAULT_VALUE,
    val customWallpaperUri: String? = null,
    val customWallpaperOpacity: Float = DEFAULT_CUSTOM_WALLPAPER_OPACITY,
    val customWallpaperCrop: CustomWallpaperCrop = CustomWallpaperCrop(),
    val customWallpaperPassthroughEnabled: Boolean = false,
    val customWallpaperPassthroughOpacity: Float = DEFAULT_CUSTOM_WALLPAPER_PASSTHROUGH_OPACITY,
    val customStartupAnimationUri: String? = null,
    val customStartupSoundUri: String? = null,

    // Su Compat
    val suCompatStatus: String = "",
    val suCompatMode: Int = 0, // 0: enable default, 1: disable until reboot, 2: disable always
    val isSuEnabled: Boolean = false,

    // Kernel Umount
    val kernelUmountStatus: String = "",
    val isKernelUmountEnabled: Boolean = false,

    // SELinux Hide
    val selinuxHideStatus: String = "",
    val isSelinuxHideEnabled: Boolean = false,

    // SU Log
    val sulogStatus: String = "",
    val isSulogEnabled: Boolean = false,

    // Umount Modules
    val isDefaultUmountModules: Boolean = false,

    // ADB Root
    val adbRootStatus: String = "",
    val isAdbRootEnabled: Boolean = false,

    // AVC Spoof
    val avcSpoofStatus: String = "",
    val isAvcSpoofEnabled: Boolean = false,

    val isLkmMode: Boolean = false,
    val isLateLoadMode: Boolean = false,

    // Auto Jailbreak
    val autoJailbreak: Boolean = false
)

@Immutable
data class SettingsScreenActions(
    val onSetCheckModuleUpdate: (Boolean) -> Unit,
    val onOpenTheme: () -> Unit,
    val onOpenThemeStore: () -> Unit,
    val onSetUiModeIndex: (Int) -> Unit,
    val onOpenLauncherIcon: () -> Unit,
    val onPickWallpaper: () -> Unit,
    val onPreviewWallpaper: () -> Unit,
    val onEditWallpaperCrop: () -> Unit,
    val onClearWallpaper: () -> Unit,
    val onSetWallpaperOpacity: (Float) -> Unit,
    val onSetWallpaperCrop: (CustomWallpaperCrop) -> Unit,
    val onSetWallpaperPassthroughEnabled: (Boolean) -> Unit,
    val onSetWallpaperPassthroughOpacity: (Float) -> Unit,
    val onSaveCustomThemePreset: (String) -> Unit,
    val onApplyCustomThemePreset: (String) -> Unit,
    val onRenameCustomThemePreset: (String, String) -> Unit,
    val onDeleteCustomThemePreset: (String) -> Unit,
    val onSetThemeSyncStrategy: (ThemeSyncStrategy) -> Unit,
    val onResetThemeToDefault: () -> Unit,
    val onPickStartupAnimation: () -> Unit,
    val onPreviewStartupAnimation: () -> Unit,
    val onClearStartupAnimation: () -> Unit,
    val onPickStartupSound: () -> Unit,
    val onPreviewStartupSound: () -> Unit,
    val onClearStartupSound: () -> Unit,
    val onOpenProfileTemplate: () -> Unit,
    val onSetSuCompatMode: (Int) -> Unit,
    val onSetKernelUmountEnabled: (Boolean) -> Unit,
    val onSetSelinuxHideEnabled: (Boolean) -> Unit,
    val onSetSulogEnabled: (Boolean) -> Unit,
    val onSetAdbRootEnabled: (Boolean) -> Unit,
    val onSetAvcSpoofEnabled: (Boolean) -> Unit,
    val onSetDefaultUmountModules: (Boolean) -> Unit,
    val onSetEnableWebDebugging: (Boolean) -> Unit,
    val onSetAutoJailbreak: (Boolean) -> Unit,
    val onOpenAbout: () -> Unit,
)
