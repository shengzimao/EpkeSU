package me.weishu.kernelsu.ui.viewmodel

import androidx.compose.runtime.Immutable
import me.weishu.kernelsu.ui.UiMode
import me.weishu.kernelsu.ui.theme.AppSettings
import me.weishu.kernelsu.ui.util.CustomWallpaperCrop

@Immutable
data class MainActivityUiState(
    val appSettings: AppSettings,
    val pageScale: Float,
    val fontScale: Float,
    val blurIntensity: Float,
    val enableBlur: Boolean,
    val enableFloatingBottomBar: Boolean,
    val enableFloatingBottomBarBlur: Boolean,
    val uiMode: UiMode,
    val interfaceStyle: String,
    val customWallpaperUri: String?,
    val customWallpaperOpacity: Float,
    val customWallpaperCrop: CustomWallpaperCrop,
    val customWallpaperPassthroughEnabled: Boolean,
    val customWallpaperPassthroughOpacity: Float,
    val customStartupAnimationUri: String?,
    val customStartupSoundUri: String?,
)
