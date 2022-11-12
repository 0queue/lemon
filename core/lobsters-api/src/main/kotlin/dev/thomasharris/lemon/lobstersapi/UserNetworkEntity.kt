package dev.thomasharris.lemon.lobstersapi

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserNetworkEntity(
    val username: String,
    @SerialName("created_at") val createdAt: Instant,
    @SerialName("is_admin") val isAdmin: Boolean,
    val about: String,
    @SerialName("is_moderator") val isModerator: Boolean,
    val karma: Int = 0,
    @SerialName("avatar_url") val avatarUrl: String,
    @SerialName("invited_by_user") val invitedByUser: String? = null,
    @SerialName("github_username") val githubUsername: String? = null,
    @SerialName("twitter_username") val twitterUsername: String? = null,
)
