package dev.thomasharris.lemon.lobstersapi

import dev.thomasharris.lemon.model.LobstersStory
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class StoryNetworkEntity(
    @SerialName("short_id") val shortId: ShortId,
    @SerialName("short_id_url") val shortIdUrl: String,
    @SerialName("created_at") val createdAt: Instant,
    val title: String,
    val url: String,
    val score: Int,
    @SerialName("comment_count") val commentCount: Int,
    val description: String,
    @SerialName("submitter_user") val submitter: UserNetworkEntity,
    val tags: List<String>,
    // if null, this is the dehydrated model from a page
    val comments: List<CommentNetworkEntity>? = null,
)

internal fun StoryNetworkEntity.asModel(): LobstersStory = LobstersStory(
    shortId = shortId,
    shortIdUrl = shortIdUrl,
    createdAt = createdAt,
    title = title,
    url = url,
    score = score,
    commentCount = commentCount,
    description = description,
    submitter = submitter.asModel(),
    tags = tags,
    comments = comments?.map(CommentNetworkEntity::asModel),
)
