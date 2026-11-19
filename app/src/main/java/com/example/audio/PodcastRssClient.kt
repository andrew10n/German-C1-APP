package com.example.audio

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URL

data class PodcastFeedSpec(
    val id: String,
    val name: String,
    val categoryDe: String,
    val rssUrl: String,
    val websiteUrl: String
)

data class PodcastEpisodeItem(
    val feedId: String,
    val sourceName: String,
    val title: String,
    val audioUrl: String,
    val pubDate: String,
    val durationLabel: String,
    val summaryDe: String
)

object PodcastRssClient {

    val feeds: List<PodcastFeedSpec> = listOf(
        PodcastFeedSpec(
            id = "ams_moove",
            name = "auto motor und sport · Moove",
            categoryDe = "Antrieb, E-Mobilität & Fahrzeugtechnik",
            rssUrl = "https://moove-der-mobilitaetspodcast.podigee.io/feed/mp3",
            websiteUrl = "https://www.auto-motor-und-sport.de/podcast/"
        ),
        PodcastFeedSpec(
            id = "rasenfunk",
            name = "Rasenfunk · Fußball-Taktik & Liga",
            categoryDe = "Taktikanalyse, BVB & DFB",
            rssUrl = "https://rasenfunk.de/feed/rss",
            websiteUrl = "https://rasenfunk.de/"
        ),
        PodcastFeedSpec(
            id = "swr_wissen",
            name = "SWR Kultur · Das Wissen",
            categoryDe = "Wissenschaft, Industrie & Südwest-Kontext",
            rssUrl = "https://www.swr.de/~rss/swr2/wissen/podcast-swr2-wissen-100.xml",
            websiteUrl = "https://www.swr.de/swrkultur/wissen/index.html"
        ),
        PodcastFeedSpec(
            id = "tagesschau_100s",
            name = "ARD Tagesschau in 100 Sekunden",
            categoryDe = "Schnelles Nachrichten-Deutsch (100 Sek.)",
            rssUrl = "https://www.tagesschau.de/multimedia/audio/ondemand100~podcast.xml",
            websiteUrl = "https://www.tagesschau.de/multimedia/sendung/tagesschau_in_100_sekunden"
        ),
        PodcastFeedSpec(
            id = "dlf_der_tag",
            name = "Deutschlandfunk · Der Tag",
            categoryDe = "Gesellschaft, Politik & Goethe-C1-Debatte",
            rssUrl = "https://www.deutschlandfunk.de/der-tag-100.xml",
            websiteUrl = "https://www.deutschlandfunk.de/der-tag-100.html"
        )
    )

    fun feedForId(feedId: String): PodcastFeedSpec =
        feeds.find { it.id.equals(feedId, ignoreCase = true) } ?: feeds.first()

    suspend fun fetchEpisodes(
        context: Context,
        feedId: String,
        forceRefresh: Boolean = false
    ): Result<List<PodcastEpisodeItem>> = withContext(Dispatchers.IO) {
        val feed = feedForId(feedId)
        val cacheDir = File(context.cacheDir, "rss_feeds").apply { mkdirs() }
        val cacheFile = File(cacheDir, "${feed.id}.xml")
        val maxAgeMs = 6L * 3_600_000L

        if (!forceRefresh && cacheFile.exists() && (System.currentTimeMillis() - cacheFile.lastModified()) < maxAgeMs) {
            val cachedParsed = runCatching { parseRssXml(cacheFile.readText(), feed) }.getOrNull()
            if (!cachedParsed.isNullOrEmpty()) {
                return@withContext Result.success(cachedParsed)
            }
        }

        val networkResult = runCatching {
            val conn = (URL(feed.rssUrl).openConnection() as HttpURLConnection).apply {
                connectTimeout = 10_000
                readTimeout = 12_000
                instanceFollowRedirects = true
                setRequestProperty("User-Agent", "GermanC1MasteryApp/2.0 (Personal)")
                setRequestProperty("Accept", "application/rss+xml, application/xml, text/xml")
            }
            try {
                val code = conn.responseCode
                if (code !in 200..299) {
                    error("HTTP $code beim Laden des RSS-Feeds (${feed.name})")
                }
                val xml = conn.inputStream.bufferedReader().use { it.readText() }
                val episodes = parseRssXml(xml, feed)
                if (episodes.isNotEmpty()) {
                    cacheFile.writeText(xml)
                }
                episodes
            } finally {
                conn.disconnect()
            }
        }

        if (networkResult.isSuccess && networkResult.getOrNull()?.isNotEmpty() == true) {
            return@withContext networkResult
        }

        if (cacheFile.exists()) {
            val stale = runCatching { parseRssXml(cacheFile.readText(), feed) }.getOrNull()
            if (!stale.isNullOrEmpty()) {
                return@withContext Result.success(stale)
            }
        }

        val err = networkResult.exceptionOrNull()
        Result.failure(err ?: IllegalStateException("Keine Audio-Episoden im Feed gefunden."))
    }

    fun parseRssXml(xml: String, feed: PodcastFeedSpec): List<PodcastEpisodeItem> {
        val factory = XmlPullParserFactory.newInstance().apply { isNamespaceAware = false }
        val parser = factory.newPullParser()
        parser.setInput(StringReader(xml))

        val items = mutableListOf<PodcastEpisodeItem>()
        var insideItem = false
        var title = ""
        var audioUrl = ""
        var pubDate = ""
        var duration = ""
        var description = ""
        var currentTag = ""

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    val tag = parser.name.lowercase()
                    currentTag = tag
                    if (tag == "item") {
                        insideItem = true
                        title = ""
                        audioUrl = ""
                        pubDate = ""
                        duration = ""
                        description = ""
                    } else if (insideItem && tag == "enclosure") {
                        val encUrl = parser.getAttributeValue(null, "url").orEmpty()
                        val encType = parser.getAttributeValue(null, "type").orEmpty()
                        if (encUrl.isNotBlank() && (encType.contains("audio", true) || encUrl.contains(".mp3", true) || encUrl.contains(".m4a", true) || encType.isBlank())) {
                            audioUrl = encUrl
                        }
                    }
                }
                XmlPullParser.TEXT -> {
                    if (insideItem) {
                        val text = parser.text?.trim().orEmpty()
                        if (text.isNotEmpty()) {
                            when (currentTag) {
                                "title" -> title += text
                                "pubdate" -> pubDate += text
                                "itunes:duration", "duration" -> duration += text
                                "description", "itunes:summary" -> if (description.length < 280) description += text
                            }
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    val tag = parser.name.lowercase()
                    if (tag == "item" && insideItem) {
                        insideItem = false
                        if (title.isNotBlank() && audioUrl.isNotBlank()) {
                            val cleanDesc = description
                                .replace(Regex("<[^>]*>"), " ")
                                .replace(Regex("\\s+"), " ")
                                .trim()
                                .take(200)
                            items += PodcastEpisodeItem(
                                feedId = feed.id,
                                sourceName = feed.name,
                                title = title.trim(),
                                audioUrl = audioUrl.trim(),
                                pubDate = pubDate.take(16).trim(),
                                durationLabel = duration.ifBlank { "Live-MP3" },
                                summaryDe = cleanDesc
                            )
                        }
                    }
                    currentTag = ""
                }
            }
            eventType = parser.next()
        }
        return items.take(8)
    }
}
