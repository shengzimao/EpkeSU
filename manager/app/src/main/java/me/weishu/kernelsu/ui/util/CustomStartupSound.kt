package me.weishu.kernelsu.ui.util

import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log

const val CUSTOM_STARTUP_SOUND_URI_KEY = "custom_startup_sound_uri"

fun takePersistableAudioReadPermission(context: Context, uri: Uri) {
    runCatching {
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }
}

fun releasePersistableAudioReadPermission(context: Context, uriString: String?) {
    if (uriString.isNullOrBlank()) return
    runCatching {
        context.contentResolver.releasePersistableUriPermission(
            Uri.parse(uriString),
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }
}

object StartupSoundPlayer {

    private var player: MediaPlayer? = null
    private var source: AssetFileDescriptor? = null
    private var suppressNextAutoPlay = false
    private val mainHandler = Handler(Looper.getMainLooper())

    fun playConfigured(context: Context) {
        if (suppressNextAutoPlay) {
            suppressNextAutoPlay = false
            return
        }
        val appContext = context.applicationContext
        val uriString = appContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getString(CUSTOM_STARTUP_SOUND_URI_KEY, null)
        play(appContext, uriString)
    }

    fun suppressNextAutoPlay() {
        suppressNextAutoPlay = true
    }

    fun clearAutoPlaySuppression() {
        suppressNextAutoPlay = false
    }

    fun play(
        context: Context,
        uriString: String?,
        onError: ((Throwable?) -> Unit)? = null,
    ) {
        if (uriString.isNullOrBlank()) return
        stop()

        runCatching {
            val appContext = context.applicationContext
            val uri = Uri.parse(uriString)
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            player = MediaPlayer().apply {
                setAudioAttributes(audioAttributes)
                setVolume(1.0f, 1.0f)
                source = runCatching {
                    appContext.contentResolver.openAssetFileDescriptor(uri, "r")
                }.getOrNull()
                val currentSource = source
                if (currentSource != null) {
                    if (currentSource.length == AssetFileDescriptor.UNKNOWN_LENGTH) {
                        setDataSource(currentSource.fileDescriptor)
                    } else {
                        setDataSource(currentSource.fileDescriptor, currentSource.startOffset, currentSource.length)
                    }
                } else {
                    setDataSource(appContext, uri)
                }
                setOnPreparedListener {
                    runCatching {
                        it.start()
                    }.onFailure { throwable ->
                        Log.e("StartupSound", "failed to start startup sound", throwable)
                        cleanup(it)
                        notifyError(onError, throwable)
                    }
                }
                setOnCompletionListener { mediaPlayer ->
                    cleanup(mediaPlayer)
                }
                setOnErrorListener { mediaPlayer, what, extra ->
                    Log.e("StartupSound", "failed to play startup sound: what=$what extra=$extra")
                    cleanup(mediaPlayer)
                    notifyError(onError, null)
                    true
                }
                prepareAsync()
            }
        }.onFailure {
            Log.e("StartupSound", "failed to play startup sound", it)
            stop()
            notifyError(onError, it)
        }
    }

    fun stop() {
        player?.let { mediaPlayer ->
            runCatching {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }
            }
            mediaPlayer.release()
        }
        player = null
        runCatching { source?.close() }
        source = null
    }

    private fun cleanup(mediaPlayer: MediaPlayer) {
        if (player === mediaPlayer) {
            player = null
        }
        mediaPlayer.release()
        runCatching { source?.close() }
        source = null
    }

    private fun notifyError(onError: ((Throwable?) -> Unit)?, throwable: Throwable?) {
        onError ?: return
        mainHandler.post { onError(throwable) }
    }
}
