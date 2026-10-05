package com.ibm.tic_tac_toe.audio

import android.media.AudioManager
import android.media.ToneGenerator

/**
 * Short feedback tones for placing a mark, winning, and a draw. Uses
 * [ToneGenerator] to synthesize them rather than bundling audio files, so the app
 * ships no sound assets; swap this for a `SoundPool`-backed implementation if real
 * sound effects are added later, without changing any call site.
 *
 * Holds a native resource ([ToneGenerator]) — callers must call [release] once done
 * (e.g. from a Compose `DisposableEffect`).
 */
class GameSoundPlayer {
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, VOLUME_PERCENT)

    fun playPlace() {
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, PLACE_DURATION_MS)
    }

    fun playWin() {
        toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, WIN_DURATION_MS)
    }

    fun playDraw() {
        toneGenerator.startTone(ToneGenerator.TONE_PROP_NACK, DRAW_DURATION_MS)
    }

    fun release() {
        toneGenerator.release()
    }

    private companion object {
        const val VOLUME_PERCENT = 70
        const val PLACE_DURATION_MS = 80
        const val WIN_DURATION_MS = 400
        const val DRAW_DURATION_MS = 250
    }
}
