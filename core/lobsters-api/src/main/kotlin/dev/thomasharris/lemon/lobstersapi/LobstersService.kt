package dev.thomasharris.lemon.lobstersapi

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class LobstersService(
    private val client: HttpClient,
) {
    suspend fun getPage(
        index: Int,
    ): Result<List<StoryNetworkEntity>, Throwable> = runSuspendCatching {
        client.get("page/$index.json").body()
    }

    suspend fun getStory(
        shortId: ShortId,
    ): Result<Pair<StoryNetworkEntity, List<CommentNetworkEntity>>, Throwable> =
        runSuspendCatching {
            val story = client.get("s/$shortId.json")
                .body<StoryNetworkEntity>()

            val comments = story.comments!! // TODO real errors

            story to comments
        }

    suspend fun getUser(
        username: String,
    ): Result<UserNetworkEntity, Throwable> = runSuspendCatching {
        client.get("u/$username.json").body()
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
