package me.weishu.kernelsu.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.weishu.kernelsu.data.repository.SettingsRepository
import me.weishu.kernelsu.data.repository.SettingsRepositoryImpl
import me.weishu.kernelsu.ksuApp
import me.weishu.kernelsu.ui.InterfaceStyle
import me.weishu.kernelsu.ui.UiMode
import me.weishu.kernelsu.ui.theme.THEME_SYNC_STRATEGY_KEY
import me.weishu.kernelsu.ui.theme.ThemeController
import me.weishu.kernelsu.ui.theme.ThemePreferenceKeys

class MainActivityViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val prefs = ksuApp.getSharedPreferences("settings", Context.MODE_PRIVATE)
    private val settingRepo: SettingsRepository = SettingsRepositoryImpl()
    private val mainPageState = MainPageState(savedStateHandle)
    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == null || key in observedKeys) {
            _uiState.value = readUiState()
        }
    }

    private val _uiState = MutableStateFlow(readUiState())
    val uiState: StateFlow<MainActivityUiState> = _uiState.asStateFlow()
    val selectedMainPage: StateFlow<Int> = mainPageState.selectedPage

    init {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onCleared() {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
        super.onCleared()
    }

    fun setSelectedMainPage(page: Int) {
        mainPageState.updateSelectedPage(page)
    }

    private fun readUiState(): MainActivityUiState {
        val interfaceStyle = settingRepo.uiMode
        val isLiquidGlassInterface = interfaceStyle == InterfaceStyle.LiquidGlass.value
        return MainActivityUiState(
            appSettings = ThemeController.getAppSettings(ksuApp),
            pageScale = settingRepo.pageScale,
            fontScale = settingRepo.fontScale,
            blurIntensity = settingRepo.blurIntensity,
            enableBlur = if (isLiquidGlassInterface) false else settingRepo.enableBlur,
            enableFloatingBottomBar = settingRepo.enableFloatingBottomBar,
            enableFloatingBottomBarBlur = if (isLiquidGlassInterface) false else settingRepo.enableFloatingBottomBarBlur,
            uiMode = UiMode.fromValue(interfaceStyle),
            interfaceStyle = interfaceStyle,
            customWallpaperUri = settingRepo.customWallpaperUri,
            customWallpaperOpacity = settingRepo.customWallpaperOpacity,
            customWallpaperCrop = settingRepo.customWallpaperCrop,
            customWallpaperPassthroughEnabled = settingRepo.customWallpaperPassthroughEnabled,
            customWallpaperPassthroughOpacity = settingRepo.customWallpaperPassthroughOpacity,
            customStartupAnimationUri = settingRepo.customStartupAnimationUri,
            customStartupSoundUri = settingRepo.customStartupSoundUri,
        )
    }

    private companion object {
        val observedKeys = buildSet {
            add(THEME_SYNC_STRATEGY_KEY)
            addAll(ThemePreferenceKeys)
            InterfaceStyle.entries.forEach { style ->
                ThemePreferenceKeys.forEach { key ->
                    add("${key}_${style.value}")
                }
            }
            addAll(
                listOf(
            "ui_mode",
            "custom_wallpaper_uri",
            "custom_wallpaper_opacity",
            "custom_wallpaper_crop_left",
            "custom_wallpaper_crop_top",
            "custom_wallpaper_crop_right",
            "custom_wallpaper_crop_bottom",
            "custom_wallpaper_passthrough_enabled",
            "custom_wallpaper_passthrough_opacity",
            "custom_startup_animation_uri",
            "custom_startup_sound_uri",
                )
            )
        }
    }
}

private const val SELECTED_MAIN_PAGE_KEY = "selected_main_page"

private class MainPageState(
    private val savedStateHandle: SavedStateHandle,
) {
    val selectedPage: StateFlow<Int> = savedStateHandle.getStateFlow(SELECTED_MAIN_PAGE_KEY, 0)

    fun updateSelectedPage(page: Int) {
        savedStateHandle[SELECTED_MAIN_PAGE_KEY] = MainPagerConfig.coercePage(page)
    }
}

object MainPagerConfig {
    const val PAGE_COUNT = 4
    const val LAST_PAGE_INDEX = PAGE_COUNT - 1

    fun coercePage(page: Int): Int = page.coerceIn(0, LAST_PAGE_INDEX)
}
