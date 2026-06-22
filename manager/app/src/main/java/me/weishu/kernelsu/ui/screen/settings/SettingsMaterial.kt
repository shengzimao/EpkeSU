package me.weishu.kernelsu.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Adb
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.Fence
import androidx.compose.material.icons.filled.FolderDelete
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.RemoveModerator
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.weishu.kernelsu.R
import me.weishu.kernelsu.ui.InterfaceStyle
import me.weishu.kernelsu.ui.component.KsuIsValid
import me.weishu.kernelsu.ui.component.material.SegmentedColumn
import me.weishu.kernelsu.ui.component.material.SegmentedDropdownItem
import me.weishu.kernelsu.ui.component.material.SegmentedListItem
import me.weishu.kernelsu.ui.component.material.SegmentedSwitchItem
import me.weishu.kernelsu.ui.component.material.SendLogBottomSheet
import me.weishu.kernelsu.ui.component.material.SnackBarHost
import me.weishu.kernelsu.ui.component.uninstalldialog.UninstallDialog
import me.weishu.kernelsu.ui.util.MAX_CUSTOM_WALLPAPER_OPACITY
import me.weishu.kernelsu.ui.util.MAX_CUSTOM_WALLPAPER_PASSTHROUGH_OPACITY
import me.weishu.kernelsu.ui.util.MAX_CUSTOM_STARTUP_SOUND_DURATION_SECONDS
import me.weishu.kernelsu.ui.util.MIN_CUSTOM_WALLPAPER_OPACITY
import me.weishu.kernelsu.ui.util.MIN_CUSTOM_WALLPAPER_PASSTHROUGH_OPACITY
import me.weishu.kernelsu.ui.util.MIN_CUSTOM_STARTUP_SOUND_DURATION_SECONDS
import kotlin.math.roundToInt

/**
 * @author weishu
 * @date 2023/1/1.
 */
@Composable
fun SettingPagerMaterial(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    bottomInnerPadding: Dp,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val snackBarHost = remember { SnackbarHostState() }
    val showUninstallDialog = rememberSaveable { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }

    UninstallDialog(
        show = showUninstallDialog.value,
        onDismissRequest = { showUninstallDialog.value = false }
    )

    Scaffold(
        topBar = {
            TopBar(scrollBehavior = scrollBehavior)
        },
        snackbarHost = { SnackBarHost(hostState = snackBarHost, modifier = Modifier.padding(bottom = bottomInnerPadding)) },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                SettingsMaterialContent(
                    uiState = uiState,
                    actions = actions,
                    bottomInnerPadding = bottomInnerPadding,
                    showUninstallDialog = { showUninstallDialog.value = true },
                    showBottomSheet = { showBottomSheet = true },
                )
            }
        }
    }

    if (showBottomSheet) {
        SendLogBottomSheet(
            onDismiss = { showBottomSheet = false },
            snackbarHostState = snackBarHost,
        )
    }
}

