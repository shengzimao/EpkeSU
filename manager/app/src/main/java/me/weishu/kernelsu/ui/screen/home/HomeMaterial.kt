package me.weishu.kernelsu.ui.screen.home

import android.content.ClipData
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.weishu.kernelsu.KernelVersion
import me.weishu.kernelsu.R
import me.weishu.kernelsu.ui.component.material.TonalCard
import me.weishu.kernelsu.ui.component.rebootlistpopup.RebootListPopup
import me.weishu.kernelsu.ui.component.statustag.StatusTag
import me.weishu.kernelsu.ui.screen.settings.SettingsWallpaperCropDialog
import me.weishu.kernelsu.ui.theme.isInDarkTheme
import me.weishu.kernelsu.ui.util.CustomWallpaperCrop

@Composable
fun HomePagerMaterial(
    state: HomeUiState,
    actions: HomeActions,
    bottomInnerPadding: Dp,
    installFeedbackActive: Boolean = false,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        containerColor = Color.Transparent,
        topBar = { TopBar(scrollBehavior = scrollBehavior) },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatusCard(
                state = state,
                actions = actions,
                installFeedbackActive = installFeedbackActive,
            )
            WarningSummaryCard(messages = homeWarningMessages(state))
            InfoCard(systemInfo = state.systemInfo)
            SecondaryLinksCard(onOpenUrl = actions.onOpenUrl)
            Spacer(Modifier.height(bottomInnerPadding))
        }
    }
}

@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    LargeFlexibleTopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        actions = { RebootListPopup() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        ),
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun StatusCard(
    state: HomeUiState,
    actions: HomeActions,
    installFeedbackActive: Boolean = false,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        val isInstalled = state.ksuVersion != null
        val isInstallable = state.kernelVersion.isGKI()
        val containerColor = when {
            isInstalled -> MaterialTheme.colorScheme.secondaryContainer
            isInstallable -> MaterialTheme.colorScheme.tertiaryContainer
            else -> MaterialTheme.colorScheme.surfaceVariant
        }
        val contentColor = when {
            isInstalled -> MaterialTheme.colorScheme.onSecondaryContainer
            isInstallable -> MaterialTheme.colorScheme.onTertiaryContainer
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        }
        val accentColor = when {
            isInstalled -> MaterialTheme.colorScheme.primary
            isInstallable -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.outline
        }
        val statusIcon = when {
            isInstalled -> Icons.Outlined.CheckCircle
            isInstallable -> Icons.Rounded.PowerSettingsNew
            else -> Icons.Outlined.Block
        }

        val statusContent = @Composable {
            Box(modifier = Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(156.dp)
                        .align(Alignment.BottomEnd)
                        .offset(32.dp, 36.dp),
                    tint = contentColor.copy(alpha = 0.12f)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    when {
                        state.ksuVersion != null -> {
                            val workingMode = when (state.lkmMode) {
                                null -> ""
                                true -> "LKM"
                                else -> "GKI"
                            }

                            StatusHeader(
                                icon = statusIcon,
                                iconContentDescription = stringResource(R.string.home_working),
                                title = stringResource(id = R.string.home_working),
                                subtitle = stringResource(
                                    R.string.home_working_version,
                                    "${state.ksuVersion}-${state.kernelUAPIVersion}"
                                ),
                                accentColor = accentColor,
                                contentColor = contentColor
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (workingMode.isNotEmpty()) {
                                    StatusTag(
                                        label = workingMode,
                                        contentColor = MaterialTheme.colorScheme.onPrimary,
                                        backgroundColor = MaterialTheme.colorScheme.primary
                                    )
                                }
                                if (state.isSafeMode) {
                                    StatusTag(
                                        label = stringResource(id = R.string.safe_mode),
                                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                        backgroundColor = MaterialTheme.colorScheme.errorContainer
                                    )
                                }
                                if (state.isLateLoadMode) {
                                    StatusTag(
                                        label = stringResource(id = R.string.jailbreak_mode),
                                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                        backgroundColor = MaterialTheme.colorScheme.errorContainer
                                    )
                                }
                            }
                        }

                        state.kernelVersion.isGKI() -> {
                            StatusHeader(
                                icon = statusIcon,
                                iconContentDescription = stringResource(R.string.home_not_installed),
                                title = stringResource(R.string.home_not_installed),
                                subtitle = stringResource(R.string.home_click_to_install),
                                accentColor = accentColor,
                                contentColor = contentColor,
                                pulse = !installFeedbackActive,
                                loading = installFeedbackActive
                            )
                            InstallActionRow(
                                installFeedbackActive = installFeedbackActive,
                                accentColor = accentColor,
                                onInstallClick = actions.onInstallClick,
                                showJailbreak = state.isSELinuxPermissive,
                                onJailbreakClick = actions.onJailbreakClick
                            )
                        }

                        else -> {
                            StatusHeader(
                                icon = statusIcon,
                                iconContentDescription = stringResource(R.string.home_unsupported),
                                title = stringResource(R.string.home_unsupported),
                                subtitle = stringResource(R.string.home_unsupported_reason),
                                accentColor = accentColor,
                                contentColor = contentColor
                            )
                        }
                    }
                }
            }
        }

        if (state.isLateLoadMode) {
            TonalCard(containerColor = containerColor, content = statusContent)
        } else {
            TonalCard(
                containerColor = containerColor,
                enabled = !installFeedbackActive,
                onClick = actions.onInstallClick,
                content = statusContent
            )
        }
        if (state.isFullFeatured) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TonalCard(
                    modifier = Modifier.weight(1f),
                    onClick = actions.onSuperuserClick
                ) {
                    MetricContent(
                        target = HomeMetricCardWallpaperTarget.Superuser,
                        icon = Icons.Rounded.Shield,
                        title = stringResource(R.string.superuser),
                        value = state.superuserCount.toString()
                    )
                }
                TonalCard(
                    modifier = Modifier.weight(1f),
                    onClick = actions.onModuleClick
                ) {
                    MetricContent(
                        target = HomeMetricCardWallpaperTarget.Module,
                        icon = Icons.Rounded.Extension,
                        title = stringResource(R.string.module),
                        value = state.moduleCount.toString()
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusHeader(
    icon: ImageVector,
    iconContentDescription: String,
    title: String,
    subtitle: String,
    accentColor: Color,
    contentColor: Color,
    pulse: Boolean = false,
    loading: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatusIconBubble(
            icon = icon,
            iconContentDescription = iconContentDescription,
            accentColor = accentColor,
            pulse = pulse,
            loading = loading
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor.copy(alpha = 0.78f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun StatusIconBubble(
    icon: ImageVector,
    iconContentDescription: String,
    accentColor: Color,
    pulse: Boolean,
    loading: Boolean,
) {
    val transition = rememberInfiniteTransition(label = "status_icon_pulse")
    val pulseScale by transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "status_icon_scale"
    )
    val pulseAlpha by transition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.24f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "status_icon_alpha"
    )

    Box(modifier = Modifier.size(52.dp), contentAlignment = Alignment.Center) {
        if (pulse) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        scaleX = pulseScale
                        scaleY = pulseScale
                    }
                    .background(accentColor.copy(alpha = pulseAlpha), CircleShape)
            )
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(accentColor.copy(alpha = 0.14f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    color = accentColor,
                    strokeWidth = 2.5.dp
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = iconContentDescription,
                    modifier = Modifier.size(28.dp),
                    tint = accentColor
                )
            }
        }
    }
}

