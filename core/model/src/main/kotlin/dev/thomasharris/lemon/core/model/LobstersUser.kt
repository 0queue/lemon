package dev.thomasharris.lemon.core.model

import kotlinx.datetime.Instant

data class LobstersUser(
    val username: String,
    val createdAt: Instant,
    val isAdmin: Boolean,
    val about: String,
    val isModerator: Boolean,
    val karma: Int,
    val avatarUrl: String,
    val invitedByUser: String?,
    val githubUsername: String?,
    val twitterUsername: String?,
) {
    val fullAvatarUrl = "https://lobste.rs/$avatarUrl"
}
