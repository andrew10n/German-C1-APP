package com.example.programme

/** Provider-neutral boundaries. The local app remains usable when no production provider is configured. */
data class AccountIdentity(
    val accountId: String,
    val role: String,
    val verified: Boolean
)

data class SyncEnvelope(
    val recordId: String,
    val recordType: String,
    val schemaVersion: Int,
    val payload: String,
    val updatedAtUtc: Long,
    val deleted: Boolean = false
)

interface AccountProvider {
    suspend fun currentIdentity(): AccountIdentity?
    suspend fun signOut()
}

interface LearningSyncProvider {
    suspend fun push(envelopes: List<SyncEnvelope>): List<String>
    suspend fun pull(sinceUtc: Long?): List<SyncEnvelope>
    suspend fun deleteAccountData(accountId: String)
}

interface CrashReporter {
    fun recordNonSensitiveException(error: Throwable, context: Map<String, String> = emptyMap())
    fun setRelease(versionName: String, versionCode: Long)
}

object LocalOnlyProductionProviders {
    val account: AccountProvider = object : AccountProvider {
        override suspend fun currentIdentity(): AccountIdentity? = null
        override suspend fun signOut() = Unit
    }

    val sync: LearningSyncProvider = object : LearningSyncProvider {
        override suspend fun push(envelopes: List<SyncEnvelope>): List<String> = emptyList()
        override suspend fun pull(sinceUtc: Long?): List<SyncEnvelope> = emptyList()
        override suspend fun deleteAccountData(accountId: String) = Unit
    }

    val crashReporter: CrashReporter = object : CrashReporter {
        override fun recordNonSensitiveException(error: Throwable, context: Map<String, String>) = Unit
        override fun setRelease(versionName: String, versionCode: Long) = Unit
    }
}