@Composable
private fun InstallActionRow(
    installFeedbackActive: Boolean,
    accentColor: Color,
    onInstallClick: () -> Unit,
    showJailbreak: Boolean,
    onJailbreakClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (installFeedbackActive) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = accentColor,
                trackColor = accentColor.copy(alpha = 0.18f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onInstallClick,
                enabled = !installFeedbackActive,
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                    disabledContainerColor = accentColor.copy(alpha = 0.45f),
                    disabledContentColor = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.86f)
                )
            ) {
                if (installFeedbackActive) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onTertiary,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.size(8.dp))
                }
                Text(
                    text = stringResource(
                        if (installFeedbackActive) R.string.home_install_preparing else R.string.install
                    )
                )
            }
            if (showJailbreak) {
                Button(
                    onClick = onJailbreakClick,
                    enabled = !installFeedbackActive,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text(stringResource(R.string.home_jailbreak))
                }
            }
        }
    }
}

@Composable
private fun MetricContent(
    target: HomeMetricCardWallpaperTarget,
    icon: ImageVector,
    title: String,
    value: String,
) {
    var showWallpaperPreview by remember { mutableStateOf(false) }
    var showWallpaperCropEditor by remember { mutableStateOf(false) }
    val wallpaperState = rememberHomeMetricCardWallpaperState(
        target = target,
        onWallpaperSelected = { showWallpaperCropEditor = true }
    )
    val wallpaperBitmap = rememberHomeMetricCardWallpaperBitmap(
        uriString = wallpaperState.uriString,
        crop = wallpaperState.crop,
    )
    val hasWallpaper = wallpaperBitmap != null
    val primaryColor = if (hasWallpaper) Color.White else MaterialTheme.colorScheme.onSurface
    val secondaryColor = if (hasWallpaper) {
        Color.White.copy(alpha = 0.82f)
    } else {
        MaterialTheme.colorScheme.outline
    }
    val accentColor = if (hasWallpaper) Color.White else MaterialTheme.colorScheme.primary
    val bubbleColor = if (hasWallpaper) {
        Color.White.copy(alpha = 0.18f)
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        HomeMetricCardWallpaperBackground(bitmap = wallpaperBitmap)
        MetricCardWallpaperActions(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
            target = target,
            hasWallpaper = hasWallpaper,
            showClear = wallpaperState.hasSelectedWallpaper,
            onPickWallpaper = wallpaperState.onPickWallpaper,
            onEditCrop = { showWallpaperCropEditor = true },
            onPreviewWallpaper = { showWallpaperPreview = true },
            onClearWallpaper = wallpaperState.onClearWallpaper,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 12.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(bubbleColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = accentColor
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    modifier = Modifier.padding(end = 44.dp),
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = secondaryColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    color = primaryColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
    SettingsWallpaperCropDialog(
        show = showWallpaperCropEditor && wallpaperState.hasSelectedWallpaper,
        uriString = wallpaperState.uriString,
        crop = wallpaperState.crop,
        onCropChange = {
            wallpaperState.onCropChange(it)
            showWallpaperPreview = true
        },
        onDismissRequest = { showWallpaperCropEditor = false },
        title = stringResource(target.cropLabelRes),
        editorAspectRatio = target.aspectRatio,
        cropAspectRatio = target.aspectRatio,
    )
    MetricCardWallpaperPreviewDialog(
        show = showWallpaperPreview && wallpaperBitmap != null,
        target = target,
        bitmap = wallpaperBitmap,
        icon = icon,
        title = title,
        value = value,
        onDismissRequest = { showWallpaperPreview = false },
    )
}

@Composable
private fun MetricCardWallpaperActions(
    target: HomeMetricCardWallpaperTarget,
    hasWallpaper: Boolean,
    showClear: Boolean,
    onPickWallpaper: () -> Unit,
    onEditCrop: () -> Unit,
    onPreviewWallpaper: () -> Unit,
    onClearWallpaper: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor = if (hasWallpaper) {
        Color.Black.copy(alpha = 0.28f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f)
    }
    val contentColor = if (hasWallpaper) {
        Color.White
    } else {
        MaterialTheme.colorScheme.outline
    }
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.background(containerColor, RoundedCornerShape(999.dp))) {
        IconButton(onClick = { expanded = true }, modifier = Modifier.size(32.dp)) {
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = stringResource(target.pickLabelRes),
                tint = contentColor
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(target.pickLabelRes)) },
                onClick = {
                    expanded = false
                    onPickWallpaper()
                },
            )
            if (showClear) {
                DropdownMenuItem(
                    text = { Text(stringResource(target.cropLabelRes)) },
                    onClick = {
                        expanded = false
                        onEditCrop()
                    },
                )
            }
            if (hasWallpaper) {
                DropdownMenuItem(
                    text = { Text(stringResource(target.previewLabelRes)) },
                    onClick = {
                        expanded = false
                        onPreviewWallpaper()
                    },
                )
            }
            if (showClear) {
                DropdownMenuItem(
                    text = { Text(stringResource(target.clearLabelRes)) },
                    onClick = {
                        expanded = false
                        onClearWallpaper()
                    },
                )
            }
        }
    }
}

