package com.example.audio

import java.nio.ByteBuffer
import java.nio.ByteOrder

object WavPcmEncoder {

    fun extractSampleRateFromMime(mimeType: String?): Int {
        if (mimeType.isNullOrBlank()) return 24_000
        val match = Regex("rate=(\\d+)").find(mimeType)
        return match?.groupValues?.getOrNull(1)?.toIntOrNull()?.coerceIn(8_000, 96_000) ?: 24_000
    }

    fun wrapPcm16MonoToWav(
        pcmBytes: ByteArray,
        sampleRate: Int = 24_000,
        channels: Int = 1,
        bitsPerSample: Int = 16
    ): ByteArray {
        val byteRate = sampleRate * channels * (bitsPerSample / 8)
        val blockAlign = (channels * (bitsPerSample / 8)).toShort()
        val dataSize = pcmBytes.size
        val chunkSize = 36 + dataSize

        val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)
        header.put('R'.code.toByte())
        header.put('I'.code.toByte())
        header.put('F'.code.toByte())
        header.put('F'.code.toByte())
        header.putInt(chunkSize)
        header.put('W'.code.toByte())
        header.put('A'.code.toByte())
        header.put('V'.code.toByte())
        header.put('E'.code.toByte())
        header.put('f'.code.toByte())
        header.put('m'.code.toByte())
        header.put('t'.code.toByte())
        header.put(' '.code.toByte())
        header.putInt(16) // Subchunk1Size for PCM
        header.putShort(1.toShort()) // AudioFormat = 1 (PCM)
        header.putShort(channels.toShort())
        header.putInt(sampleRate)
        header.putInt(byteRate)
        header.putShort(blockAlign)
        header.putShort(bitsPerSample.toShort())
        header.put('d'.code.toByte())
        header.put('a'.code.toByte())
        header.put('t'.code.toByte())
        header.put('a'.code.toByte())
        header.putInt(dataSize)

        val out = ByteArray(44 + dataSize)
        System.arraycopy(header.array(), 0, out, 0, 44)
        System.arraycopy(pcmBytes, 0, out, 44, dataSize)
        return out
    }
}
