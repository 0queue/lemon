package dev.thomasharris.lemon.core.database.database

import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import dev.thomasharris.lemon.core.database.Comment
import dev.thomasharris.lemon.core.database.LobstersDatabase
import dev.thomasharris.lemon.core.database.LobstersQueries
import dev.thomasharris.lemon.core.database.Story
import dev.thomasharris.lemon.core.database.User
import kotlin.Int
import kotlin.Unit
import kotlin.reflect.KClass

internal val KClass<LobstersDatabase>.schema: SqlSchema
    get() = LobstersDatabaseImpl.Schema

internal fun KClass<LobstersDatabase>.newInstance(
    driver: SqlDriver,
    commentAdapter: Comment.Adapter,
    storyAdapter: Story.Adapter,
    userAdapter: User.Adapter,
): LobstersDatabase = LobstersDatabaseImpl(driver, commentAdapter, storyAdapter, userAdapter)

private class LobstersDatabaseImpl(
    driver: SqlDriver,
    commentAdapter: Comment.Adapter,
    storyAdapter: Story.Adapter,
    userAdapter: User.Adapter,
) : TransacterImpl(driver), LobstersDatabase {
    public override val lobstersQueries: LobstersQueries = LobstersQueries(
        driver,
        storyAdapter,
        userAdapter,
        commentAdapter,
    )

    public object Schema : SqlSchema {
        public override val version: Int
            get() = 1

        public override fun create(driver: SqlDriver): QueryResult<Unit> {
            driver.execute(
                null,
                """
          |CREATE TABLE story (
          |    shortId TEXT NOT NULL UNIQUE PRIMARY KEY,
          |    title TEXT NOT NULL,
          |    createdAt INTEGER NOT NULL,
          |    url TEXT NOT NULL,
          |    score INTEGER NOT NULL,
          |    commentCount INTEGER NOT NULL,
          |    description TEXT NOT NULL,
          |    username TEXT NOT NULL,
          |    tags TEXT NOT NULL,
          |    pageIndex INTEGER NOT NULL,
          |    pageSubIndex INTEGER NOT NULL,
          |    insertedAt INTEGER NOT NULL
          |)
                """.trimMargin(),
                0,
            )
            driver.execute(
                null,
                """
          |CREATE TABLE user (
          |    username TEXT NOT NULL UNIQUE PRIMARY KEY,
          |    createdAt INTEGER NOT NULL,
          |    isAdmin INTEGER NOT NULL,
          |    about TEXT NOT NULL,
          |    isModerator INTEGER NOT NULL,
          |    karma INTEGER NOT NULL,
          |    avatarShortUrl TEXT NOT NULL,
          |    invitedByUser TEXT,
          |    insertedAt INTEGER NOT NULL,
          |    githubUsername TEXT,
          |    twitterUsername TEXT
          |)
                """.trimMargin(),
                0,
            )
            driver.execute(
                null,
                """
          |CREATE TABLE comment (
          |    shortId TEXT NOT NULL UNIQUE PRIMARY KEY,
          |    storyId TEXT NOT NULL,
          |    commentIndex INTEGER NOT NULL,
          |    shortIdUrl TEXT NOT NULL,
          |    createdAt INTEGER NOT NULL,
          |    updatedAt INTEGER NOT NULL,
          |    isDeleted INTEGER NOT NULL,
          |    isModerated INTEGER NOT NULL,
          |    score INTEGER NOT NULL,
          |    comment TEXT NOT NULL,
          |    indentLevel INTEGER NOT NULL,
          |    username TEXT NOT NULL,
          |    insertedAt INTEGER NOT NULL
          |--     status INTEGER AS CommentStatus NOT NULL
          |)
                """.trimMargin(),
                0,
            )
            return QueryResult.Unit
        }

        public override fun migrate(
            driver: SqlDriver,
            oldVersion: Int,
            newVersion: Int,
        ): QueryResult<Unit> = QueryResult.Unit
    }
}
