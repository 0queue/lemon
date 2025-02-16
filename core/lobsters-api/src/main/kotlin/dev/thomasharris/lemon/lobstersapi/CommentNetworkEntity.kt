package dev.thomasharris.lemon.lobstersapi

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentNetworkEntity(
    @SerialName("short_id") val shortId: ShortId,
    @SerialName("short_id_url") val shortIdUrl: String,
    @SerialName("created_at") val createdAt: Instant,
    @SerialName("last_edited_at") val updatedAt: Instant,
    @SerialName("is_deleted") val isDeleted: Boolean,
    @SerialName("is_moderated") val isModerated: Boolean,
    val score: Int,
    val comment: String,
    val url: String,
    @SerialName("depth") val indentLevel: Int, // starts at 0
    @SerialName("commenting_user") val commentingUser: String,
)
