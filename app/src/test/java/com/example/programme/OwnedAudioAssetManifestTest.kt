package com.example.programme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OwnedAudioAssetManifestTest {
    @Test
    fun readyAssetWithoutApplicationRightsIsNotPlayable() {
        val asset = OwnedAudioAsset(
            id = "test-asset",
            briefId = "supermarket",
            path = "res/raw/test.ogg",
            transcriptPath = "assets/test.txt",
            speakers = listOf("speaker-a", "speaker-b"),
            region = "Standarddeutsch",
            status = AudioAssetStatus.READY,
            applicationUseReleased = false,
            transcriptVerified = true
        )

        assertTrue(OwnedAudioAssetManifest.validate(asset).contains("Anwendungsfreigabe fehlt."))
        assertEquals(0, OwnedAudioAssetManifest.playableAssets(listOf(asset)).size)
    }
}
