package me.weishu.kernelsu.ui.screen.home

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.weishu.kernelsu.R
import me.weishu.kernelsu.ui.theme.isInDarkTheme
import me.weishu.kernelsu.ui.util.CustomWallpaperCrop
import me.weishu.kernelsu.ui.util.DEFAULT_CUSTOM_WALLPAPER_CROP
import me.weishu.kernelsu.ui.util.loadCustomImageBitmap
import me.weishu.kernelsu.ui.util.persistCustomImageReference
import me.weishu.kernelsu.ui.util.releaseCustomImageReference
import me.weishu.kernelsu.ui.util.sanitizeCustomWallpaperCrop
import me.weishu.kernelsu.ui.util.takePersistableImageReadPermission

internal const val HOME_METRIC_CARD_WALLPAPER_ASPECT_RATIO = 1.72f

private const val HOME_METRIC_CARD_WALLPAPER_MAX_SIDE = 1200

internal enum class HomeMetricCardWallpaperTarget(
    private val keyPrefix: String,
    val aspectRatio: Float,
    @StringRes val pickLabelRes: Int,
    @StringRes val cropLabelRes: Int,
    @StringRes val previewLabelRes: Int,
    @StringRes val clearLabelRes: Int,
) {
    Superuser(
        keyPrefix = "home_superuser_card_wallpaper",
        aspectRatio = HOME_METRIC_CARD_WALLPAPER_ASPECT_RATIO,
        pickLabelRes = R.string.home_superuser_wallpaper_pick,
        cropLabelRes = R.string.home_superuser_wallpaper_crop,
        previewLabelRes = R.string.home_superuser_wallpaper_preview,
        clearLabelRes = R.string.home_superuser_wallpaper_clear,
    ),
    Module(
        keyPrefix = "home_module_card_wallpaper",
        aspectRatio = HOME_METRIC_CARD_WALLPAPER_ASPECT_RATIO,
        pickLabelRes = R.string.home_module_wallpaper_pick,
        cropLabelRes = R.string.home_module_wallpaper_crop,
        previewLabelRes = R.string.home_module_wallpaper_preview,
        clearLabelRes = R.string.home_module_wallpaper_clear,
    ),
    StatusMonitor(
        keyPrefix = "home_status_monitor_wallpaper",
        aspectRatio = 2.72f,
        pickLabelRes = R.string.home_status_monitor_wallpaper_pick,
        cropLabelRes = R.string.home_status_monitor_wallpaper_crop,
        previewLabelRes = R.string.home_status_monitor_wallpaper_preview,
        clearLabelRes = R.string.home_status_monitor_wallpaper_clear,
    ),
    SystemInfo(
        keyPrefix = "home_system_info_wallpaper",
        aspectRatio = 1.36f,
        pickLabelRes = R.string.home_system_info_wallpaper_pick,
        cropLabelRes = R.string.home_system_info_wallpaper_crop,
        previewLabelRes = R.string.home_system_info_wallpaper_preview,
        clearLabelRes = R.string.home_system_info_wallpaper_clear,
    );

    val uriKey: String get() = "${keyPrefix}_uri"
    val cropLeftKey: String get() = "${keyPrefix}_crop_left"
    val cropTopKey: String get() = "${keyPrefix}_crop_top"
    val cropRightKey: String get() = "${keyPrefix}_crop_right"
    val cropBottomKey: String get() = "${keyPrefix}_crop_bottom"
}

internal data class HomeMetricCardWallpaperState(
    val uriString: String?,
    val crop: CustomWallpaperCrop,
    val onPickWallpaper: () -> Unit,
    val onCropChange: (CustomWallpaperCrop) -> Unit,
    val onClearWallpaper: () -> Unit,
) {
    val hasSelectedWallpaper: Boolean
        get() = !uriString.isNullOrBlank()
}

