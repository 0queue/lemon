package dev.thomasharris.lemon.core.model

import kotlinx.datetime.Instant

data class LobstersComment(
    val shortId: ShortId,
    val storyId: String,
    val commentIndex: Int,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isDeleted: Boolean,
    val isModerated: Boolean,
    val score: Int,
    val comment: String,
    val indentLevel: Int, // starts at 1
    val commentingUser: LobstersUser,
    val visibility: Visibility,
    val childCount: Int,
) {
    enum class Visibility {
        VISIBLE,
        COMPACT,
        GONE,
    }
}
