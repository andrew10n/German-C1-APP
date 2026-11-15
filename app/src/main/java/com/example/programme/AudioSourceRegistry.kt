package com.example.programme

enum class AudioAccess {
    PUBLIC_DOWNLOAD,
    REGISTERED_RESEARCH_ACCESS,
    AUTHORIZED_STREAMING_ONLY,
    COMMISSION_REQUIRED,
    UNKNOWN_REQUIRES_RIGHTSHOLDER_CONFIRMATION
}

data class AudioSourceRecord(
    val id: String,
    val name: String,
    val contentType: String,
    val format: String,
    val transcriptAvailability: String,
    val speakerMetadata: String,
    val access: AudioAccess,
    val redistribution: String,
    val androidUse: String,
    val action: String
)

object AudioSourceRegistry {
    val sources = listOf(
        AudioSourceRecord(
            "ids-folk", "IDS FOLK", "Spontaneous German conversations",
            "Corpus-controlled audio; verify file format per access release",
            "Transcripts and corpus annotations through IDS access",
            "Conversation roles and corpus metadata available",
            AudioAccess.REGISTERED_RESEARCH_ACCESS,
            "No redistribution by default; terms/user agreement apply",
            "Do not bundle or mirror in the app without written permission",
            "Request an application licence from IDS"
        ),
        AudioSourceRecord(
            "ids-dgd", "Database of Spoken German (DGD)", "Spoken German corpus portal",
            "Portal-delivered corpus audio; format depends on collection",
            "Collection-dependent transcripts and annotations",
            "Collection metadata available through the portal",
            AudioAccess.REGISTERED_RESEARCH_ACCESS,
            "Research access is not an app redistribution licence",
            "Use only through an authorized access method",
            "Confirm DGD terms for commercial/private Android use"
        ),
        AudioSourceRecord(
            "ids-pfeffer", "Pfeffer-Korpus", "German regional/informal spoken language",
            "Corpus-controlled audio",
            "Corpus transcripts/annotations where released",
            "Regional and conversational metadata where released",
            AudioAccess.REGISTERED_RESEARCH_ACCESS,
            "No third-party redistribution by default",
            "Do not embed recordings without written permission",
            "Request a specific licence"
        ),
        AudioSourceRecord(
            "ids-zwirner", "Zwirner-Korpus", "Regional and dialect speech",
            "Archive-controlled audio",
            "Archive-dependent transcript/annotation availability",
            "Speaker and region metadata subject to access terms",
            AudioAccess.REGISTERED_RESEARCH_ACCESS,
            "Redistribution requires rightsholder permission",
            "External authorized access only until licensed",
            "Request permission and regional-use terms"
        ),
        AudioSourceRecord(
            "ids-gewiss", "GeWiss", "Spoken academic German",
            "Corpus-controlled audio",
            "Transcripts and academic metadata through corpus access",
            "Academic setting, speaker role and task metadata",
            AudioAccess.REGISTERED_RESEARCH_ACCESS,
            "Do not assume research access permits app bundling",
            "External authorized access only",
            "Request an app licence from the rightsholders"
        ),
        AudioSourceRecord(
            "common-voice-de", "Mozilla Common Voice German", "Short contributed speech samples",
            "Downloaded clips; release-specific format",
            "Sentence text and release metadata",
            "Contributor metadata is limited and release-dependent",
            AudioAccess.PUBLIC_DOWNLOAD,
            "Verify the release datasheet; CC0 releases permit reuse",
            "Suitable for short samples, not coherent dialogue by itself",
            "Use with release/version and attribution metadata"
        ),
        AudioSourceRecord(
            "goethe-c1", "Goethe-Zertifikat C1 practice audio", "Exam practice listening",
            "Goethe-controlled media",
            "Official task transcripts/materials where supplied",
            "Exam/task metadata",
            AudioAccess.AUTHORIZED_STREAMING_ONLY,
            "Copyrighted; no bundling without written permission",
            "Link to authorized material only",
            "Seek Goethe licensing or use official links"
        )
    )
}
