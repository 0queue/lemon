package dev.thomasharris.lemon.core.database

import app.cash.sqldelight.ColumnAdapter
import kotlinx.datetime.Instant
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String

public data class Comment(
    public val shortId: String,
    public val storyId: String,
    public val commentIndex: Int,
    public val shortIdUrl: String,
    public val createdAt: Instant,
    public val updatedAt: Instant,
    public val isDeleted: Boolean,
    public val isModerated: Boolean,
    public val score: Int,
    public val comment: String,
    public val indentLevel: Int,
    public val username: String,
    public val insertedAt: Instant,
) {
    public class Adapter(
        public val commentIndexAdapter: ColumnAdapter<Int, Long>,
        public val createdAtAdapter: ColumnAdapter<Instant, Long>,
        public val updatedAtAdapter: ColumnAdapter<Instant, Long>,
        public val scoreAdapter: ColumnAdapter<Int, Long>,
        public val indentLevelAdapter: ColumnAdapter<Int, Long>,
        public val insertedAtAdapter: ColumnAdapter<Instant, Long>,
    )
}
