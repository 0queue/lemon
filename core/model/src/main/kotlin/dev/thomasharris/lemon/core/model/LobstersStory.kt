package dev.thomasharris.lemon.core.model

import kotlinx.datetime.Instant

data class LobstersStory(
    val shortId: ShortId,
    val createdAt: Instant,
    val title: String,
    val url: String,
    val score: Int,
    val commentCount: Int,
    val description: String,
    val submitter: LobstersUser,
    val tags: List<String>,
    // TODO figure out how to best have optional page indices.  Only front page needs them,
    //      comments for example could be deep linked to and not have any
    val pageIndex: Int?,
    val pageSubIndex: Int?,
)
