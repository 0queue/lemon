package dev.thomasharris.lemon.core.data

import dev.thomasharris.lemon.core.database.LobstersDatabase
import dev.thomasharris.lemon.lobstersapi.LobstersService
import javax.inject.Inject

class PageRepository @Inject constructor(
    private val lobstersService: LobstersService,
    private val lobstersDatabase: LobstersDatabase,
) {
    // alright I guess I need to deal with paging soon
}
