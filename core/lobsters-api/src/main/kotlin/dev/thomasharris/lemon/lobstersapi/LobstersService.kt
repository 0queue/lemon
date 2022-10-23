package dev.thomasharris.lemon.lobstersapi

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import kotlinx.coroutines.delay

class LobstersService(
    private val client: HttpClient,
) {
    suspend fun getPage(index: Int): String {
        delay(2000)
        return "things from lobsters"
    }
}

fun LobstersService(): LobstersService = LobstersService(
    client = HttpClient(Android),
)