@Composable
private fun MetricCardWallpaperPreviewDialog(
    show: Boolean,
    target: HomeMetricCardWallpaperTarget,
    bitmap: Bitmap?,
    icon: ImageVector,
    title: String,
    value: String,
    onDismissRequest: () -> Unit,
) {
    val imageBitmap = remember(bitmap) { bitmap?.asImageBitmap() }
    if (!show || imageBitmap == null) return

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(target.previewLabelRes)) },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(target.aspectRatio)
                    .clip(RoundedCornerShape(18.dp))
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    bitmap = imageBitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = if (isInDarkTheme()) 0.52f else 0.44f))
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.White.copy(alpha = 0.18f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color.White
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White.copy(alpha = 0.82f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = value,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(android.R.string.ok))
            }
        },
    )
}

@Composable
private fun WarningSummaryCard(
    messages: List<String>,
) {
    if (messages.isEmpty()) return

    var expanded by remember { mutableStateOf(false) }
    val visibleMessages = if (expanded) messages else messages.take(1)
    val hiddenCount = messages.size - visibleMessages.size

    TonalCard(containerColor = MaterialTheme.colorScheme.errorContainer) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.14f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.WarningAmber,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.home_warning_title),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = visibleMessages.first(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.82f),
                        maxLines = if (expanded) 3 else 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            visibleMessages.drop(1).forEach { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.82f)
                )
            }
            if (messages.size > 1) {
                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = if (expanded) {
                            stringResource(R.string.home_warning_show_less)
                        } else {
                            stringResource(R.string.home_warning_more, hiddenCount)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SecondaryLinksCard(onOpenUrl: (String) -> Unit) {
    val learnUrl = stringResource(R.string.home_learn_kernelsu_url)
    TonalCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            SecondaryLinkItem(
                icon = Icons.Rounded.FavoriteBorder,
                title = stringResource(R.string.home_support_title),
                summary = stringResource(R.string.home_support_content),
                onClick = { onOpenUrl("https://patreon.com/weishu") }
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(horizontal = 20.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.62f))
            )
            SecondaryLinkItem(
                icon = Icons.Rounded.Info,
                title = stringResource(R.string.home_learn_kernelsu),
                summary = stringResource(R.string.home_click_to_learn_kernelsu),
                onClick = { onOpenUrl(learnUrl) }
            )
        }
    }
}