@Composable
private fun SettingsMaterialContent(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    bottomInnerPadding: Dp,
    showUninstallDialog: () -> Unit,
    showBottomSheet: () -> Unit,
) {
    Spacer(modifier = Modifier.height(8.dp))
    KsuIsValid {
        SegmentedColumn(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            title = stringResource(R.string.settings_section_updates),
            content = listOf {
                SegmentedSwitchItem(
                    icon = Icons.Rounded.UploadFile,
                    title = stringResource(id = R.string.settings_module_check_update),
                    summary = stringResource(id = R.string.settings_module_check_update_summary),
                    checked = uiState.checkModuleUpdate,
                    onCheckedChange = actions.onSetCheckModuleUpdate
                )
            }
        )
    }

    SegmentedColumn(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        title = stringResource(R.string.settings_section_appearance),
        content = buildList {
            add {
                SegmentedDropdownItem(
                    icon = Icons.Rounded.Dashboard,
                    title = stringResource(id = R.string.settings_ui_mode),
                    summary = stringResource(id = R.string.settings_ui_mode_summary),
                    items = InterfaceStyle.entries.map { it.label },
                    selectedIndex = InterfaceStyle.selectedIndex(uiState.uiMode),
                    onItemSelected = actions.onSetUiModeIndex
                )
            }
            add {
                SegmentedListItem(
                    onClick = actions.onOpenTheme,
                    headlineContent = { Text(stringResource(id = R.string.settings_theme)) },
                    supportingContent = { Text(stringResource(id = R.string.settings_theme_summary)) },
                    leadingContent = { Icon(Icons.Filled.Palette, stringResource(id = R.string.settings_theme)) },
                    trailingContent = {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            null
                        )
                    }
                )
            }
            add {
                SegmentedListItem(
                    onClick = actions.onOpenThemeStore,
                    headlineContent = { Text(stringResource(id = R.string.theme_store)) },
                    supportingContent = { Text(stringResource(id = R.string.theme_store_settings_summary)) },
                    leadingContent = { Icon(Icons.Filled.Storefront, stringResource(id = R.string.theme_store)) },
                    trailingContent = {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            null
                        )
                    }
                )
            }
            add {
                val defaultName = stringResource(id = R.string.app_name)
                val summary = if (uiState.customManagerName.isBlank()) {
                    stringResource(id = R.string.settings_manager_name_default_summary, defaultName)
                } else {
                    stringResource(id = R.string.settings_manager_name_custom_summary, uiState.customManagerName)
                }
                SegmentedListItem(
                    onClick = actions.onEditCustomManagerName,
                    headlineContent = { Text(stringResource(id = R.string.settings_manager_name)) },
                    supportingContent = { Text(summary) },
                    leadingContent = {
                        Icon(Icons.Filled.EditNote, stringResource(id = R.string.settings_manager_name))
                    },
                    trailingContent = {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            null
                        )
                    }
                )
            }
            add {
                SegmentedListItem(
                    onClick = actions.onOpenLauncherIcon,
                    headlineContent = { Text(stringResource(id = R.string.settings_app_icon)) },
                    supportingContent = { Text(stringResource(id = R.string.settings_app_icon_summary)) },
                    leadingContent = { Icon(Icons.Filled.Apps, stringResource(id = R.string.settings_app_icon)) },
                    trailingContent = {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            null
                        )
                    }
                )
            }
            add {
                SegmentedListItem(
                    onClick = actions.onPickWallpaper,
                    headlineContent = { Text(stringResource(id = R.string.settings_wallpaper)) },
                    supportingContent = {
                        Text(
                            stringResource(
                                if (uiState.customWallpaperUri == null) {
                                    R.string.settings_wallpaper_summary
                                } else {
                                    R.string.settings_wallpaper_selected_summary
                                }
                            )
                        )
                    },
                    leadingContent = { Icon(Icons.Filled.Wallpaper, stringResource(id = R.string.settings_wallpaper)) },
                    trailingContent = {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            null
                        )
                    }
                )
            }
            if (uiState.customWallpaperUri != null) {
                add {
                    SegmentedListItem(
                        onClick = actions.onEditWallpaperCrop,
                        headlineContent = { Text(stringResource(id = R.string.settings_wallpaper_crop)) },
                        supportingContent = { Text(stringResource(id = R.string.settings_wallpaper_crop_summary)) },
                        leadingContent = {
                            Icon(
                                Icons.Filled.ImageSearch,
                                stringResource(id = R.string.settings_wallpaper_crop)
                            )
                        },
                    )
                }
                add {
                    WallpaperOpacityMaterialItem(
                        opacity = uiState.customWallpaperOpacity,
                        title = stringResource(id = R.string.settings_wallpaper_opacity),
                        summary = stringResource(id = R.string.settings_wallpaper_opacity_summary),
                        onOpacityChange = actions.onSetWallpaperOpacity,
                    )
                }
                add {
                    SegmentedSwitchItem(
                        icon = Icons.Filled.Layers,
                        title = stringResource(id = R.string.settings_wallpaper_passthrough),
                        summary = stringResource(id = R.string.settings_wallpaper_passthrough_summary),
                        checked = uiState.customWallpaperPassthroughEnabled,
                        onCheckedChange = actions.onSetWallpaperPassthroughEnabled,
                    )
                }
                if (uiState.customWallpaperPassthroughEnabled) {
                    add {
                        WallpaperOpacityMaterialItem(
                            opacity = uiState.customWallpaperPassthroughOpacity,
                            title = stringResource(id = R.string.settings_wallpaper_passthrough_opacity),
                            summary = stringResource(id = R.string.settings_wallpaper_passthrough_opacity_summary),
                            range = MIN_CUSTOM_WALLPAPER_PASSTHROUGH_OPACITY..MAX_CUSTOM_WALLPAPER_PASSTHROUGH_OPACITY,
                            onOpacityChange = actions.onSetWallpaperPassthroughOpacity,
                        )
                    }
                }
                add {
                    SegmentedListItem(
                        onClick = actions.onPreviewWallpaper,
                        headlineContent = { Text(stringResource(id = R.string.settings_wallpaper_preview)) },
                        leadingContent = {
                            Icon(
                                Icons.Filled.Visibility,
                                stringResource(id = R.string.settings_wallpaper_preview)
                            )
                        },
                    )
                }
                add {
                    SegmentedListItem(
                        onClick = actions.onClearWallpaper,
                        headlineContent = { Text(stringResource(id = R.string.settings_wallpaper_clear)) },
                        leadingContent = {
                            Icon(
                                Icons.Filled.Close,
                                stringResource(id = R.string.settings_wallpaper_clear)
                            )
                        },
                    )
                }
            }
            add {
                SegmentedListItem(
                    onClick = actions.onPickStartupSound,
                    headlineContent = { Text(stringResource(id = R.string.settings_startup_sound)) },
                    supportingContent = {
                        Text(
                            stringResource(
                                if (uiState.customStartupSoundUri == null) {
                                    R.string.settings_startup_sound_summary
                                } else {
                                    R.string.settings_startup_sound_selected_summary
                                }
                            )
                        )
                    },
                    leadingContent = {
                        Icon(
                            Icons.AutoMirrored.Filled.VolumeUp,
                            stringResource(id = R.string.settings_startup_sound)
                        )
                    },
                    trailingContent = {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            null
                        )
                    }
                )
            }
            add {
                SegmentedListItem(
                    onClick = actions.onPickStartupAnimation,
                    headlineContent = { Text(stringResource(id = R.string.settings_startup_animation)) },
                    supportingContent = {
                        Text(
                            stringResource(
                                if (uiState.customStartupAnimationUri == null) {
                                    R.string.settings_startup_animation_summary
                                } else {
                                    R.string.settings_startup_animation_selected_summary
                                }
                            )
                        )
                    },
                    leadingContent = {
                        Icon(Icons.Filled.PlayCircle, stringResource(id = R.string.settings_startup_animation))
                    },
                    trailingContent = {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            null
                        )
                    }
                )
            }
            if (uiState.customStartupAnimationUri != null) {
                add {
                    SegmentedListItem(
                        onClick = actions.onPreviewStartupAnimation,
                        headlineContent = { Text(stringResource(id = R.string.settings_startup_animation_preview)) },
                        leadingContent = {
                            Icon(
                                Icons.Filled.Visibility,
                                stringResource(id = R.string.settings_startup_animation_preview)
                            )
                        },
                    )
                }
                add {
                    SegmentedListItem(
                        onClick = actions.onClearStartupAnimation,
                        headlineContent = { Text(stringResource(id = R.string.settings_startup_animation_clear)) },
                        leadingContent = {
                            Icon(
                                Icons.Filled.Close,
                                stringResource(id = R.string.settings_startup_animation_clear)
                            )
                        },
                    )
                }
            }
            if (uiState.customStartupSoundUri != null) {
                add {
                    StartupSoundDurationMaterialItem(
                        durationSeconds = uiState.customStartupSoundDurationSeconds,
                        onDurationChange = actions.onSetStartupSoundDurationSeconds,
                    )
                }
                add {
                    SegmentedListItem(
                        onClick = actions.onPreviewStartupSound,
                        headlineContent = { Text(stringResource(id = R.string.settings_startup_sound_preview)) },
                        leadingContent = {
                            Icon(
                                Icons.AutoMirrored.Filled.VolumeUp,
                                stringResource(id = R.string.settings_startup_sound_preview)
                            )
                        },
                    )
                }
                add {
                    SegmentedListItem(
                        onClick = actions.onClearStartupSound,
                        headlineContent = { Text(stringResource(id = R.string.settings_startup_sound_clear)) },
                        leadingContent = {
                            Icon(
                                Icons.Filled.Close,
                                stringResource(id = R.string.settings_startup_sound_clear)
                            )
                        },
                    )
                }
            }
        }
    )

    val profileTemplate = stringResource(id = R.string.settings_profile_template)
    KsuIsValid {
        SegmentedColumn(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            title = stringResource(R.string.settings_section_profiles),
            content = listOf {
                SegmentedListItem(
                    onClick = actions.onOpenProfileTemplate,
                    headlineContent = { Text(profileTemplate) },
                    supportingContent = { Text(stringResource(id = R.string.settings_profile_template_summary)) },
                    leadingContent = { Icon(Icons.Filled.Fence, profileTemplate) },
                    trailingContent = {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            null
                        )
                    }
                )
            }
        )
    }

    KsuIsValid {
        val suCompatModeItems = listOf(
            stringResource(id = R.string.settings_mode_enable_by_default),
            stringResource(id = R.string.settings_mode_disable_until_reboot),
            stringResource(id = R.string.settings_mode_disable_always),
        )

        SegmentedColumn(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            title = stringResource(R.string.settings_section_root_features),
            content = listOf(
                {
                    val suSummary = when (uiState.suCompatStatus) {
                        "unsupported" -> stringResource(id = R.string.feature_status_unsupported_summary)
                        "managed" -> stringResource(id = R.string.feature_status_managed_summary)
                        else -> stringResource(id = R.string.settings_sucompat_summary)
                    }
                    SegmentedDropdownItem(
                        icon = Icons.Filled.RemoveModerator,
                        title = stringResource(id = R.string.settings_sucompat),
                        summary = suSummary,
                        items = suCompatModeItems,
                        enabled = uiState.suCompatStatus == "supported",
                        selectedIndex = uiState.suCompatMode,
                        onItemSelected = actions.onSetSuCompatMode
                    )
                },
                {
                    val umountSummary = when (uiState.kernelUmountStatus) {
                        "unsupported" -> stringResource(id = R.string.feature_status_unsupported_summary)
                        "managed" -> stringResource(id = R.string.feature_status_managed_summary)
                        else -> stringResource(id = R.string.settings_kernel_umount_summary)
                    }
                    SegmentedSwitchItem(
                        icon = Icons.Filled.RemoveCircle,
                        title = stringResource(id = R.string.settings_kernel_umount),
                        summary = umountSummary,
                        enabled = uiState.kernelUmountStatus == "supported",
                        checked = uiState.isKernelUmountEnabled,
                        onCheckedChange = actions.onSetKernelUmountEnabled
                    )
                },
                {
                    val selinuxHideSummary = when (uiState.selinuxHideStatus) {
                        "unsupported" -> stringResource(id = R.string.feature_status_unsupported_summary)
                        "managed" -> stringResource(id = R.string.feature_status_managed_summary)
                        else -> stringResource(id = R.string.settings_selinux_hide_summary)
                    }
                    SegmentedSwitchItem(
                        icon = Icons.Filled.Policy,
                        title = stringResource(id = R.string.settings_selinux_hide),
                        summary = selinuxHideSummary,
                        enabled = uiState.selinuxHideStatus == "supported",
                        checked = uiState.isSelinuxHideEnabled,
                        onCheckedChange = actions.onSetSelinuxHideEnabled
                    )
                },
                {
                    val sulogSummary = when (uiState.sulogStatus) {
                        "unsupported" -> stringResource(id = R.string.feature_status_unsupported_summary)
                        "managed" -> stringResource(id = R.string.feature_status_managed_summary)
                        else -> stringResource(id = R.string.settings_sulog_summary)
                    }
                    SegmentedSwitchItem(
                        icon = Icons.AutoMirrored.Filled.Article,
                        title = stringResource(id = R.string.settings_sulog),
                        summary = sulogSummary,
                        enabled = uiState.sulogStatus == "supported",
                        checked = uiState.isSulogEnabled,
                        onCheckedChange = actions.onSetSulogEnabled
                    )
                },
                {
                    val adbRootSummary = when (uiState.adbRootStatus) {
                        "unsupported" -> stringResource(id = R.string.feature_status_unsupported_summary)
                        "managed" -> stringResource(id = R.string.feature_status_managed_summary)
                        else -> stringResource(id = R.string.settings_adb_root_summary)
                    }
                    SegmentedSwitchItem(
                        icon = Icons.Filled.Adb,
                        title = stringResource(id = R.string.settings_adb_root),
                        summary = adbRootSummary,
                        enabled = uiState.adbRootStatus == "supported",
                        checked = uiState.isAdbRootEnabled,
                        onCheckedChange = actions.onSetAdbRootEnabled
                    )
                },
                {
                    val avcSpoofSummary = when (uiState.avcSpoofStatus) {
                        "unsupported" -> stringResource(id = R.string.feature_status_unsupported_summary)
                        "managed" -> stringResource(id = R.string.feature_status_managed_summary)
                        else -> stringResource(id = R.string.settings_avc_spoof_summary)
                    }
                    SegmentedSwitchItem(
                        icon = Icons.Filled.EditNote,
                        title = stringResource(id = R.string.settings_avc_spoof),
                        summary = avcSpoofSummary,
                        enabled = uiState.avcSpoofStatus == "supported",
                        checked = uiState.isAvcSpoofEnabled,
                        onCheckedChange = actions.onSetAvcSpoofEnabled
                    )
                },
            )
        )

        SegmentedColumn(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            title = stringResource(R.string.settings_section_advanced),
            content = listOf(
                {
                    SegmentedSwitchItem(
                        icon = Icons.Filled.FolderDelete,
                        title = stringResource(id = R.string.settings_umount_modules_default),
                        summary = stringResource(id = R.string.settings_umount_modules_default_summary),
                        checked = uiState.isDefaultUmountModules,
                        onCheckedChange = actions.onSetDefaultUmountModules
                    )
                },
                {
                    SegmentedSwitchItem(
                        icon = Icons.Filled.DeveloperMode,
                        title = stringResource(id = R.string.enable_web_debugging),
                        summary = stringResource(id = R.string.enable_web_debugging_summary),
                        checked = uiState.enableWebDebugging,
                        onCheckedChange = actions.onSetEnableWebDebugging
                    )
                },
                {
                    SegmentedSwitchItem(
                        icon = Icons.Filled.ElectricalServices,
                        title = stringResource(id = R.string.settings_auto_jailbreak),
                        summary = stringResource(id = R.string.settings_auto_jailbreak_summary),
                        enabled = uiState.isLateLoadMode,
                        checked = uiState.autoJailbreak,
                        onCheckedChange = actions.onSetAutoJailbreak
                    )
                }
            )
        )
    }

    if (uiState.isLkmMode) {
        SegmentedColumn(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            title = stringResource(R.string.settings_section_maintenance),
            content = listOf(
                {
                    val uninstall = stringResource(id = R.string.settings_uninstall)
                    SegmentedListItem(
                        onClick = showUninstallDialog,
                        enabled = !uiState.isLateLoadMode,
                        headlineContent = { Text(uninstall) },
                        leadingContent = { Icon(Icons.Filled.Delete, uninstall) }
                    )
                }
            )
        )
    }

    SegmentedColumn(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        title = if (uiState.isLkmMode) "" else stringResource(R.string.settings_section_maintenance),
        content = listOf(
            {
                SegmentedListItem(
                    onClick = showBottomSheet,
                    headlineContent = { Text(stringResource(id = R.string.send_log)) },
                    leadingContent = {
                        Icon(
                            Icons.Filled.BugReport,
                            stringResource(id = R.string.send_log)
                        )
                    },
                )
            },
            {
                SegmentedListItem(
                    onClick = actions.onOpenAbout,
                    headlineContent = { Text(stringResource(id = R.string.about)) },
                    leadingContent = {
                        Icon(
                            Icons.Filled.ContactPage,
                            stringResource(id = R.string.about)
                        )
                    },
                )
            }
        )
    )
    Spacer(modifier = Modifier.height(8.dp))
    Spacer(modifier = Modifier.height(bottomInnerPadding))
}

