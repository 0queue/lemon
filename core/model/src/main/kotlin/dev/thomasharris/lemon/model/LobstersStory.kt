package dev.thomasharris.lemon.model

import kotlinx.datetime.Instant

data class LobstersStory(
    val shortId: ShortId,
    val shortIdUrl: String,
    val createdAt: Instant,
    val title: String,
    val url: String,
    val score: Int,
    val commentCount: Int,
    val description: String,
    val submitter: LobstersUser,
    val tags: List<String>,
    val comments: List<LobstersComment>,
)