@Composable
private fun SecondaryLinkItem(
    icon: ImageVector,
    title: String,
    summary: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.11f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = summary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun InfoCard(systemInfo: SystemInfo) {
    val clipboard = LocalClipboard.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val copiedText = stringResource(R.string.home_copied_to_clipboard)
    var fingerprintExpanded by remember { mutableStateOf(false) }
    var showStatusWallpaperPreview by remember { mutableStateOf(false) }
    var showStatusWallpaperCropEditor by remember { mutableStateOf(false) }
    val statusWallpaperState = rememberHomeMetricCardWallpaperState(
        target = HomeMetricCardWallpaperTarget.StatusMonitor,
        onWallpaperSelected = { showStatusWallpaperCropEditor = true }
    )
    val statusWallpaperBitmap = rememberHomeMetricCardWallpaperBitmap(
        uriString = statusWallpaperState.uriString,
        crop = statusWallpaperState.crop,
    )
    var showSystemInfoWallpaperPreview by remember { mutableStateOf(false) }
    var showSystemInfoWallpaperCropEditor by remember { mutableStateOf(false) }
    val systemInfoWallpaperState = rememberHomeMetricCardWallpaperState(
        target = HomeMetricCardWallpaperTarget.SystemInfo,
        onWallpaperSelected = { showSystemInfoWallpaperCropEditor = true }
    )
    val systemInfoWallpaperBitmap = rememberHomeMetricCardWallpaperBitmap(
        uriString = systemInfoWallpaperState.uriString,
        crop = systemInfoWallpaperState.crop,
    )

    fun copyValue(label: String, content: String) {
        scope.launch {
            clipboard.setClipEntry(ClipEntry(ClipData.newPlainText(label, content)))
            Toast.makeText(context, copiedText, Toast.LENGTH_SHORT).show()
        }
    }

    TonalCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            val selinuxDisplay = when (systemInfo.selinuxStatus) {
                "Enforcing" -> stringResource(R.string.selinux_status_enforcing)
                "Permissive" -> stringResource(R.string.selinux_status_permissive)
                "Disabled" -> stringResource(R.string.selinux_status_disabled)
                else -> stringResource(R.string.selinux_status_unknown)
            }
            val seccompDisplay = when (systemInfo.seccompStatus) {
                -1 -> stringResource(R.string.seccomp_status_not_supported)
                0 -> stringResource(R.string.seccomp_status_disabled)
                1 -> stringResource(R.string.seccomp_status_strict)
                2 -> stringResource(R.string.seccomp_status_filter)
                else -> stringResource(R.string.seccomp_status_unknown)
            }
            StatusMonitorPanel(
                selinuxLabel = stringResource(R.string.home_selinux_status),
                selinuxValue = selinuxDisplay,
                selinuxDotColor = selinuxDotColor(systemInfo.selinuxStatus),
                seccompLabel = stringResource(R.string.home_seccomp_status),
                seccompValue = seccompDisplay,
                seccompDotColor = seccompDotColor(systemInfo.seccompStatus),
                wallpaperState = statusWallpaperState,
                wallpaperBitmap = statusWallpaperBitmap,
                onEditWallpaperCrop = { showStatusWallpaperCropEditor = true },
                onPreviewWallpaper = { showStatusWallpaperPreview = true },
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.62f))
            )

            SystemInfoPanel(
                systemInfo = systemInfo,
                fingerprintExpanded = fingerprintExpanded,
                onFingerprintExpandedChange = { fingerprintExpanded = it },
                wallpaperState = systemInfoWallpaperState,
                wallpaperBitmap = systemInfoWallpaperBitmap,
                onEditWallpaperCrop = { showSystemInfoWallpaperCropEditor = true },
                onPreviewWallpaper = { showSystemInfoWallpaperPreview = true },
                onCopyValue = ::copyValue,
            )
        }
    }
    HomeWallpaperCropDialog(
        show = showStatusWallpaperCropEditor && statusWallpaperState.hasSelectedWallpaper,
        target = HomeMetricCardWallpaperTarget.StatusMonitor,
        uriString = statusWallpaperState.uriString,
        crop = statusWallpaperState.crop,
        onCropChange = {
            statusWallpaperState.onCropChange(it)
            showStatusWallpaperPreview = true
        },
        onDismissRequest = { showStatusWallpaperCropEditor = false },
    )
    StatusMonitorWallpaperPreviewDialog(
        show = showStatusWallpaperPreview && statusWallpaperBitmap != null,
        bitmap = statusWallpaperBitmap,
        selinuxLabel = stringResource(R.string.home_selinux_status),
        selinuxValue = when (systemInfo.selinuxStatus) {
            "Enforcing" -> stringResource(R.string.selinux_status_enforcing)
            "Permissive" -> stringResource(R.string.selinux_status_permissive)
            "Disabled" -> stringResource(R.string.selinux_status_disabled)
            else -> stringResource(R.string.selinux_status_unknown)
        },
        selinuxDotColor = selinuxDotColor(systemInfo.selinuxStatus),
        seccompLabel = stringResource(R.string.home_seccomp_status),
        seccompValue = when (systemInfo.seccompStatus) {
            -1 -> stringResource(R.string.seccomp_status_not_supported)
            0 -> stringResource(R.string.seccomp_status_disabled)
            1 -> stringResource(R.string.seccomp_status_strict)
            2 -> stringResource(R.string.seccomp_status_filter)
            else -> stringResource(R.string.seccomp_status_unknown)
        },
        seccompDotColor = seccompDotColor(systemInfo.seccompStatus),
        onDismissRequest = { showStatusWallpaperPreview = false },
    )
    HomeWallpaperCropDialog(
        show = showSystemInfoWallpaperCropEditor && systemInfoWallpaperState.hasSelectedWallpaper,
        target = HomeMetricCardWallpaperTarget.SystemInfo,
        uriString = systemInfoWallpaperState.uriString,
        crop = systemInfoWallpaperState.crop,
        onCropChange = {
            systemInfoWallpaperState.onCropChange(it)
            showSystemInfoWallpaperPreview = true
        },
        onDismissRequest = { showSystemInfoWallpaperCropEditor = false },
    )
    SystemInfoWallpaperPreviewDialog(
        show = showSystemInfoWallpaperPreview && systemInfoWallpaperBitmap != null,
        bitmap = systemInfoWallpaperBitmap,
        systemInfo = systemInfo,
        fingerprintExpanded = fingerprintExpanded,
        onDismissRequest = { showSystemInfoWallpaperPreview = false },
    )
}

