package dev.thomasharris.lemon.core.data

import app.cash.sqldelight.coroutines.asFlow
import com.github.michaelbull.result.coroutines.binding.binding
import dev.thomasharris.lemon.core.database.LobstersDatabase
import dev.thomasharris.lemon.lobstersapi.LobstersService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

// TODO refreshing functions? eh
@Singleton
class UserRepository @Inject constructor(
    private val lobstersService: LobstersService,
    private val lobstersDatabase: LobstersDatabase,
) {
    fun userFlow(username: String) = lobstersDatabase
        .userQueries
        .getUser(
            username = username,
            mapper = userMapper,
        )
        .asFlow()
        .map { query ->
            withContext(Dispatchers.IO) {
                query.executeAsOneOrNull()
            }
        }

    suspend fun refreshUser(username: String) = withContext(Dispatchers.IO) {
        binding {
            val user = lobstersService
                .getUser(username)
                .bind()

            lobstersDatabase.userQueries.insertUser(user.asDbUser())
        }
    }
}