@Composable
internal fun rememberHomeMetricCardWallpaperState(
    target: HomeMetricCardWallpaperTarget,
    onWallpaperSelected: () -> Unit,
): HomeMetricCardWallpaperState {
    val context = LocalContext.current
    val currentOnWallpaperSelected by rememberUpdatedState(onWallpaperSelected)
    val prefs = remember(context) {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    }
    var uriString by remember(target) {
        mutableStateOf(prefs.getString(target.uriKey, null))
    }
    var crop by remember(target) {
        mutableStateOf(readHomeMetricCardWallpaperCrop(prefs, target))
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        val nextUriString = persistCustomImageReference(context, uri, target.uriKey)
            ?: uri.toString().also { takePersistableImageReadPermission(context, uri) }
        val previousUriString = uriString
        val defaultCrop = DEFAULT_CUSTOM_WALLPAPER_CROP
        if (previousUriString != nextUriString) {
            releaseCustomImageReference(context, previousUriString)
        }
        uriString = nextUriString
        crop = defaultCrop
        prefs.edit(commit = true) {
            putString(target.uriKey, nextUriString)
            putHomeMetricCardWallpaperCrop(target, defaultCrop)
        }
        currentOnWallpaperSelected()
    }

    return remember(target, uriString, crop, launcher, prefs, context) {
        HomeMetricCardWallpaperState(
            uriString = uriString,
            crop = crop,
            onPickWallpaper = {
                launcher.launch(arrayOf("image/*"))
            },
            onCropChange = { nextCrop ->
                val safeCrop = sanitizeCustomWallpaperCrop(nextCrop)
                crop = safeCrop
                prefs.edit(commit = true) {
                    putHomeMetricCardWallpaperCrop(target, safeCrop)
                }
            },
            onClearWallpaper = {
                releaseCustomImageReference(context, uriString)
                uriString = null
                crop = DEFAULT_CUSTOM_WALLPAPER_CROP
                prefs.edit(commit = true) {
                    remove(target.uriKey)
                    removeHomeMetricCardWallpaperCrop(target)
                }
            },
        )
    }
}

@Composable
internal fun rememberHomeMetricCardWallpaperBitmap(
    uriString: String?,
    crop: CustomWallpaperCrop,
): Bitmap? {
    val context = LocalContext.current
    val bitmapState = produceState<Bitmap?>(initialValue = null, uriString, crop, context) {
        value = if (uriString.isNullOrBlank()) {
            null
        } else {
            withContext(Dispatchers.IO) {
                loadCustomImageBitmap(
                    context = context,
                    uriString = uriString,
                    maxSide = HOME_METRIC_CARD_WALLPAPER_MAX_SIDE,
                    crop = crop,
                )
            }
        }
    }
    return bitmapState.value
}

@Composable
internal fun BoxScope.HomeMetricCardWallpaperBackground(bitmap: Bitmap?) {
    if (bitmap == null) return

    val imageBitmap = remember(bitmap) { bitmap.asImageBitmap() }
    Image(
        modifier = Modifier.matchParentSize(),
        bitmap = imageBitmap,
        contentDescription = null,
        contentScale = ContentScale.Crop
    )
    Box(
        modifier = Modifier
            .matchParentSize()
            .background(Color.Black.copy(alpha = if (isInDarkTheme()) 0.52f else 0.44f))
    )
}

private fun readHomeMetricCardWallpaperCrop(
    prefs: SharedPreferences,
    target: HomeMetricCardWallpaperTarget,
): CustomWallpaperCrop {
    return sanitizeCustomWallpaperCrop(
        CustomWallpaperCrop(
            left = prefs.getFloat(target.cropLeftKey, DEFAULT_CUSTOM_WALLPAPER_CROP.left),
            top = prefs.getFloat(target.cropTopKey, DEFAULT_CUSTOM_WALLPAPER_CROP.top),
            right = prefs.getFloat(target.cropRightKey, DEFAULT_CUSTOM_WALLPAPER_CROP.right),
            bottom = prefs.getFloat(target.cropBottomKey, DEFAULT_CUSTOM_WALLPAPER_CROP.bottom),
        )
    )
}

private fun SharedPreferences.Editor.putHomeMetricCardWallpaperCrop(
    target: HomeMetricCardWallpaperTarget,
    crop: CustomWallpaperCrop,
) {
    val safeCrop = sanitizeCustomWallpaperCrop(crop)
    putFloat(target.cropLeftKey, safeCrop.left)
    putFloat(target.cropTopKey, safeCrop.top)
    putFloat(target.cropRightKey, safeCrop.right)
    putFloat(target.cropBottomKey, safeCrop.bottom)
}

private fun SharedPreferences.Editor.removeHomeMetricCardWallpaperCrop(
    target: HomeMetricCardWallpaperTarget,
) {
    remove(target.cropLeftKey)
    remove(target.cropTopKey)
    remove(target.cropRightKey)
    remove(target.cropBottomKey)
}