@Composable
private fun StatusMonitorPanel(
    selinuxLabel: String,
    selinuxValue: String,
    selinuxDotColor: Color,
    seccompLabel: String,
    seccompValue: String,
    seccompDotColor: Color,
    wallpaperState: HomeMetricCardWallpaperState,
    wallpaperBitmap: Bitmap?,
    onEditWallpaperCrop: () -> Unit,
    onPreviewWallpaper: () -> Unit,
) {
    val hasWallpaper = wallpaperBitmap != null
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (hasWallpaper) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.58f),
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
    ) {
        HomeMetricCardWallpaperBackground(bitmap = wallpaperBitmap)
        MetricCardWallpaperActions(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
            target = HomeMetricCardWallpaperTarget.StatusMonitor,
            hasWallpaper = hasWallpaper,
            showClear = wallpaperState.hasSelectedWallpaper,
            onPickWallpaper = wallpaperState.onPickWallpaper,
            onEditCrop = onEditWallpaperCrop,
            onPreviewWallpaper = onPreviewWallpaper,
            onClearWallpaper = wallpaperState.onClearWallpaper,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 42.dp)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatusMonitorLine(
                icon = Icons.Rounded.Security,
                label = selinuxLabel,
                value = selinuxValue,
                dotColor = selinuxDotColor,
                hasWallpaper = hasWallpaper,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        if (hasWallpaper) {
                            Color.White.copy(alpha = 0.20f)
                        } else {
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.48f)
                        }
                    )
            )
            StatusMonitorLine(
                icon = Icons.Rounded.Lock,
                label = seccompLabel,
                value = seccompValue,
                dotColor = seccompDotColor,
                hasWallpaper = hasWallpaper,
            )
        }
    }
}