@Composable
private fun WallpaperOpacityMaterialItem(
    opacity: Float,
    title: String,
    summary: String,
    range: ClosedFloatingPointRange<Float> = MIN_CUSTOM_WALLPAPER_OPACITY..MAX_CUSTOM_WALLPAPER_OPACITY,
    onOpacityChange: (Float) -> Unit,
) {
    var sliderValue by remember(opacity) { mutableFloatStateOf(opacity) }

    SegmentedListItem(
        headlineContent = { Text(title) },
        supportingContent = {
            Column {
                Text(summary)
                Slider(
                    value = sliderValue,
                    onValueChange = {
                        sliderValue = it
                        onOpacityChange(it)
                    },
                    valueRange = range,
                )
            }
        },
        leadingContent = {
            Icon(Icons.Filled.Wallpaper, title)
        },
        trailingContent = {
            Text(
                text = "${(sliderValue * 100).roundToInt()}%",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
    )
}

@Composable
private fun StartupSoundDurationMaterialItem(
    durationSeconds: Int,
    onDurationChange: (Int) -> Unit,
) {
    var sliderValue by remember(durationSeconds) { mutableFloatStateOf(durationSeconds.toFloat()) }
    val currentSeconds = sliderValue.roundToInt()

    SegmentedListItem(
        headlineContent = { Text(stringResource(id = R.string.settings_startup_sound_duration)) },
        supportingContent = {
            Column {
                Text(stringResource(id = R.string.settings_startup_sound_duration_summary))
                Slider(
                    value = sliderValue,
                    onValueChange = {
                        sliderValue = it
                        onDurationChange(it.roundToInt())
                    },
                    valueRange = MIN_CUSTOM_STARTUP_SOUND_DURATION_SECONDS.toFloat()..
                        MAX_CUSTOM_STARTUP_SOUND_DURATION_SECONDS.toFloat(),
                    steps = MAX_CUSTOM_STARTUP_SOUND_DURATION_SECONDS -
                        MIN_CUSTOM_STARTUP_SOUND_DURATION_SECONDS - 1,
                )
            }
        },
        leadingContent = {
            Icon(Icons.Filled.Timer, stringResource(id = R.string.settings_startup_sound_duration))
        },
        trailingContent = {
            Text(
                text = stringResource(id = R.string.settings_startup_sound_duration_value, currentSeconds),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
    )
}

@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    LargeFlexibleTopAppBar(
        title = { Text(stringResource(R.string.settings)) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        ),
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
        scrollBehavior = scrollBehavior
    )
}
