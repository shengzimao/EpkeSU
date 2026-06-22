package me.weishu.kernelsu.ui.screen.module

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.weishu.kernelsu.R
import me.weishu.kernelsu.ui.LocalUiMode
import me.weishu.kernelsu.ui.UiMode
import me.weishu.kernelsu.ui.theme.isInDarkTheme
import me.weishu.kernelsu.ui.util.CustomWallpaperCrop
import me.weishu.kernelsu.ui.util.DEFAULT_CUSTOM_WALLPAPER_CROP
import me.weishu.kernelsu.ui.util.loadCustomImageBitmap
import me.weishu.kernelsu.ui.util.releasePersistableImageReadPermission
import me.weishu.kernelsu.ui.util.sanitizeCustomWallpaperCrop
import me.weishu.kernelsu.ui.util.takePersistableImageReadPermission
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.basic.TextButton as MiuixTextButton

internal const val MODULE_CARD_WALLPAPER_ASPECT_RATIO = 1.72f

private const val MODULE_CARD_WALLPAPER_MAX_SIDE = 1200
private const val MODULE_CARD_WALLPAPER_KEY_PREFIX = "module_card_wallpaper"

internal data class ModuleCardWallpaperState(
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
internal fun rememberModuleCardWallpaperState(
    moduleId: String,
    onWallpaperSelected: () -> Unit = {},
): ModuleCardWallpaperState {
    val context = LocalContext.current
    val currentOnWallpaperSelected by rememberUpdatedState(onWallpaperSelected)
    val prefs = remember(context) {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    }
    var uriString by remember(moduleId) {
        mutableStateOf(prefs.getString(moduleWallpaperUriKey(moduleId), null))
    }
    var crop by remember(moduleId) {
        mutableStateOf(readModuleCardWallpaperCrop(prefs, moduleId))
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        val nextUriString = uri.toString()
        val previousUriString = uriString
        val defaultCrop = DEFAULT_CUSTOM_WALLPAPER_CROP
        takePersistableImageReadPermission(context, uri)
        if (previousUriString != nextUriString) {
            releasePersistableImageReadPermission(context, previousUriString)
        }
        uriString = nextUriString
        crop = defaultCrop
        prefs.edit {
            putString(moduleWallpaperUriKey(moduleId), nextUriString)
            putModuleCardWallpaperCrop(moduleId, defaultCrop)
        }
        currentOnWallpaperSelected()
    }

    return remember(moduleId, uriString, crop, launcher, prefs, context) {
        ModuleCardWallpaperState(
            uriString = uriString,
            crop = crop,
            onPickWallpaper = {
                launcher.launch(arrayOf("image/*"))
            },
            onCropChange = { nextCrop ->
                val safeCrop = sanitizeCustomWallpaperCrop(nextCrop)
                crop = safeCrop
                prefs.edit {
                    putModuleCardWallpaperCrop(moduleId, safeCrop)
                }
            },
            onClearWallpaper = {
                releasePersistableImageReadPermission(context, uriString)
                uriString = null
                crop = DEFAULT_CUSTOM_WALLPAPER_CROP
                prefs.edit {
                    remove(moduleWallpaperUriKey(moduleId))
                    removeModuleCardWallpaperCrop(moduleId)
                }
            },
        )
    }
}

@Composable
internal fun rememberModuleCardWallpaperBitmap(
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
                    maxSide = MODULE_CARD_WALLPAPER_MAX_SIDE,
                    crop = crop,
                )
            }
        }
    }
    return bitmapState.value
}

@Composable
internal fun BoxScope.ModuleCardWallpaperBackground(
    bitmap: Bitmap?,
    overlayColor: Color? = null,
) {
    if (bitmap == null) return

    val imageBitmap = remember(bitmap) { bitmap.asImageBitmap() }
    Image(
        modifier = Modifier.matchParentSize(),
        bitmap = imageBitmap,
        contentDescription = null,
        contentScale = ContentScale.Crop,
    )
    Box(
        modifier = Modifier
            .matchParentSize()
            .background(
                overlayColor ?: if (isInDarkTheme()) {
                    Color.Black.copy(alpha = 0.56f)
                } else {
                    Color.White.copy(alpha = 0.68f)
                }
            )
    )
}