@Composable
private fun StatusMonitorLine(
    icon: ImageVector,
    label: String,
    value: String,
    dotColor: Color,
    hasWallpaper: Boolean = false,
) {
    val iconColor = if (hasWallpaper) Color.White else MaterialTheme.colorScheme.primary
    val labelColor = if (hasWallpaper) Color.White.copy(alpha = 0.72f) else MaterialTheme.colorScheme.outline
    val valueColor = if (hasWallpaper) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = iconColor
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = labelColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = valueColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Box(
            modifier = Modifier
                .size(9.dp)
                .background(dotColor, CircleShape)
        )
    }
}

@Composable
private fun SystemInfoPanel(
    systemInfo: SystemInfo,
    fingerprintExpanded: Boolean,
    onFingerprintExpandedChange: (Boolean) -> Unit,
    wallpaperState: HomeMetricCardWallpaperState,
    wallpaperBitmap: Bitmap?,
    onEditWallpaperCrop: () -> Unit,
    onPreviewWallpaper: () -> Unit,
    onCopyValue: (String, String) -> Unit,
) {
    val hasWallpaper = wallpaperBitmap != null
    val labelColor = if (hasWallpaper) Color.White.copy(alpha = 0.72f) else MaterialTheme.colorScheme.outline
    val valueColor = if (hasWallpaper) Color.White else MaterialTheme.colorScheme.onSurface
    val actionColor = if (hasWallpaper) Color.White.copy(alpha = 0.82f) else MaterialTheme.colorScheme.outline

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
    ) {
        HomeMetricCardWallpaperBackground(bitmap = wallpaperBitmap)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            InfoRow(
                label = stringResource(R.string.home_manager_version),
                value = systemInfo.managerVersion,
                onCopy = { onCopyValue("manager_version", systemInfo.managerVersion) },
                labelColor = labelColor,
                valueColor = valueColor,
                actionColor = actionColor,
                trailingAction = {
                    MetricCardWallpaperActions(
                        target = HomeMetricCardWallpaperTarget.SystemInfo,
                        hasWallpaper = hasWallpaper,
                        showClear = wallpaperState.hasSelectedWallpaper,
                        onPickWallpaper = wallpaperState.onPickWallpaper,
                        onEditCrop = onEditWallpaperCrop,
                        onPreviewWallpaper = onPreviewWallpaper,
                        onClearWallpaper = wallpaperState.onClearWallpaper,
                    )
                },
            )
            InfoRow(
                label = stringResource(R.string.home_device_model),
                value = systemInfo.deviceModel,
                onCopy = { onCopyValue("device_model", systemInfo.deviceModel) },
                labelColor = labelColor,
                valueColor = valueColor,
                actionColor = actionColor,
            )
            InfoRow(
                label = stringResource(R.string.home_kernel),
                value = systemInfo.kernelVersion,
                maxLines = 3,
                onCopy = { onCopyValue("kernel_version", systemInfo.kernelVersion) },
                labelColor = labelColor,
                valueColor = valueColor,
                actionColor = actionColor,
            )
            InfoRow(
                label = stringResource(R.string.home_fingerprint),
                value = systemInfo.fingerprint,
                displayValue = compactFingerprint(systemInfo.fingerprint, fingerprintExpanded),
                maxLines = if (fingerprintExpanded) 4 else 1,
                expanded = fingerprintExpanded,
                onExpandToggle = { onFingerprintExpandedChange(!fingerprintExpanded) },
                onCopy = { onCopyValue("fingerprint", systemInfo.fingerprint) },
                labelColor = labelColor,
                valueColor = valueColor,
                actionColor = actionColor,
            )
        }
    }
}

@Composable
private fun HomeWallpaperCropDialog(
    show: Boolean,
    target: HomeMetricCardWallpaperTarget,
    uriString: String?,
    crop: CustomWallpaperCrop,
    onCropChange: (CustomWallpaperCrop) -> Unit,
    onDismissRequest: () -> Unit,
) {
    SettingsWallpaperCropDialog(
        show = show,
        uriString = uriString,
        crop = crop,
        onCropChange = onCropChange,
        onDismissRequest = onDismissRequest,
        title = stringResource(target.cropLabelRes),
        editorAspectRatio = target.aspectRatio,
        cropAspectRatio = target.aspectRatio,
    )
}

