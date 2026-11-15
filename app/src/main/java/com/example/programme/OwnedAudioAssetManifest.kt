package com.example.programme

enum class AudioAssetStatus {
    PLANNED,
    INTAKE,
    TRANSCRIPT_REVIEW,
    RIGHTS_REVIEW,
    READY,
    BLOCKED
}

data class OwnedAudioAsset(
    val id: String,
    val briefId: String,
    val path: String?,
    val transcriptPath: String?,
    val speakers: List<String>,
    val region: String?,
    val status: AudioAssetStatus,
    val applicationUseReleased: Boolean,
    val transcriptVerified: Boolean
)

object OwnedAudioAssetManifest {
    fun validate(asset: OwnedAudioAsset): List<String> = buildList {
        if (asset.id.isBlank()) add("Asset-ID fehlt.")
        if (OwnedDialogueLibrary.briefs.none { it.id == asset.briefId }) add("Dialog-Brief fehlt.")
        if (asset.speakers.size < 2) add("Mindestens zwei Sprecher erforderlich.")
        if (asset.status == AudioAssetStatus.READY && asset.path.isNullOrBlank()) {
            add("Bereites Asset benötigt einen Audiopfad.")
        }
        if (asset.status == AudioAssetStatus.READY && asset.transcriptPath.isNullOrBlank()) {
            add("Bereites Asset benötigt ein Transkript.")
        }
        if (asset.status == AudioAssetStatus.READY && !asset.applicationUseReleased) {
            add("Anwendungsfreigabe fehlt.")
        }
        if (asset.status == AudioAssetStatus.READY && !asset.transcriptVerified) {
            add("Transkript ist noch nicht geprüft.")
        }
    }

    fun playableAssets(assets: List<OwnedAudioAsset>): List<OwnedAudioAsset> =
        assets.filter { validate(it).isEmpty() && it.status == AudioAssetStatus.READY }
}
