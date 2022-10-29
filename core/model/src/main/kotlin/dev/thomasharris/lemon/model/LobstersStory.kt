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
    // if null, this is the dehydrated model from a page
    val comments: List<LobstersComment>?,
)
