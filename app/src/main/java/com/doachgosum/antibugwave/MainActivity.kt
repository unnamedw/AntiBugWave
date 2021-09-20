package com.doachgosum.antibugwave

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.doachgosum.antibugwave.databinding.ActivityMainBinding
import kotlin.experimental.and
import kotlin.math.sin


class MainActivity : AppCompatActivity() {
    // originally from http://marblemice.blogspot.com/2010/04/generate-and-play-tone-in-android.html
    // and modified by Steve Pomeroy <steve@staticfree.info>
    private val duration = 60 // seconds

    private val sampleRate = 8000 // 초당 샘플링 빈도 수
    private val numSamples = duration * sampleRate // 초당 총 샘플링 수
    private val sample = DoubleArray(numSamples) // 샘플링 값
    private val freqOfTone = 440.0 // hz


    private val generatedSnd = ByteArray(2 * numSamples)

    var handler: Handler = Handler()

    private val thread = Thread {
        genTone()
        handler.post { playSound() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnOn.setOnClickListener {
            thread.start()
        }

        binding.btnOff.setOnClickListener {
            thread.interrupt()
        }
    }

    private fun genTone() {
        // fill out the array
        for (i in 0 until numSamples) {
            sample[i] = sin(2 * Math.PI * i / (sampleRate / freqOfTone))
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        var idx = 0
        for (dVal in sample) {
            // scale to maximum amplitude
            val `val` = (dVal * 32767).toShort()
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (`val`.and(0x00ff)).toByte()
            generatedSnd[idx++] = (`val`.and(0xff00.toShort())).toInt().ushr(8).toByte()
        }
    }

    private fun playSound() {
        val audioTrack = AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.size,
                AudioTrack.MODE_STATIC)
        audioTrack.write(generatedSnd, 0, generatedSnd.size)
        audioTrack.play()
    }
}
