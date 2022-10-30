package dev.thomasharris.lemon.core.data

import app.cash.sqldelight.paging3.QueryPagingSource
import dev.thomasharris.lemon.core.database.LobstersDatabase
import dev.thomasharris.lemon.lobstersapi.LobstersService
import kotlinx.coroutines.Dispatchers

class PageRepository(
    private val lobstersService: LobstersService,
    private val lobstersDatabase: LobstersDatabase,
) {
    // alright I guess I need to deal with paging soon

    val pagingSource = QueryPagingSource(
        countQuery = lobstersDatabase.lobstersQueries.countStories(),
        transacter = lobstersDatabase.lobstersQueries,
        context = Dispatchers.IO,
    ) { limit, offset ->
        // todo custom mapper here?
        lobstersDatabase.lobstersQueries.getStories(limit, offset)
    }
}
