package me.weishu.kernelsu.ui.component

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.weishu.kernelsu.ui.LocalUiMode
import me.weishu.kernelsu.ui.UiMode
import me.weishu.kernelsu.ui.component.liquid.isLiquidGlassTheme
import me.weishu.kernelsu.ui.component.liquid.liquidGlassBackdropColor
import me.weishu.kernelsu.ui.theme.isInDarkTheme
import me.weishu.kernelsu.ui.util.CustomWallpaperCrop
import me.weishu.kernelsu.ui.util.DEFAULT_CUSTOM_WALLPAPER_OPACITY
import me.weishu.kernelsu.ui.util.DEFAULT_CUSTOM_WALLPAPER_PASSTHROUGH_OPACITY
import me.weishu.kernelsu.ui.util.loadCustomImageBitmap
import me.weishu.kernelsu.ui.util.sanitizeCustomWallpaperOpacity
import me.weishu.kernelsu.ui.util.sanitizeCustomWallpaperPassthroughOpacity
import top.yukonga.miuix.kmp.theme.MiuixTheme

private const val WALLPAPER_BACKGROUND_MAX_SIDE = 1800
private const val WALLPAPER_PREVIEW_MAX_SIDE = 1400

@Composable
fun CustomWallpaperBackground(
    uriString: String?,
    opacity: Float = DEFAULT_CUSTOM_WALLPAPER_OPACITY,
    crop: CustomWallpaperCrop = CustomWallpaperCrop(),
    imageAlpha: Float = 1f,
    drawOverlay: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val imageBitmap = rememberCustomImageBitmap(
        uriString = uriString,
        maxSide = WALLPAPER_BACKGROUND_MAX_SIDE,
        crop = crop,
    ) ?: return

    Image(
        modifier = modifier.fillMaxSize(),
        bitmap = imageBitmap,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alpha = imageAlpha.coerceIn(0f, 1f),
    )
    if (drawOverlay) {
        val overlayAlpha = 1f - sanitizeCustomWallpaperOpacity(opacity)
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(if (isInDarkTheme()) Color.Black.copy(alpha = overlayAlpha) else Color.White.copy(alpha = overlayAlpha))
        )
    }
}

@Composable
fun CustomWallpaperRoot(
    uriString: String?,
    opacity: Float = DEFAULT_CUSTOM_WALLPAPER_OPACITY,
    crop: CustomWallpaperCrop = CustomWallpaperCrop(),
    passthroughEnabled: Boolean = false,
    passthroughOpacity: Float = DEFAULT_CUSTOM_WALLPAPER_PASSTHROUGH_OPACITY,
    content: @Composable BoxScope.() -> Unit,
) {
    val isLiquidGlass = isLiquidGlassTheme()
    val surfaceColor = when (LocalUiMode.current) {
        UiMode.Material -> MaterialTheme.colorScheme.surface
        UiMode.Miuix -> liquidGlassBackdropColor()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceColor),
    ) {
        CustomWallpaperBackground(
            uriString = uriString,
            opacity = if (isLiquidGlass) opacity.coerceAtMost(0.42f) else opacity,
            crop = crop,
        )
        content()
        if (passthroughEnabled && !uriString.isNullOrBlank()) {
            CustomWallpaperBackground(
                uriString = uriString,
                opacity = sanitizeCustomWallpaperPassthroughOpacity(passthroughOpacity),
                crop = crop,
                imageAlpha = sanitizeCustomWallpaperPassthroughOpacity(passthroughOpacity),
                drawOverlay = false,
            )
        }
    }
}

@Composable
fun rememberCustomWallpaperPreviewBitmap(
    uriString: String?,
    crop: CustomWallpaperCrop = CustomWallpaperCrop(),
) = rememberCustomImageBitmap(
    uriString = uriString,
    maxSide = WALLPAPER_PREVIEW_MAX_SIDE,
    crop = crop,
)

@Composable
fun rememberCustomImageBitmap(
    uriString: String?,
    maxSide: Int = WALLPAPER_PREVIEW_MAX_SIDE,
    crop: CustomWallpaperCrop = CustomWallpaperCrop(),
) = run {
    val context = LocalContext.current
    val bitmap by produceState<Bitmap?>(initialValue = null, uriString, maxSide, crop, context) {
        value = if (uriString.isNullOrBlank()) {
            null
        } else {
            withContext(Dispatchers.IO) {
                loadCustomImageBitmap(context, uriString, maxSide, crop)
            }
        }
    }
    remember(bitmap) { bitmap?.asImageBitmap() }
}
