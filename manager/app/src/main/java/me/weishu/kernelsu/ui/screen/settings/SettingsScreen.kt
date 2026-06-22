package me.weishu.kernelsu.ui.screen.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import android.widget.Toast
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import me.weishu.kernelsu.R
import me.weishu.kernelsu.ui.InterfaceStyle
import me.weishu.kernelsu.ui.LocalInterfaceStyle
import me.weishu.kernelsu.ui.LocalUiMode
import me.weishu.kernelsu.ui.UiMode
import me.weishu.kernelsu.ui.component.StartupAnimationOverlay
import me.weishu.kernelsu.ui.navigation3.Navigator
import me.weishu.kernelsu.ui.navigation3.Route
import me.weishu.kernelsu.ui.util.CUSTOM_WALLPAPER_URI_KEY
import me.weishu.kernelsu.ui.util.persistCustomImageReference
import me.weishu.kernelsu.ui.util.releasePersistableStartupAnimationReadPermission
import me.weishu.kernelsu.ui.util.releasePersistableAudioReadPermission
import me.weishu.kernelsu.ui.util.StartupSoundPlayer
import me.weishu.kernelsu.ui.util.isCustomStartupAnimationVideo
import me.weishu.kernelsu.ui.util.takePersistableStartupAnimationReadPermission
import me.weishu.kernelsu.ui.util.takePersistableAudioReadPermission
import me.weishu.kernelsu.ui.util.takePersistableImageReadPermission
import me.weishu.kernelsu.ui.viewmodel.SettingsViewModel

