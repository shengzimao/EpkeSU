package me.weishu.kernelsu.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Switch
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
import me.weishu.kernelsu.ui.component.skrootpro.SkrootproColors
import me.weishu.kernelsu.ui.component.skrootpro.SkrootproDivider
import me.weishu.kernelsu.ui.component.skrootpro.SkrootproScreen
import me.weishu.kernelsu.ui.component.skrootpro.SkrootproSectionTitle
import me.weishu.kernelsu.ui.component.skrootpro.skrootproSp
import me.weishu.kernelsu.ui.util.MAX_CUSTOM_WALLPAPER_OPACITY
import me.weishu.kernelsu.ui.util.MAX_CUSTOM_WALLPAPER_PASSTHROUGH_OPACITY
import me.weishu.kernelsu.ui.util.MAX_CUSTOM_STARTUP_SOUND_DURATION_SECONDS
import me.weishu.kernelsu.ui.util.MIN_CUSTOM_WALLPAPER_OPACITY
import me.weishu.kernelsu.ui.util.MIN_CUSTOM_WALLPAPER_PASSTHROUGH_OPACITY
import me.weishu.kernelsu.ui.util.MIN_CUSTOM_STARTUP_SOUND_DURATION_SECONDS
import kotlin.math.roundToInt

@Composable
fun SettingPagerSkrootpro(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    bottomInnerPadding: Dp,
) {
    SkrootproScreen(
        title = stringResource(R.string.skrootpro_title),
        bottomInnerPadding = bottomInnerPadding,
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
                .padding(horizontal = 18.dp, vertical = 10.dp),
        ) {
            SkrootproSectionTitle(stringResource(R.string.settings_ui_mode))
            SkrootproStylePicker(uiState = uiState, actions = actions)
            SkrootproDivider(modifier = Modifier.padding(vertical = 18.dp))

            SkrootproSectionTitle(stringResource(R.string.settings_section_appearance))
            SkrootproActionRow(
                title = stringResource(R.string.settings_theme),
                summary = stringResource(R.string.settings_theme_summary),
                onClick = actions.onOpenTheme,
            )
            SkrootproActionRow(
                title = stringResource(R.string.theme_store),
                summary = stringResource(R.string.theme_store_settings_summary),
                onClick = actions.onOpenThemeStore,
            )
            SkrootproActionRow(
                title = stringResource(R.string.settings_manager_name),
                summary = if (uiState.customManagerName.isBlank()) {
                    stringResource(
                        R.string.settings_manager_name_default_summary,
                        stringResource(R.string.app_name)
                    )
                } else {
                    stringResource(R.string.settings_manager_name_custom_summary, uiState.customManagerName)
                },
                leadingIcon = Icons.Rounded.EditNote,
                onClick = actions.onEditCustomManagerName,
            )
            SkrootproActionRow(
                title = stringResource(R.string.settings_app_icon),
                summary = stringResource(R.string.settings_app_icon_summary),
                leadingIcon = Icons.Rounded.Apps,
                onClick = actions.onOpenLauncherIcon,
            )
            SkrootproActionRow(
                title = stringResource(R.string.settings_wallpaper),
                summary = stringResource(
                    if (uiState.customWallpaperUri == null) {
                        R.string.settings_wallpaper_summary
                    } else {
                        R.string.settings_wallpaper_selected_summary
                    }
                ),
                onClick = actions.onPickWallpaper,
            )
            if (uiState.customWallpaperUri != null) {
                SkrootproActionRow(
                    title = stringResource(R.string.settings_wallpaper_crop),
                    summary = stringResource(R.string.settings_wallpaper_crop_summary),
                    onClick = actions.onEditWallpaperCrop,
                )
                SkrootproSliderRow(
                    title = stringResource(R.string.settings_wallpaper_opacity),
                    value = uiState.customWallpaperOpacity,
                    onValueChange = actions.onSetWallpaperOpacity,
                )
                SkrootproSwitchRow(
                    title = stringResource(R.string.settings_wallpaper_passthrough),
                    checked = uiState.customWallpaperPassthroughEnabled,
                    onCheckedChange = actions.onSetWallpaperPassthroughEnabled,
                )
                if (uiState.customWallpaperPassthroughEnabled) {
                    SkrootproSliderRow(
                        title = stringResource(R.string.settings_wallpaper_passthrough_opacity),
                        value = uiState.customWallpaperPassthroughOpacity,
                        valueRange = MIN_CUSTOM_WALLPAPER_PASSTHROUGH_OPACITY..MAX_CUSTOM_WALLPAPER_PASSTHROUGH_OPACITY,
                        onValueChange = actions.onSetWallpaperPassthroughOpacity,
                    )
                }
                SkrootproActionRow(
                    title = stringResource(R.string.settings_wallpaper_preview),
                    summary = "",
                    onClick = actions.onPreviewWallpaper,
                )
                SkrootproActionRow(
                    title = stringResource(R.string.settings_wallpaper_clear),
                    summary = "",
                    onClick = actions.onClearWallpaper,
                )
            }
            SkrootproActionRow(
                title = stringResource(R.string.settings_startup_sound),
                summary = stringResource(
                    if (uiState.customStartupSoundUri == null) {
                        R.string.settings_startup_sound_summary
                    } else {
                        R.string.settings_startup_sound_selected_summary
                    }
                ),
                leadingIcon = Icons.AutoMirrored.Rounded.VolumeUp,
                onClick = actions.onPickStartupSound,
            )
            SkrootproActionRow(
                title = stringResource(R.string.settings_startup_animation),
                summary = stringResource(
                    if (uiState.customStartupAnimationUri == null) {
                        R.string.settings_startup_animation_summary
                    } else {
                        R.string.settings_startup_animation_selected_summary
                    }
                ),
                leadingIcon = Icons.Rounded.PlayCircle,
                onClick = actions.onPickStartupAnimation,
            )
            if (uiState.customStartupAnimationUri != null) {
                SkrootproActionRow(
                    title = stringResource(R.string.settings_startup_animation_preview),
                    summary = "",
                    leadingIcon = Icons.Rounded.Visibility,
                    onClick = actions.onPreviewStartupAnimation,
                )
                SkrootproActionRow(
                    title = stringResource(R.string.settings_startup_animation_clear),
                    summary = "",
                    onClick = actions.onClearStartupAnimation,
                )
            }
            if (uiState.customStartupSoundUri != null) {
                SkrootproDurationSliderRow(
                    title = stringResource(R.string.settings_startup_sound_duration),
                    value = uiState.customStartupSoundDurationSeconds,
                    onValueChange = actions.onSetStartupSoundDurationSeconds,
                )
                SkrootproActionRow(
                    title = stringResource(R.string.settings_startup_sound_preview),
                    summary = "",
                    leadingIcon = Icons.AutoMirrored.Rounded.VolumeUp,
                    onClick = actions.onPreviewStartupSound,
                )
                SkrootproActionRow(
                    title = stringResource(R.string.settings_startup_sound_clear),
                    summary = "",
                    onClick = actions.onClearStartupSound,
                )
            }

            SkrootproSectionTitle(stringResource(R.string.settings_section_updates))
            SkrootproSwitchRow(
                title = stringResource(R.string.settings_module_check_update),
                checked = uiState.checkModuleUpdate,
                onCheckedChange = actions.onSetCheckModuleUpdate,
            )

            SkrootproSectionTitle(stringResource(R.string.settings_section_root_features))
            SkrootproSwitchRow(
                title = stringResource(R.string.settings_kernel_umount),
                checked = uiState.isKernelUmountEnabled,
                onCheckedChange = actions.onSetKernelUmountEnabled,
                enabled = uiState.kernelUmountStatus == "supported",
            )
            SkrootproSwitchRow(
                title = stringResource(R.string.settings_selinux_hide),
                checked = uiState.isSelinuxHideEnabled,
                onCheckedChange = actions.onSetSelinuxHideEnabled,
                enabled = uiState.selinuxHideStatus == "supported",
            )
            SkrootproSwitchRow(
                title = stringResource(R.string.settings_sulog),
                checked = uiState.isSulogEnabled,
                onCheckedChange = actions.onSetSulogEnabled,
                enabled = uiState.sulogStatus == "supported",
            )
            SkrootproSwitchRow(
                title = stringResource(R.string.settings_adb_root),
                checked = uiState.isAdbRootEnabled,
                onCheckedChange = actions.onSetAdbRootEnabled,
                enabled = uiState.adbRootStatus == "supported",
            )
            SkrootproSwitchRow(
                title = stringResource(R.string.settings_avc_spoof),
                checked = uiState.isAvcSpoofEnabled,
                onCheckedChange = actions.onSetAvcSpoofEnabled,
                enabled = uiState.avcSpoofStatus == "supported",
            )

            SkrootproSectionTitle(stringResource(R.string.settings_section_advanced))
            SkrootproActionRow(
                title = stringResource(R.string.settings_profile_template),
                summary = stringResource(R.string.settings_profile_template_summary),
                onClick = actions.onOpenProfileTemplate,
            )
            SkrootproSwitchRow(
                title = stringResource(R.string.settings_umount_modules_default),
                checked = uiState.isDefaultUmountModules,
                onCheckedChange = actions.onSetDefaultUmountModules,
            )
            SkrootproSwitchRow(
                title = stringResource(R.string.enable_web_debugging),
                checked = uiState.enableWebDebugging,
                onCheckedChange = actions.onSetEnableWebDebugging,
            )
            SkrootproSwitchRow(
                title = stringResource(R.string.settings_auto_jailbreak),
                checked = uiState.autoJailbreak,
                onCheckedChange = actions.onSetAutoJailbreak,
                enabled = uiState.isLateLoadMode,
            )

            SkrootproSectionTitle(stringResource(R.string.settings_section_maintenance))
            SkrootproActionRow(
                title = stringResource(R.string.about),
                summary = "",
                onClick = actions.onOpenAbout,
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SkrootproStylePicker(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(SkrootproColors.BarSurface, CircleShape)
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        InterfaceStyle.entries.forEachIndexed { index, style ->
            val selected = InterfaceStyle.selectedIndex(uiState.uiMode) == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(
                        color = if (selected) SkrootproColors.Purple else Color.Transparent,
                        shape = CircleShape,
                    )
                    .clickable { actions.onSetUiModeIndex(index) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = style.label,
                    color = if (selected) Color.White else SkrootproColors.Muted,
                    fontSize = skrootproSp(14f, maxScale = 1.04f),
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun SkrootproDurationSliderRow(
    title: String,
    value: Int,
    onValueChange: (Int) -> Unit,
) {
    var sliderValue by remember(value) { mutableFloatStateOf(value.toFloat()) }
    val currentSeconds = sliderValue.roundToInt()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(SkrootproColors.BarSurface)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Rounded.Timer,
                contentDescription = null,
                tint = SkrootproColors.Text,
            )
            Text(
                text = title,
                color = SkrootproColors.Text,
                fontSize = skrootproSp(16f, maxScale = 1.05f),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(R.string.settings_startup_sound_duration_value, currentSeconds),
                color = SkrootproColors.Muted,
                fontSize = skrootproSp(13f, maxScale = 1.05f),
                fontWeight = FontWeight.Medium,
            )
        }
        Slider(
            value = sliderValue,
            onValueChange = {
                sliderValue = it
                onValueChange(it.roundToInt())
            },
            valueRange = MIN_CUSTOM_STARTUP_SOUND_DURATION_SECONDS.toFloat()..
                MAX_CUSTOM_STARTUP_SOUND_DURATION_SECONDS.toFloat(),
            steps = MAX_CUSTOM_STARTUP_SOUND_DURATION_SECONDS -
                MIN_CUSTOM_STARTUP_SOUND_DURATION_SECONDS - 1,
        )
    }
}

@Composable
private fun SkrootproSliderRow(
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float> = MIN_CUSTOM_WALLPAPER_OPACITY..MAX_CUSTOM_WALLPAPER_OPACITY,
    onValueChange: (Float) -> Unit,
) {
    var sliderValue by remember(value) { mutableFloatStateOf(value) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(SkrootproColors.BarSurface)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                color = SkrootproColors.Text,
                fontSize = skrootproSp(16f, maxScale = 1.05f),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${(sliderValue * 100).roundToInt()}%",
                color = SkrootproColors.Muted,
                fontSize = skrootproSp(13f, maxScale = 1.05f),
                fontWeight = FontWeight.Medium,
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
private fun SkrootproActionRow(
    title: String,
    summary: String,
    leadingIcon: ImageVector? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(SkrootproColors.BarSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (leadingIcon != null) {
                androidx.compose.material3.Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = SkrootproColors.Text,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = SkrootproColors.Text,
                    fontSize = skrootproSp(16f, maxScale = 1.05f),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (summary.isNotBlank()) {
                    Text(
                        text = summary,
                        color = SkrootproColors.Muted,
                        fontSize = skrootproSp(12.5f, maxScale = 1.05f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        androidx.compose.material3.Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
            contentDescription = null,
            tint = SkrootproColors.Muted,
        )
    }
}

@Composable
private fun SkrootproSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(SkrootproColors.BarSurface)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = if (enabled) SkrootproColors.Text else SkrootproColors.DisabledText,
            fontSize = skrootproSp(16f, maxScale = 1.05f),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
        )
    }
}