@Composable
private fun StatusMonitorWallpaperPreviewDialog(
    show: Boolean,
    bitmap: Bitmap?,
    selinuxLabel: String,
    selinuxValue: String,
    selinuxDotColor: Color,
    seccompLabel: String,
    seccompValue: String,
    seccompDotColor: Color,
    onDismissRequest: () -> Unit,
) {
    val imageBitmap = remember(bitmap) { bitmap?.asImageBitmap() }
    if (!show || imageBitmap == null) return

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(HomeMetricCardWallpaperTarget.StatusMonitor.previewLabelRes)) },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(HomeMetricCardWallpaperTarget.StatusMonitor.aspectRatio)
                    .clip(RoundedCornerShape(18.dp))
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    bitmap = imageBitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = if (isInDarkTheme()) 0.52f else 0.44f))
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatusMonitorLine(
                        icon = Icons.Rounded.Security,
                        label = selinuxLabel,
                        value = selinuxValue,
                        dotColor = selinuxDotColor,
                        hasWallpaper = true,
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.White.copy(alpha = 0.20f))
                    )
                    StatusMonitorLine(
                        icon = Icons.Rounded.Lock,
                        label = seccompLabel,
                        value = seccompValue,
                        dotColor = seccompDotColor,
                        hasWallpaper = true,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(android.R.string.ok))
            }
        },
    )
}

@Composable
private fun SystemInfoWallpaperPreviewDialog(
    show: Boolean,
    bitmap: Bitmap?,
    systemInfo: SystemInfo,
    fingerprintExpanded: Boolean,
    onDismissRequest: () -> Unit,
) {
    val imageBitmap = remember(bitmap) { bitmap?.asImageBitmap() }
    if (!show || imageBitmap == null) return

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(HomeMetricCardWallpaperTarget.SystemInfo.previewLabelRes)) },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(HomeMetricCardWallpaperTarget.SystemInfo.aspectRatio)
                    .clip(RoundedCornerShape(18.dp))
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    bitmap = imageBitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = if (isInDarkTheme()) 0.52f else 0.44f))
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SystemInfoPreviewLine(
                        label = stringResource(R.string.home_manager_version),
                        value = systemInfo.managerVersion,
                    )
                    SystemInfoPreviewLine(
                        label = stringResource(R.string.home_device_model),
                        value = systemInfo.deviceModel,
                    )
                    SystemInfoPreviewLine(
                        label = stringResource(R.string.home_kernel),
                        value = systemInfo.kernelVersion,
                        maxLines = 3,
                    )
                    SystemInfoPreviewLine(
                        label = stringResource(R.string.home_fingerprint),
                        value = compactFingerprint(systemInfo.fingerprint, fingerprintExpanded),
                        maxLines = if (fingerprintExpanded) 4 else 1,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(android.R.string.ok))
            }
        },
    )
}

