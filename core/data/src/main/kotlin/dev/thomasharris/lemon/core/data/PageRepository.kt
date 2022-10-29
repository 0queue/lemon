package dev.thomasharris.lemon.core.data

import dev.thomasharris.lemon.core.database.LobstersDatabase
import dev.thomasharris.lemon.lobstersapi.LobstersService

class PageRepository(
    val lobstersService: LobstersService,
    val lobstersDatabase: LobstersDatabase,
) {
    // alright I guess I need to deal with paging soon
}