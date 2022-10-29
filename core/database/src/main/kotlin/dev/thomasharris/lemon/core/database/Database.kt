package dev.thomasharris.lemon.core.database

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

fun LobstersDatabase(context: Context): LobstersDatabase {
    val driver = AndroidSqliteDriver(
        schema = LobstersDatabase.Schema,
        context = context,
        name = "lobsters.db",
    )

    return LobstersDatabase(
        driver,
        storyAdapter = Story.Adapter(
            insertedAtAdapter = InstantAdapter,
            commentCountAdapter = IntAdapter,
            createdAtAdapter = InstantAdapter,
            pageIndexAdapter = IntAdapter,
            pageSubIndexAdapter = IntAdapter,
            scoreAdapter = IntAdapter,
            tagsAdapter = ListStringAdapter,
        ),
        commentAdapter = Comment.Adapter(
            createdAtAdapter = InstantAdapter,
            insertedAtAdapter = InstantAdapter,
            commentIndexAdapter = IntAdapter,
            indentLevelAdapter = IntAdapter,
            scoreAdapter = IntAdapter,
            updatedAtAdapter = InstantAdapter,
        ),
        userAdapter = User.Adapter(
            createdAtAdapter = InstantAdapter,
            insertedAtAdapter = InstantAdapter,
            karmaAdapter = IntAdapter,
        ),
    )
}
