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
const val CUSTOM_STARTUP_SOUND_DURATION_SECONDS_KEY = "custom_startup_sound_duration_seconds"
const val DEFAULT_CUSTOM_STARTUP_SOUND_DURATION_SECONDS = 5
const val MIN_CUSTOM_STARTUP_SOUND_DURATION_SECONDS = 1
const val MAX_CUSTOM_STARTUP_SOUND_DURATION_SECONDS = 30

fun sanitizeCustomStartupSoundDurationSeconds(value: Int): Int {
    return value.coerceIn(
        MIN_CUSTOM_STARTUP_SOUND_DURATION_SECONDS,
        MAX_CUSTOM_STARTUP_SOUND_DURATION_SECONDS,
    )
}

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
    private var stopRunnable: Runnable? = null

    fun playConfigured(context: Context) {
        if (suppressNextAutoPlay) {
            suppressNextAutoPlay = false
            return
        }
        val appContext = context.applicationContext
        val prefs = appContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val uriString = prefs.getString(CUSTOM_STARTUP_SOUND_URI_KEY, null)
        val durationSeconds = sanitizeCustomStartupSoundDurationSeconds(
            prefs.getInt(
                CUSTOM_STARTUP_SOUND_DURATION_SECONDS_KEY,
                DEFAULT_CUSTOM_STARTUP_SOUND_DURATION_SECONDS,
            )
        )
        play(appContext, uriString, durationSeconds)
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
        play(
            context = context,
            uriString = uriString,
            durationSeconds = readConfiguredDurationSeconds(context),
            onError = onError,
        )
    }

    fun play(
        context: Context,
        uriString: String?,
        durationSeconds: Int,
        onError: ((Throwable?) -> Unit)? = null,
    ) {
        if (uriString.isNullOrBlank()) return
        stop()
        val safeDurationSeconds = sanitizeCustomStartupSoundDurationSeconds(durationSeconds)

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
                        scheduleStop(it, safeDurationSeconds)
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
        clearScheduledStop()
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
            clearScheduledStop()
            runCatching { source?.close() }
            source = null
        }
        mediaPlayer.release()
    }

    private fun scheduleStop(mediaPlayer: MediaPlayer, durationSeconds: Int) {
        clearScheduledStop()
        val runnable = Runnable {
            if (player === mediaPlayer) {
                stop()
            }
        }
        stopRunnable = runnable
        mainHandler.postDelayed(runnable, durationSeconds * 1000L)
    }

    private fun clearScheduledStop() {
        stopRunnable?.let { mainHandler.removeCallbacks(it) }
        stopRunnable = null
    }

    private fun readConfiguredDurationSeconds(context: Context): Int {
        val appContext = context.applicationContext
        val prefs = appContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return sanitizeCustomStartupSoundDurationSeconds(
            prefs.getInt(
                CUSTOM_STARTUP_SOUND_DURATION_SECONDS_KEY,
                DEFAULT_CUSTOM_STARTUP_SOUND_DURATION_SECONDS,
            )
        )
    }

    private fun notifyError(onError: ((Throwable?) -> Unit)?, throwable: Throwable?) {
        onError ?: return
        mainHandler.post { onError(throwable) }
    }
}
