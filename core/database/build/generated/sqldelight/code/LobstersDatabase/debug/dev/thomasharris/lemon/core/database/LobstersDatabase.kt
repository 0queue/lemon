package dev.thomasharris.lemon.core.database

import app.cash.sqldelight.Transacter
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import dev.thomasharris.lemon.core.database.database.newInstance
import dev.thomasharris.lemon.core.database.database.schema

public interface LobstersDatabase : Transacter {
    public val lobstersQueries: LobstersQueries

    public companion object {
        public val Schema: SqlSchema
            get() = LobstersDatabase::class.schema

        public operator fun invoke(
            driver: SqlDriver,
            commentAdapter: Comment.Adapter,
            storyAdapter: Story.Adapter,
            userAdapter: User.Adapter,
        ): LobstersDatabase = LobstersDatabase::class.newInstance(
            driver,
            commentAdapter,
            storyAdapter,
            userAdapter,
        )
    }
}
