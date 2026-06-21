package com.example.newsapp.presentation.details

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * Wraps Android [TextToSpeech] for reading an article aloud. Tracks a [isSpeaking] flag so the
 * detail UI can toggle the play/stop affordance, and shuts the engine down with the composition.
 */
class ArticleSpeaker(context: Context) {

    private var ready = false
    private var pending: String? = null

    var isSpeaking by mutableStateOf(false)
        private set

    private lateinit var tts: TextToSpeech

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.getDefault()
                ready = true
                pending?.let { speak(it) }
                pending = null
            }
        }
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) { isSpeaking = true }
            override fun onDone(utteranceId: String?) { isSpeaking = false }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) { isSpeaking = false }
        })
    }

    /** Starts reading [text]; if already speaking, stops instead (toggle behavior). */
    fun toggle(text: String) {
        if (isSpeaking) stop() else speak(text)
    }

    private fun speak(text: String) {
        if (!ready) {
            pending = text
            return
        }
        isSpeaking = true
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "article")
    }

    fun stop() {
        tts.stop()
        isSpeaking = false
    }

    fun shutdown() {
        tts.shutdown()
    }
}

/** Remembers an [ArticleSpeaker] tied to the current context and disposes it with the composition. */
@Composable
fun rememberArticleSpeaker(): ArticleSpeaker {
    val context = LocalContext.current
    val speaker = remember { ArticleSpeaker(context) }
    DisposableEffect(speaker) {
        onDispose { speaker.shutdown() }
    }
    return speaker
}