@Composable
private fun SystemInfoPreviewLine(
    label: String,
    value: String,
    maxLines: Int = 2,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.72f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    onCopy: () -> Unit,
    displayValue: String = value,
    maxLines: Int = 2,
    expanded: Boolean? = null,
    onExpandToggle: (() -> Unit)? = null,
    labelColor: Color = MaterialTheme.colorScheme.outline,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    actionColor: Color = MaterialTheme.colorScheme.outline,
    trailingAction: (@Composable () -> Unit)? = null,
) {
    val rowModifier = if (onExpandToggle != null) {
        Modifier.clickable(onClick = onExpandToggle)
    } else {
        Modifier
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(rowModifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = labelColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = displayValue,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = valueColor,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (expanded != null && onExpandToggle != null) {
            IconButton(onClick = onExpandToggle) {
                Icon(
                    imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = stringResource(
                        if (expanded) R.string.home_collapse_fingerprint else R.string.home_expand_fingerprint
                    ),
                    tint = actionColor
                )
            }
        }
        IconButton(onClick = onCopy) {
            Icon(
                imageVector = Icons.Rounded.ContentCopy,
                contentDescription = stringResource(R.string.home_copy_value),
                tint = actionColor
            )
        }
        trailingAction?.invoke()
    }
}

@Composable
private fun selinuxDotColor(status: String): Color {
    return when (status) {
        "Enforcing" -> Color(0xFF2E7D32)
        "Permissive" -> MaterialTheme.colorScheme.tertiary
        "Disabled" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }
}

@Composable
private fun seccompDotColor(status: Int): Color {
    return when (status) {
        1 -> Color(0xFF2E7D32)
        2 -> Color(0xFF1976D2)
        0 -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }
}

private fun compactFingerprint(fingerprint: String, expanded: Boolean): String {
    return if (expanded || fingerprint.length <= 10) {
        fingerprint
    } else {
        "${fingerprint.take(10)}..."
    }
}

@Preview(name = "Activated")
@Composable
private fun StatusCardActivatedPreview() {
    StatusCard(
        state = previewHomeScreenState(ksuVersion = 12345, lkmMode = true, superuserCount = 5, moduleCount = 10),
        actions = HomeActions({}, {}, {}, {})
    )
}

@Preview(name = "Not Activated")
@Composable
private fun StatusCardNotActivatedPreview() {
    StatusCard(state = previewHomeScreenState(ksuVersion = null, lkmMode = null), actions = HomeActions({}, {}, {}, {}))
}

@Preview(name = "Permissive")
@Composable
private fun StatusCardPermissivePreview() {
    StatusCard(
        state = previewHomeScreenState(ksuVersion = null, lkmMode = null, selinuxStatus = "Permissive"),
        actions = HomeActions({}, {}, {}, {})
    )
}

@Preview(name = "Jailbreak")
@Composable
private fun StatusCardJailbreakPreview() {
    StatusCard(
        state = previewHomeScreenState(ksuVersion = 12345, lkmMode = true, isLateLoadMode = true, superuserCount = 5, moduleCount = 10),
        actions = HomeActions({}, {}, {}, {})
    )
}

private val previewSystemInfo = SystemInfo(
    kernelVersion = "6.1.0-android14-0-g123456789000-ab12345678",
    managerVersion = "3.0.0 (30000)",
    deviceModel = "Google Pixel 6 Pro",
    fingerprint = "google/raven/raven:14/AP1A.240305.019:user/release-keys",
    selinuxStatus = "Enforcing",
    seccompStatus = 2
)

private val previewUriHandler = object : UriHandler {
    override fun openUri(uri: String) {}
}

@Composable
private fun HomeScreenPreviewContent(
    ksuVersion: Int?,
    lkmMode: Boolean?,
    isSafeMode: Boolean = false,
    isLateLoadMode: Boolean = false,
    superuserCount: Int = 0,
    moduleCount: Int = 0,
    selinuxStatus: String = "Enforcing",
) {
    CompositionLocalProvider(LocalUriHandler provides previewUriHandler) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val actions = HomeActions({}, {}, {}, {})
            StatusCard(
                state = previewHomeScreenState(
                    ksuVersion = ksuVersion,
                    lkmMode = lkmMode,
                    isSafeMode = isSafeMode,
                    isLateLoadMode = isLateLoadMode,
                    superuserCount = superuserCount,
                    moduleCount = moduleCount,
                    selinuxStatus = selinuxStatus,
                ),
                actions = actions
            )
            InfoCard(previewSystemInfo.copy(selinuxStatus = selinuxStatus))
            SecondaryLinksCard(onOpenUrl = {})
        }
    }
}

@Preview(name = "Home Activated", showBackground = true)
@Composable
private fun HomeScreenActivatedPreview() {
    HomeScreenPreviewContent(ksuVersion = 12345, lkmMode = true, superuserCount = 5, moduleCount = 10)
}

@Preview(name = "Home Not Activated", showBackground = true)
@Composable
private fun HomeScreenNotActivatedPreview() {
    HomeScreenPreviewContent(ksuVersion = null, lkmMode = null)
}

@Preview(name = "Home Permissive", showBackground = true)
@Composable
private fun HomeScreenPermissivePreview() {
    HomeScreenPreviewContent(ksuVersion = null, lkmMode = null, selinuxStatus = "Permissive")
}

@Preview(name = "Home Jailbreak", showBackground = true)
@Composable
private fun HomeScreenJailbreakPreview() {
    HomeScreenPreviewContent(ksuVersion = 12345, lkmMode = true, isLateLoadMode = true, superuserCount = 5, moduleCount = 10)
}

private fun previewHomeScreenState(
    ksuVersion: Int?,
    lkmMode: Boolean?,
    isSafeMode: Boolean = false,
    isLateLoadMode: Boolean = false,
    superuserCount: Int = 0,
    moduleCount: Int = 0,
    selinuxStatus: String = "Enforcing",
) = HomeUiState(
    kernelVersion = KernelVersion(6, 1, 0),
    ksuVersion = ksuVersion,
    lkmMode = lkmMode,
    isManager = true,
    isManagerPrBuild = false,
    isKernelPrBuild = false,
    requiresNewKernel = false,
    isRootAvailable = ksuVersion != null,
    isSafeMode = isSafeMode,
    isLateLoadMode = isLateLoadMode,
    currentManagerVersionCode = 10000,
    superuserCount = superuserCount,
    moduleCount = moduleCount,
    systemInfo = previewSystemInfo.copy(selinuxStatus = selinuxStatus),
    kernelUAPIVersion = 1,
    managerUAPIVersion = 1,
    uapiMismatch = false
)
