package com.example.programme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PhaseFoundationTest {
    @Test
    fun audioSourcesKeepRestrictedCorporaOutOfBundledAppPath() {
        val restricted = AudioSourceRegistry.sources.filter {
            it.access == AudioAccess.REGISTERED_RESEARCH_ACCESS ||
                it.access == AudioAccess.AUTHORIZED_STREAMING_ONLY
        }
        assertTrue(restricted.isNotEmpty())
        assertTrue(restricted.all {
            it.androidUse.isNotBlank() &&
                (it.action.contains("request", true) ||
                    it.action.contains("confirm", true) ||
                    it.action.contains("seek", true) ||
                    it.action.contains("licence", true) ||
                    it.action.contains("permission", true))
        })
    }

    @Test
    fun ownedDialogueBriefsCoverPersonalContextsAndRequireRights() {
        assertEquals(setOf(ContextTrack.GENERAL, ContextTrack.SOCIAL,
            ContextTrack.ENGINEERING, ContextTrack.COACHING),
            OwnedDialogueLibrary.briefs.map { it.context }.toSet())
        assertTrue(OwnedDialogueLibrary.briefs.all {
            RecordingRequirement.APPLICATION_USE_RELEASE in it.requirements &&
                it.speakers >= 2
        })
    }

    @Test
    fun diagnosticAndMasteryContractsCoverAllDimensionsWithDelayedTransfer() {
        assertEquals(DiagnosticDimension.values().toSet(),
            DiagnosticTaskBank.contracts.map { it.dimension }.toSet())
        assertTrue(MasteryAssessmentContracts.checkpoints.all {
            it.requiredIntervalsDays == listOf(1, 3, 7, 21) &&
                it.requiresUnfamiliarTransfer
        })
    }

    @Test
    fun goetheContractsKeepModulesSeparateFromFunctionalMastery() {
        assertEquals(GoetheModule.values().toSet(), GoetheC1TaskContracts.modules.map { it.module }.toSet())
        assertTrue(GoetheC1TaskContracts.modules.all { !it.usesProtectedMaterial })
    }

    @Test
    fun pilotCoversBaselineWeeklyAndFinalEvidence() {
        assertEquals(listOf(0, 1, 2, 3), FourWeekPilotProtocol.weeks.map { it.week })
        assertTrue(FourWeekPilotProtocol.weeks.last().requiredEvidence.contains(AssessmentEvidence.UNFAMILIAR_TRANSFER))
    }

    @Test
    fun audioManifestBlocksUnreleasedAssetsFromPlayback() {
        val asset = OwnedAudioAsset(
            id = "supermarket-01",
            briefId = "supermarket",
            path = "audio/supermarket-01.wav",
            transcriptPath = "audio/supermarket-01.txt",
            speakers = listOf("speaker-a", "speaker-b"),
            region = "Baden-Württemberg",
            status = AudioAssetStatus.READY,
            applicationUseReleased = false,
            transcriptVerified = true
        )

        assertTrue(OwnedAudioAssetManifest.playableAssets(listOf(asset)).isEmpty())
        assertTrue(OwnedAudioAssetManifest.validate(asset).any { it.contains("Freigabe", true) })
    }
}
