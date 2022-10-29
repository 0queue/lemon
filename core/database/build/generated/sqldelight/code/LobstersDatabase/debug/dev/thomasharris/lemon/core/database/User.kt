package dev.thomasharris.lemon.core.database

import app.cash.sqldelight.ColumnAdapter
import kotlinx.datetime.Instant
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String

public data class User(
    public val username: String,
    public val createdAt: Instant,
    public val isAdmin: Boolean,
    public val about: String,
    public val isModerator: Boolean,
    public val karma: Int,
    public val avatarShortUrl: String,
    public val invitedByUser: String?,
    public val insertedAt: Instant,
    public val githubUsername: String?,
    public val twitterUsername: String?,
) {
    public class Adapter(
        public val createdAtAdapter: ColumnAdapter<Instant, Long>,
        public val karmaAdapter: ColumnAdapter<Int, Long>,
        public val insertedAtAdapter: ColumnAdapter<Instant, Long>,
    )
}
