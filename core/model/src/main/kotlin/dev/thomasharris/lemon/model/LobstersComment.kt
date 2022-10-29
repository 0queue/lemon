package dev.thomasharris.lemon.model

import kotlinx.datetime.Instant

data class LobstersComment(
    val shortId: ShortId,
    val shortIdUrl: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isDeleted: Boolean,
    val isModerated: Boolean,
    val score: Int,
    val comment: String,
    val url: String,
    val indentLevel: Int, // starts at 1
    val commentingUser: LobstersUser,
)