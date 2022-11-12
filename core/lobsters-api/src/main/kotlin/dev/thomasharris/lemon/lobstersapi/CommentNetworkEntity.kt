package dev.thomasharris.lemon.lobstersapi

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentNetworkEntity(
    @SerialName("short_id") val shortId: ShortId,
    @SerialName("short_id_url") val shortIdUrl: String,
    @SerialName("created_at") val createdAt: Instant,
    @SerialName("updated_at") val updatedAt: Instant,
    @SerialName("is_deleted") val isDeleted: Boolean,
    @SerialName("is_moderated") val isModerated: Boolean,
    val score: Int,
    val comment: String,
    val url: String,
    @SerialName("indent_level") val indentLevel: Int, // starts at 1
    @SerialName("commenting_user") val commentingUser: UserNetworkEntity,
)
