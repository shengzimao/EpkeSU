package me.weishu.kernelsu.ui.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.compose.runtime.Immutable
import kotlin.math.roundToInt

const val CUSTOM_WALLPAPER_URI_KEY = "custom_wallpaper_uri"
const val CUSTOM_WALLPAPER_OPACITY_KEY = "custom_wallpaper_opacity"
const val CUSTOM_WALLPAPER_CROP_LEFT_KEY = "custom_wallpaper_crop_left"
const val CUSTOM_WALLPAPER_CROP_TOP_KEY = "custom_wallpaper_crop_top"
const val CUSTOM_WALLPAPER_CROP_RIGHT_KEY = "custom_wallpaper_crop_right"
const val CUSTOM_WALLPAPER_CROP_BOTTOM_KEY = "custom_wallpaper_crop_bottom"
const val CUSTOM_WALLPAPER_PASSTHROUGH_ENABLED_KEY = "custom_wallpaper_passthrough_enabled"
const val CUSTOM_WALLPAPER_PASSTHROUGH_OPACITY_KEY = "custom_wallpaper_passthrough_opacity"
const val DEFAULT_CUSTOM_WALLPAPER_OPACITY = 0.30f
const val MIN_CUSTOM_WALLPAPER_OPACITY = 0.10f
const val MAX_CUSTOM_WALLPAPER_OPACITY = 0.80f
const val DEFAULT_CUSTOM_WALLPAPER_PASSTHROUGH_OPACITY = 0.25f
const val MIN_CUSTOM_WALLPAPER_PASSTHROUGH_OPACITY = 0.0f
const val MAX_CUSTOM_WALLPAPER_PASSTHROUGH_OPACITY = 0.65f

val DEFAULT_CUSTOM_WALLPAPER_CROP = CustomWallpaperCrop(
    left = 0.10f,
    top = 0.10f,
    right = 0.90f,
    bottom = 0.90f,
)

@Immutable
data class CustomWallpaperCrop(
    val left: Float = 0.10f,
    val top: Float = 0.10f,
    val right: Float = 0.90f,
    val bottom: Float = 0.90f,
) {
    val width: Float get() = right - left
    val height: Float get() = bottom - top
}

fun sanitizeCustomWallpaperOpacity(value: Float): Float {
    return value.coerceIn(MIN_CUSTOM_WALLPAPER_OPACITY, MAX_CUSTOM_WALLPAPER_OPACITY)
}

fun sanitizeCustomWallpaperPassthroughOpacity(value: Float): Float {
    return value.coerceIn(MIN_CUSTOM_WALLPAPER_PASSTHROUGH_OPACITY, MAX_CUSTOM_WALLPAPER_PASSTHROUGH_OPACITY)
}

fun sanitizeCustomWallpaperCrop(crop: CustomWallpaperCrop): CustomWallpaperCrop {
    val minSize = 0.12f
    val left = crop.left.coerceIn(0f, 1f - minSize)
    val top = crop.top.coerceIn(0f, 1f - minSize)
    val right = crop.right.coerceIn(left + minSize, 1f)
    val bottom = crop.bottom.coerceIn(top + minSize, 1f)
    return CustomWallpaperCrop(left, top, right, bottom)
}

fun takePersistableImageReadPermission(context: Context, uri: Uri) {
    runCatching {
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }
}

fun releasePersistableImageReadPermission(context: Context, uriString: String?) {
    if (uriString.isNullOrBlank()) return
    runCatching {
        context.contentResolver.releasePersistableUriPermission(
            Uri.parse(uriString),
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }
}

fun loadCustomImageBitmap(
    context: Context,
    uriString: String,
    maxSide: Int,
    crop: CustomWallpaperCrop? = null,
): Bitmap? {
    return runCatching {
        val uri = Uri.parse(uriString)
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        val bitmap = ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            val imageMaxSide = maxOf(info.size.width, info.size.height)
            val sampleSize = maxOf(1, (imageMaxSide + maxSide - 1) / maxSide)
            decoder.setTargetSampleSize(sampleSize)
        }
        cropBitmap(bitmap, crop)
    }.getOrNull()
}

private fun cropBitmap(bitmap: Bitmap, crop: CustomWallpaperCrop?): Bitmap {
    val safeCrop = crop?.let(::sanitizeCustomWallpaperCrop) ?: return bitmap
    if (safeCrop.left <= 0f && safeCrop.top <= 0f && safeCrop.right >= 1f && safeCrop.bottom >= 1f) {
        return bitmap
    }
    val left = (safeCrop.left * bitmap.width).roundToInt().coerceIn(0, bitmap.width - 1)
    val top = (safeCrop.top * bitmap.height).roundToInt().coerceIn(0, bitmap.height - 1)
    val right = (safeCrop.right * bitmap.width).roundToInt().coerceIn(left + 1, bitmap.width)
    val bottom = (safeCrop.bottom * bitmap.height).roundToInt().coerceIn(top + 1, bitmap.height)
    val cropped = Bitmap.createBitmap(bitmap, left, top, right - left, bottom - top)
    if (cropped !== bitmap && !bitmap.isRecycled) {
        bitmap.recycle()
    }
    return cropped
}
