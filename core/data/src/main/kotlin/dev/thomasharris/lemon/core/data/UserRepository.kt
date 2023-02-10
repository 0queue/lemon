package dev.thomasharris.lemon.core.data

import app.cash.sqldelight.coroutines.asFlow
import dev.thomasharris.lemon.core.database.LobstersDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

// TODO refreshing functions? eh
@Singleton
class UserRepository @Inject constructor(
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
}
