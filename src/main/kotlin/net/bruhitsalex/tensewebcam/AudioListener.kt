package net.bruhitsalex.tensewebcam

import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine
import javax.swing.JOptionPane
import kotlin.math.pow
import kotlin.system.exitProcess

private const val THRESHOLD_TALKING = 25

object AudioListener {

    init {
        println("Starting audio listener")

        val format = AudioFormat(8000f, 16, 1, true, true)
        val info = DataLine.Info(TargetDataLine::class.java, format)

        if (!AudioSystem.isLineSupported(info)) {
            JOptionPane.showMessageDialog(null, "Audio is not supported on this device", "Tense Webcam", JOptionPane.ERROR_MESSAGE)
            exitProcess(0)
        }

        val mic = AudioSystem.getLine(info) as TargetDataLine
        mic.open(format)
        mic.start()

        val buffer = ByteArray(mic.bufferSize / 5)

        var lastPeriod = System.currentTimeMillis()
        var talkingMoment = 0L
        var totalMoments = 0L

        var lastTalking = false

        while (true) {
            mic.read(buffer, 0, buffer.size)
            val avg = calculateRMSLevel(buffer)

            totalMoments++
            if (avg > THRESHOLD_TALKING) {
                talkingMoment++
            }

            if (System.currentTimeMillis() - lastPeriod >= 1000) {
                lastPeriod = System.currentTimeMillis()
                val isTalking = talkingMoment.toDouble() / totalMoments.toDouble() >= 0.5
                if (isTalking != lastTalking) {
                    lastTalking = isTalking
                    Websocket.setTalking(isTalking)
                }

                talkingMoment = 0
                totalMoments = 0
            }
        }
    }

    private fun calculateRMSLevel(audioData: ByteArray): Double {
        var lSum: Long = 0
        for (i in audioData.indices) {
            lSum += audioData[i]
        }
        val dAvg = (lSum / audioData.size).toDouble()
        var sumMeanSquare = 0.0
        for (j in audioData.indices) {
            sumMeanSquare += (audioData[j] - dAvg).pow(2.0)
        }
        val averageMeanSquare = sumMeanSquare / audioData.size
        return averageMeanSquare.pow(0.5) + 0.5
    }

}