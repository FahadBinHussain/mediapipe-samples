/*
 * Copyright 2026 FahadBinHussain. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.mediapipe.examples.objectdetection

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.SystemClock

/**
 * Plays a loud siren when a person is detected. A cooldown prevents a person
 * who stays in frame from triggering an endless alarm.
 */
class PersonAlert(context: Context) {

    private val audioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val soundId = soundPool.load(context, R.raw.person_detected, 1)
    private var lastPlayedAt = 0L

    fun trigger() {
        val now = SystemClock.elapsedRealtime()
        if (now - lastPlayedAt < BEEP_COOLDOWN_MS) return
        lastPlayedAt = now

        // Force the alarm stream to max volume so the siren is as loud as
        // the device can go, regardless of media volume
        val maxAlarmVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
        audioManager.setStreamVolume(
            AudioManager.STREAM_ALARM,
            maxAlarmVolume,
            AudioManager.FLAG_SHOW_UI
        )

        soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }

    companion object {
        const val BEEP_COOLDOWN_MS = 3000L
    }
}