package dev.thomasharris.lemon.lobstersapi

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import dev.thomasharris.lemon.model.LobstersComment
import dev.thomasharris.lemon.model.LobstersStory
import dev.thomasharris.lemon.model.LobstersUser
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class LobstersService(
    private val client: HttpClient,
) {
    suspend fun getPage(
        index: Int,
    ): Result<List<LobstersStory>, Throwable> = runSuspendCatching {
        client.get("page/$index.json")
            .body<List<StoryNetworkEntity>>()
            .map(StoryNetworkEntity::asModel)
    }
}

fun LobstersService(): LobstersService = LobstersService(
    client = HttpClient(Android) {
        defaultRequest {
            url("https://lobste.rs/")
        }

        install(ContentNegotiation) {
            @Suppress("JSON_FORMAT_REDUNDANT")
            Json {
                ignoreUnknownKeys = true
            }.let(::json)
        }
    },
)

typealias ShortId = String

@Serializable
data class StoryNetworkEntity(
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
    val comments: List<CommentNetworkEntity>? = null,
)

fun StoryNetworkEntity.asModel(): LobstersStory = LobstersStory(
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
    comments = comments?.map(CommentNetworkEntity::asModel) ?: emptyList(),
)

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

fun CommentNetworkEntity.asModel() = LobstersComment(
    shortId = shortId,
    shortIdUrl = shortIdUrl,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isDeleted = isDeleted,
    isModerated = isModerated,
    score = score,
    comment = comment,
    url = url,
    indentLevel = indentLevel,
    commentingUser = commentingUser.asModel(),
)

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

fun UserNetworkEntity.asModel() = LobstersUser(
    username = username,
    createdAt = createdAt,
    isAdmin = isAdmin,
    about = about,
    isModerator = isModerator,
    karma = karma,
    avatarUrl = avatarUrl,
    invitedByUser = invitedByUser,
    githubUsername = githubUsername,
    twitterUsername = twitterUsername,
)

@Serializable
data class TagNetworkEntity(
    val id: Int,
    val tag: String,
    val description: String,
    val privileged: Boolean,
    @SerialName("is_media") val isMedia: Boolean,
    val inactive: Boolean,
    @SerialName("hotness_mod") val hotnessMod: Float,
)
