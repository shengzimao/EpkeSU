package me.weishu.kernelsu.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.ImageSearch
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.weishu.kernelsu.R
import me.weishu.kernelsu.ui.InterfaceStyle
import me.weishu.kernelsu.ui.component.alpha.AlphaCard
import me.weishu.kernelsu.ui.component.alpha.AlphaColors
import me.weishu.kernelsu.ui.component.alpha.AlphaScreen
import me.weishu.kernelsu.ui.component.alpha.AlphaSwitch
import me.weishu.kernelsu.ui.component.alpha.alphaSp
import me.weishu.kernelsu.ui.util.MAX_CUSTOM_STARTUP_SOUND_DURATION_SECONDS
import me.weishu.kernelsu.ui.util.MAX_CUSTOM_WALLPAPER_OPACITY
import me.weishu.kernelsu.ui.util.MAX_CUSTOM_WALLPAPER_PASSTHROUGH_OPACITY
import me.weishu.kernelsu.ui.util.MIN_CUSTOM_STARTUP_SOUND_DURATION_SECONDS
import me.weishu.kernelsu.ui.util.MIN_CUSTOM_WALLPAPER_OPACITY
import me.weishu.kernelsu.ui.util.MIN_CUSTOM_WALLPAPER_PASSTHROUGH_OPACITY
import kotlin.math.roundToInt

