package dev.thomasharris.lemon.lobstersapi

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StoryNetworkEntity(
    @SerialName("short_id")
    val shortId: ShortId,
    @SerialName("short_id_url")
    val shortIdUrl: String,
    // these instants do not preserve timezone but I don't really need them
    @SerialName("created_at")
    val createdAt: Instant,
    val title: String,
    val url: String,
    val score: Int,
    @SerialName("comment_count")
    val commentCount: Int,
    val description: String,
    @SerialName("submitter_user")
    val submitter: String,
    val tags: List<String>,
    @SerialName("user_is_author")
    var userIsAuthor: Boolean,
    // if null, this is the dehydrated model from a page
    val comments: List<CommentNetworkEntity>? = null,
)