@Composable
fun SettingPager(
    navigator: Navigator,
    bottomInnerPadding: Dp
) {
    val context = LocalContext.current
    val viewModel = viewModel<SettingsViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val showManagerNameDialog = rememberSaveable { mutableStateOf(false) }
    val showWallpaperPreview = rememberSaveable { mutableStateOf(false) }
    val showWallpaperCropEditor = rememberSaveable { mutableStateOf(false) }
    val showStartupAnimationPreview = rememberSaveable { mutableStateOf(false) }
    val startupAnimationPreviewUri = rememberSaveable { mutableStateOf<String?>(null) }
    val wallpaperLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        val uriString = persistCustomImageReference(context, uri, CUSTOM_WALLPAPER_URI_KEY)
            ?: uri.toString().also { takePersistableImageReadPermission(context, uri) }
        viewModel.setCustomWallpaperUri(uriString)
        showWallpaperCropEditor.value = true
    }
    val startupSoundLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        StartupSoundPlayer.clearAutoPlaySuppression()
        uri ?: return@rememberLauncherForActivityResult
        takePersistableAudioReadPermission(context, uri)
        viewModel.setCustomStartupSoundUri(uri.toString())
        StartupSoundPlayer.play(context, uri.toString()) {
            Toast.makeText(context, R.string.settings_startup_sound_play_failed, Toast.LENGTH_SHORT).show()
        }
    }
    val startupAnimationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        takePersistableStartupAnimationReadPermission(context, uri)
        val uriString = uri.toString()
        viewModel.setCustomStartupAnimationUri(uriString)
        if (!isCustomStartupAnimationVideo(context, uri)) {
            startupAnimationPreviewUri.value = uriString
            showStartupAnimationPreview.value = true
        }
    }

    LifecycleResumeEffect(Unit) {
        viewModel.refresh()
        onPauseOrDispose { }
    }

    val actions = SettingsScreenActions(
        onSetCheckModuleUpdate = viewModel::setCheckModuleUpdate,
        onOpenTheme = { navigator.push(Route.ColorPalette) },
        onOpenThemeStore = { navigator.push(Route.ThemeStore) },
        onSetUiModeIndex = { index ->
            viewModel.setUiMode(InterfaceStyle.fromIndex(index).value)
        },
        onOpenLauncherIcon = { navigator.push(Route.LauncherIcon) },
        onEditCustomManagerName = { showManagerNameDialog.value = true },
        onSetCustomManagerName = viewModel::setCustomManagerName,
        onPickWallpaper = { wallpaperLauncher.launch(arrayOf("image/*")) },
        onPreviewWallpaper = { showWallpaperPreview.value = true },
        onEditWallpaperCrop = { showWallpaperCropEditor.value = true },
        onClearWallpaper = {
            viewModel.clearCustomWallpaper()
            showWallpaperPreview.value = false
            showWallpaperCropEditor.value = false
        },
        onSetWallpaperOpacity = viewModel::setCustomWallpaperOpacity,
        onSetWallpaperCrop = viewModel::setCustomWallpaperCrop,
        onSetWallpaperPassthroughEnabled = viewModel::setCustomWallpaperPassthroughEnabled,
        onSetWallpaperPassthroughOpacity = viewModel::setCustomWallpaperPassthroughOpacity,
        onSaveCustomThemePreset = viewModel::saveCustomThemePreset,
        onApplyCustomThemePreset = viewModel::applyCustomThemePreset,
        onRenameCustomThemePreset = viewModel::renameCustomThemePreset,
        onDeleteCustomThemePreset = viewModel::deleteCustomThemePreset,
        onSetThemeSyncStrategy = viewModel::setThemeSyncStrategy,
        onResetThemeToDefault = viewModel::resetThemeToDefault,
        onPickStartupAnimation = {
            startupAnimationLauncher.launch(arrayOf("image/*", "video/*"))
        },
        onPreviewStartupAnimation = {
            uiState.customStartupAnimationUri?.let { uri ->
                startupAnimationPreviewUri.value = uri
                showStartupAnimationPreview.value = true
            }
        },
        onClearStartupAnimation = {
            releasePersistableStartupAnimationReadPermission(context, uiState.customStartupAnimationUri)
            viewModel.clearCustomStartupAnimation()
            showStartupAnimationPreview.value = false
            startupAnimationPreviewUri.value = null
        },
        onPickStartupSound = {
            StartupSoundPlayer.suppressNextAutoPlay()
            startupSoundLauncher.launch(arrayOf("audio/*"))
        },
        onPreviewStartupSound = {
            StartupSoundPlayer.play(context, uiState.customStartupSoundUri) {
                Toast.makeText(context, R.string.settings_startup_sound_play_failed, Toast.LENGTH_SHORT).show()
            }
        },
        onClearStartupSound = {
            StartupSoundPlayer.stop()
            releasePersistableAudioReadPermission(context, uiState.customStartupSoundUri)
            viewModel.clearCustomStartupSound()
        },
        onSetStartupSoundDurationSeconds = viewModel::setCustomStartupSoundDurationSeconds,
        onOpenProfileTemplate = { navigator.push(Route.AppProfileTemplate) },
        onSetSuCompatMode = viewModel::setSuCompatMode,
        onSetKernelUmountEnabled = viewModel::setKernelUmountEnabled,
        onSetSelinuxHideEnabled = viewModel::setSelinuxHideEnabled,
        onSetSulogEnabled = viewModel::setSulogEnabled,
        onSetAdbRootEnabled = viewModel::setAdbRootEnabled,
        onSetAvcSpoofEnabled = viewModel::setAvcSpoofEnabled,
        onSetDefaultUmountModules = viewModel::setDefaultUmountModules,
        onSetEnableWebDebugging = viewModel::setEnableWebDebugging,
        onSetAutoJailbreak = viewModel::setAutoJailbreak,
        onOpenAbout = { navigator.push(Route.About) },
    )

    Box {
        when (LocalInterfaceStyle.current) {
            InterfaceStyle.Skrootpro.value -> SettingPagerSkrootpro(uiState, actions, bottomInnerPadding)
            InterfaceStyle.Alpha.value -> SettingPagerAlpha(uiState, actions, bottomInnerPadding)
            else -> {
                when (LocalUiMode.current) {
                    UiMode.Miuix -> SettingPagerMiuix(uiState, actions, bottomInnerPadding)
                    UiMode.Material -> SettingPagerMaterial(uiState, actions, bottomInnerPadding)
                }
            }
        }

        if (showStartupAnimationPreview.value && !startupAnimationPreviewUri.value.isNullOrBlank()) {
            StartupAnimationOverlay(
                uriString = startupAnimationPreviewUri.value,
                onFinished = {
                    showStartupAnimationPreview.value = false
                    startupAnimationPreviewUri.value = null
                },
                onError = {
                    showStartupAnimationPreview.value = false
                    startupAnimationPreviewUri.value = null
                    Toast.makeText(context, R.string.settings_startup_animation_play_failed, Toast.LENGTH_SHORT).show()
                },
            )
        }
    }

    ManagerNameDialog(
        show = showManagerNameDialog.value,
        initialName = uiState.customManagerName,
        onDismissRequest = { showManagerNameDialog.value = false },
        onConfirm = actions.onSetCustomManagerName,
    )
    SettingsWallpaperPreviewDialog(
        show = showWallpaperPreview.value,
        uriString = uiState.customWallpaperUri,
        opacity = uiState.customWallpaperOpacity,
        crop = uiState.customWallpaperCrop,
        passthroughEnabled = uiState.customWallpaperPassthroughEnabled,
        passthroughOpacity = uiState.customWallpaperPassthroughOpacity,
        onDismissRequest = { showWallpaperPreview.value = false },
    )
    SettingsWallpaperCropDialog(
        show = showWallpaperCropEditor.value,
        uriString = uiState.customWallpaperUri,
        crop = uiState.customWallpaperCrop,
        onCropChange = {
            actions.onSetWallpaperCrop(it)
            showWallpaperPreview.value = true
        },
        onDismissRequest = {
            showWallpaperCropEditor.value = false
        },
    )
}