@Composable
fun SettingPagerAlpha(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    bottomInnerPadding: Dp,
) {
    AlphaScreen(
        title = stringResource(R.string.settings),
        bottomInnerPadding = bottomInnerPadding,
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 16.dp,
                    top = 18.dp,
                    end = 16.dp,
                    bottom = contentPadding.calculateBottomPadding(),
                ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            AlphaSection(title = stringResource(R.string.settings_ui_mode)) {
                AlphaStylePicker(uiState = uiState, actions = actions)
            }

            AlphaSection(title = stringResource(R.string.settings_section_appearance)) {
                AlphaActionRow(
                    title = stringResource(R.string.settings_theme),
                    summary = stringResource(R.string.settings_theme_summary),
                    icon = Icons.Rounded.Palette,
                    onClick = actions.onOpenTheme,
                )
                AlphaActionRow(
                    title = stringResource(R.string.theme_store),
                    summary = stringResource(R.string.theme_store_settings_summary),
                    icon = Icons.Rounded.Storefront,
                    onClick = actions.onOpenThemeStore,
                )
                AlphaActionRow(
                    title = stringResource(R.string.settings_manager_name),
                    summary = if (uiState.customManagerName.isBlank()) {
                        stringResource(
                            R.string.settings_manager_name_default_summary,
                            stringResource(R.string.app_name)
                        )
                    } else {
                        stringResource(R.string.settings_manager_name_custom_summary, uiState.customManagerName)
                    },
                    icon = Icons.Rounded.EditNote,
                    onClick = actions.onEditCustomManagerName,
                )
                AlphaActionRow(
                    title = stringResource(R.string.settings_app_icon),
                    summary = stringResource(R.string.settings_app_icon_summary),
                    icon = Icons.Rounded.Apps,
                    onClick = actions.onOpenLauncherIcon,
                )
                AlphaActionRow(
                    title = stringResource(R.string.settings_wallpaper),
                    summary = stringResource(
                        if (uiState.customWallpaperUri == null) {
                            R.string.settings_wallpaper_summary
                        } else {
                            R.string.settings_wallpaper_selected_summary
                        }
                    ),
                    icon = Icons.Rounded.Wallpaper,
                    onClick = actions.onPickWallpaper,
                )
                if (uiState.customWallpaperUri != null) {
                    AlphaActionRow(
                        title = stringResource(R.string.settings_wallpaper_crop),
                        summary = stringResource(R.string.settings_wallpaper_crop_summary),
                        icon = Icons.Rounded.ImageSearch,
                        onClick = actions.onEditWallpaperCrop,
                    )
                    AlphaSliderRow(
                        title = stringResource(R.string.settings_wallpaper_opacity),
                        value = uiState.customWallpaperOpacity,
                        valueRange = MIN_CUSTOM_WALLPAPER_OPACITY..MAX_CUSTOM_WALLPAPER_OPACITY,
                        valueLabel = { "${(it * 100).roundToInt()}%" },
                        onValueChange = actions.onSetWallpaperOpacity,
                    )
                    AlphaSwitchRow(
                        title = stringResource(R.string.settings_wallpaper_passthrough),
                        summary = stringResource(R.string.settings_wallpaper_passthrough_summary),
                        checked = uiState.customWallpaperPassthroughEnabled,
                        onCheckedChange = actions.onSetWallpaperPassthroughEnabled,
                    )
                    if (uiState.customWallpaperPassthroughEnabled) {
                        AlphaSliderRow(
                            title = stringResource(R.string.settings_wallpaper_passthrough_opacity),
                            value = uiState.customWallpaperPassthroughOpacity,
                            valueRange = MIN_CUSTOM_WALLPAPER_PASSTHROUGH_OPACITY..MAX_CUSTOM_WALLPAPER_PASSTHROUGH_OPACITY,
                            valueLabel = { "${(it * 100).roundToInt()}%" },
                            onValueChange = actions.onSetWallpaperPassthroughOpacity,
                        )
                    }
                    AlphaActionRow(
                        title = stringResource(R.string.settings_wallpaper_preview),
                        summary = "",
                        icon = Icons.Rounded.Visibility,
                        onClick = actions.onPreviewWallpaper,
                    )
                    AlphaActionRow(
                        title = stringResource(R.string.settings_wallpaper_clear),
                        summary = "",
                        onClick = actions.onClearWallpaper,
                    )
                }
            }

            AlphaSection(title = stringResource(R.string.alpha_startup_media)) {
                AlphaActionRow(
                    title = stringResource(R.string.settings_startup_sound),
                    summary = stringResource(
                        if (uiState.customStartupSoundUri == null) {
                            R.string.settings_startup_sound_summary
                        } else {
                            R.string.settings_startup_sound_selected_summary
                        }
                    ),
                    icon = Icons.AutoMirrored.Rounded.VolumeUp,
                    onClick = actions.onPickStartupSound,
                )
                if (uiState.customStartupSoundUri != null) {
                    AlphaDurationRow(
                        value = uiState.customStartupSoundDurationSeconds,
                        onValueChange = actions.onSetStartupSoundDurationSeconds,
                    )
                    AlphaActionRow(
                        title = stringResource(R.string.settings_startup_sound_preview),
                        summary = "",
                        icon = Icons.AutoMirrored.Rounded.VolumeUp,
                        onClick = actions.onPreviewStartupSound,
                    )
                    AlphaActionRow(
                        title = stringResource(R.string.settings_startup_sound_clear),
                        summary = "",
                        onClick = actions.onClearStartupSound,
                    )
                }
                AlphaActionRow(
                    title = stringResource(R.string.settings_startup_animation),
                    summary = stringResource(
                        if (uiState.customStartupAnimationUri == null) {
                            R.string.settings_startup_animation_summary
                        } else {
                            R.string.settings_startup_animation_selected_summary
                        }
                    ),
                    icon = Icons.Rounded.PlayCircle,
                    onClick = actions.onPickStartupAnimation,
                )
                if (uiState.customStartupAnimationUri != null) {
                    AlphaActionRow(
                        title = stringResource(R.string.settings_startup_animation_preview),
                        summary = "",
                        icon = Icons.Rounded.Visibility,
                        onClick = actions.onPreviewStartupAnimation,
                    )
                    AlphaActionRow(
                        title = stringResource(R.string.settings_startup_animation_clear),
                        summary = "",
                        onClick = actions.onClearStartupAnimation,
                    )
                }
            }

            AlphaSection(title = stringResource(R.string.settings_section_updates)) {
                AlphaSwitchRow(
                    title = stringResource(R.string.settings_module_check_update),
                    summary = stringResource(R.string.settings_module_check_update_summary),
                    checked = uiState.checkModuleUpdate,
                    onCheckedChange = actions.onSetCheckModuleUpdate,
                )
            }

            AlphaSection(title = stringResource(R.string.settings_section_root_features)) {
                AlphaFeatureRows(uiState = uiState, actions = actions)
            }

            AlphaSection(title = stringResource(R.string.settings_section_advanced)) {
                AlphaActionRow(
                    title = stringResource(R.string.settings_profile_template),
                    summary = stringResource(R.string.settings_profile_template_summary),
                    onClick = actions.onOpenProfileTemplate,
                )
                AlphaSwitchRow(
                    title = stringResource(R.string.settings_umount_modules_default),
                    summary = stringResource(R.string.settings_umount_modules_default_summary),
                    checked = uiState.isDefaultUmountModules,
                    onCheckedChange = actions.onSetDefaultUmountModules,
                )
                AlphaSwitchRow(
                    title = stringResource(R.string.enable_web_debugging),
                    summary = stringResource(R.string.enable_web_debugging_summary),
                    checked = uiState.enableWebDebugging,
                    onCheckedChange = actions.onSetEnableWebDebugging,
                )
                AlphaSwitchRow(
                    title = stringResource(R.string.settings_auto_jailbreak),
                    summary = stringResource(R.string.settings_auto_jailbreak_summary),
                    checked = uiState.autoJailbreak,
                    enabled = uiState.isLateLoadMode,
                    onCheckedChange = actions.onSetAutoJailbreak,
                )
            }

            AlphaSection(title = stringResource(R.string.settings_section_maintenance)) {
                AlphaActionRow(
                    title = stringResource(R.string.about),
                    summary = "",
                    onClick = actions.onOpenAbout,
                )
            }
        }
    }
}

