package me.weishu.kernelsu.data.repository

import me.weishu.kernelsu.ui.theme.ThemePreset
import me.weishu.kernelsu.ui.theme.CustomThemePreset
import me.weishu.kernelsu.ui.theme.ThemeSyncStrategy
import me.weishu.kernelsu.ui.util.CustomWallpaperCrop

interface SettingsRepository {
    var uiMode: String
    var checkModuleUpdate: Boolean
    var themeMode: Int
    var miuixMonet: Boolean
    var keyColor: Int
    var colorStyle: String
    var colorSpec: String
    var themePreset: String
    var enablePredictiveBack: Boolean
    var enableBlur: Boolean
    var enableFloatingBottomBar: Boolean
    var enableFloatingBottomBarBlur: Boolean
    var pageScale: Float
    var fontScale: Float
    var blurIntensity: Float
    var themeSyncStrategy: ThemeSyncStrategy
    var enableWebDebugging: Boolean
    var autoJailbreak: Boolean
    var launcherIcon: String
    var customWallpaperUri: String?
    var customWallpaperOpacity: Float
    var customWallpaperCrop: CustomWallpaperCrop
    var customWallpaperPassthroughEnabled: Boolean
    var customWallpaperPassthroughOpacity: Float
    var customStartupAnimationUri: String?
    var customStartupSoundUri: String?

    suspend fun getSuCompatStatus(): String
    suspend fun getSuCompatPersistValue(): Long?
    fun isSuEnabled(): Boolean
    fun setSuEnabled(enabled: Boolean): Boolean
    fun setSuCompatModePref(mode: Int)
    fun getSuCompatModePref(): Int

    suspend fun getKernelUmountStatus(): String
    fun isKernelUmountEnabled(): Boolean
    fun setKernelUmountEnabled(enabled: Boolean): Boolean

    suspend fun getSelinuxHideStatus(): String
    fun isSelinuxHideEnabled(): Boolean
    fun setSelinuxHideEnabled(enabled: Boolean): Int

    suspend fun getSulogStatus(): String
    suspend fun getSulogPersistValue(): Long?
    fun setSulogEnabled(enabled: Boolean): Boolean

    suspend fun getAdbRootStatus(): String
    suspend fun getAdbRootPersistValue(): Long?
    fun setAdbRootEnabled(enabled: Boolean): Boolean

    suspend fun getAvcSpoofStatus(): String
    fun isAvcSpoofEnabled(): Boolean
    fun setAvcSpoofEnabled(enabled: Boolean): Boolean

    fun isDefaultUmountModules(): Boolean
    fun setDefaultUmountModules(enabled: Boolean): Boolean

    fun isLkmMode(): Boolean

    fun applyThemePreset(preset: ThemePreset)
    fun saveCustomThemePreset(name: String): CustomThemePreset?
    fun applyCustomThemePreset(presetId: String): Boolean
    fun renameCustomThemePreset(presetId: String, name: String): Boolean
    fun deleteCustomThemePreset(presetId: String): Boolean
    fun getCustomThemePresets(): List<CustomThemePreset>
    fun resetThemeToDefault()

    fun execKsudFeatureSave()
}