@Composable
internal fun ModuleCardWallpaperPreviewDialog(
    show: Boolean,
    moduleName: String,
    uriString: String?,
    bitmap: Bitmap?,
    onDismissRequest: () -> Unit,
) {
    if (!show) return

    val imageBitmap = remember(bitmap) { bitmap?.asImageBitmap() }
    when (LocalUiMode.current) {
        UiMode.Material -> AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(stringResource(R.string.module_wallpaper_preview)) },
            text = {
                ModuleCardWallpaperPreviewFrame(
                    moduleName = moduleName,
                    imageBitmap = imageBitmap,
                    uriString = uriString,
                )
            },
            confirmButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(android.R.string.ok))
                }
            },
        )

        UiMode.Miuix -> OverlayDialog(
            show = true,
            title = stringResource(R.string.module_wallpaper_preview),
            onDismissRequest = onDismissRequest,
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    ModuleCardWallpaperPreviewFrame(
                        moduleName = moduleName,
                        imageBitmap = imageBitmap,
                        uriString = uriString,
                    )
                    MiuixTextButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(android.R.string.ok),
                        onClick = onDismissRequest,
                        colors = ButtonDefaults.textButtonColorsPrimary(),
                    )
                }
            },
        )
    }
}

@Composable
private fun ModuleCardWallpaperPreviewFrame(
    moduleName: String,
    imageBitmap: ImageBitmap?,
    uriString: String?,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(MODULE_CARD_WALLPAPER_ASPECT_RATIO)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        when {
            imageBitmap != null -> {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    bitmap = imageBitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (isInDarkTheme()) {
                                Color.Black.copy(alpha = 0.56f)
                            } else {
                                Color.White.copy(alpha = 0.68f)
                            }
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = moduleName,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = stringResource(R.string.module_wallpaper_preview_hint),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            uriString.isNullOrBlank() -> Text(
                modifier = Modifier.padding(24.dp),
                text = stringResource(R.string.settings_wallpaper_not_selected),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            else -> CircularProgressIndicator()
        }
    }
}

private fun readModuleCardWallpaperCrop(
    prefs: SharedPreferences,
    moduleId: String,
): CustomWallpaperCrop {
    return sanitizeCustomWallpaperCrop(
        CustomWallpaperCrop(
            left = prefs.getFloat(moduleWallpaperCropLeftKey(moduleId), DEFAULT_CUSTOM_WALLPAPER_CROP.left),
            top = prefs.getFloat(moduleWallpaperCropTopKey(moduleId), DEFAULT_CUSTOM_WALLPAPER_CROP.top),
            right = prefs.getFloat(moduleWallpaperCropRightKey(moduleId), DEFAULT_CUSTOM_WALLPAPER_CROP.right),
            bottom = prefs.getFloat(moduleWallpaperCropBottomKey(moduleId), DEFAULT_CUSTOM_WALLPAPER_CROP.bottom),
        )
    )
}

private fun SharedPreferences.Editor.putModuleCardWallpaperCrop(
    moduleId: String,
    crop: CustomWallpaperCrop,
) {
    val safeCrop = sanitizeCustomWallpaperCrop(crop)
    putFloat(moduleWallpaperCropLeftKey(moduleId), safeCrop.left)
    putFloat(moduleWallpaperCropTopKey(moduleId), safeCrop.top)
    putFloat(moduleWallpaperCropRightKey(moduleId), safeCrop.right)
    putFloat(moduleWallpaperCropBottomKey(moduleId), safeCrop.bottom)
}

private fun SharedPreferences.Editor.removeModuleCardWallpaperCrop(moduleId: String) {
    remove(moduleWallpaperCropLeftKey(moduleId))
    remove(moduleWallpaperCropTopKey(moduleId))
    remove(moduleWallpaperCropRightKey(moduleId))
    remove(moduleWallpaperCropBottomKey(moduleId))
}

private fun moduleWallpaperUriKey(moduleId: String): String {
    return "${MODULE_CARD_WALLPAPER_KEY_PREFIX}_${moduleId}_uri"
}

private fun moduleWallpaperCropLeftKey(moduleId: String): String {
    return "${MODULE_CARD_WALLPAPER_KEY_PREFIX}_${moduleId}_crop_left"
}

private fun moduleWallpaperCropTopKey(moduleId: String): String {
    return "${MODULE_CARD_WALLPAPER_KEY_PREFIX}_${moduleId}_crop_top"
}

private fun moduleWallpaperCropRightKey(moduleId: String): String {
    return "${MODULE_CARD_WALLPAPER_KEY_PREFIX}_${moduleId}_crop_right"
}

private fun moduleWallpaperCropBottomKey(moduleId: String): String {
    return "${MODULE_CARD_WALLPAPER_KEY_PREFIX}_${moduleId}_crop_bottom"
}