@Composable
private fun AlphaFeatureRows(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
) {
    AlphaSwitchRow(
        title = stringResource(R.string.settings_kernel_umount),
        summary = alphaFeatureSummary(uiState.kernelUmountStatus, R.string.settings_kernel_umount_summary),
        checked = uiState.isKernelUmountEnabled,
        enabled = uiState.kernelUmountStatus == "supported",
        onCheckedChange = actions.onSetKernelUmountEnabled,
    )
    AlphaSwitchRow(
        title = stringResource(R.string.settings_selinux_hide),
        summary = alphaFeatureSummary(uiState.selinuxHideStatus, R.string.settings_selinux_hide_summary),
        checked = uiState.isSelinuxHideEnabled,
        enabled = uiState.selinuxHideStatus == "supported",
        onCheckedChange = actions.onSetSelinuxHideEnabled,
    )
    AlphaSwitchRow(
        title = stringResource(R.string.settings_sulog),
        summary = alphaFeatureSummary(uiState.sulogStatus, R.string.settings_sulog_summary),
        checked = uiState.isSulogEnabled,
        enabled = uiState.sulogStatus == "supported",
        onCheckedChange = actions.onSetSulogEnabled,
    )
    AlphaSwitchRow(
        title = stringResource(R.string.settings_adb_root),
        summary = alphaFeatureSummary(uiState.adbRootStatus, R.string.settings_adb_root_summary),
        checked = uiState.isAdbRootEnabled,
        enabled = uiState.adbRootStatus == "supported",
        onCheckedChange = actions.onSetAdbRootEnabled,
    )
    AlphaSwitchRow(
        title = stringResource(R.string.settings_avc_spoof),
        summary = alphaFeatureSummary(uiState.avcSpoofStatus, R.string.settings_avc_spoof_summary),
        checked = uiState.isAvcSpoofEnabled,
        enabled = uiState.avcSpoofStatus == "supported",
        onCheckedChange = actions.onSetAvcSpoofEnabled,
    )
}

@Composable
private fun alphaFeatureSummary(status: String, defaultSummary: Int): String {
    return when (status) {
        "unsupported" -> stringResource(R.string.feature_status_unsupported_summary)
        "managed" -> stringResource(R.string.feature_status_managed_summary)
        else -> stringResource(defaultSummary)
    }
}

@Composable
private fun AlphaSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            color = AlphaColors.Text,
            fontSize = alphaSp(20f, maxScale = 1.04f),
            lineHeight = alphaSp(24f, maxScale = 1.04f),
            fontWeight = FontWeight.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 2.dp),
        )
        AlphaCard(contentPadding = PaddingValues(0.dp)) {
            Column(content = content)
        }
    }
}

@Composable
private fun AlphaStylePicker(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        InterfaceStyle.entries.forEachIndexed { index, style ->
            val selected = InterfaceStyle.selectedIndex(uiState.uiMode) == index
            Box(
                modifier = Modifier
                    .height(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selected) AlphaColors.Accent else AlphaColors.SurfaceStrong)
                    .clickable { actions.onSetUiModeIndex(index) }
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = style.label,
                    color = if (selected) Color.White else AlphaColors.Muted,
                    fontSize = alphaSp(13f, maxScale = 1.0f),
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun AlphaActionRow(
    title: String,
    summary: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AlphaColors.Accent,
                modifier = Modifier.size(22.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = AlphaColors.Text,
                fontSize = alphaSp(15f),
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (summary.isNotBlank()) {
                Text(
                    text = summary,
                    color = AlphaColors.Muted,
                    fontSize = alphaSp(12f),
                    lineHeight = alphaSp(15f),
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun AlphaSwitchRow(
    title: String,
    summary: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = if (enabled) AlphaColors.Text else AlphaColors.Disabled,
                fontSize = alphaSp(15f),
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (summary.isNotBlank()) {
                Text(
                    text = summary,
                    color = AlphaColors.Muted,
                    fontSize = alphaSp(12f),
                    lineHeight = alphaSp(15f),
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        AlphaSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
        )
    }
}

@Composable
private fun AlphaSliderRow(
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    valueLabel: @Composable (Float) -> String,
    onValueChange: (Float) -> Unit,
) {
    var sliderValue by remember(value) { mutableFloatStateOf(value) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                color = AlphaColors.Text,
                fontSize = alphaSp(15f),
                fontWeight = FontWeight.Black,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = valueLabel(sliderValue),
                color = AlphaColors.Muted,
                fontSize = alphaSp(12f),
                fontWeight = FontWeight.Bold,
            )
        }
        Slider(
            value = sliderValue,
            onValueChange = {
                sliderValue = it
                onValueChange(it)
            },
            valueRange = valueRange,
        )
    }
}

@Composable
private fun AlphaDurationRow(
    value: Int,
    onValueChange: (Int) -> Unit,
) {
    AlphaSliderRow(
        title = stringResource(R.string.settings_startup_sound_duration),
        value = value.toFloat(),
        valueRange = MIN_CUSTOM_STARTUP_SOUND_DURATION_SECONDS.toFloat()..
            MAX_CUSTOM_STARTUP_SOUND_DURATION_SECONDS.toFloat(),
        valueLabel = { stringResource(R.string.settings_startup_sound_duration_value, it.roundToInt()) },
        onValueChange = { onValueChange(it.roundToInt()) },
    )
}
