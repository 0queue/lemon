package dev.thomasharris.lemon.core.database

import app.cash.sqldelight.ColumnAdapter
import kotlinx.datetime.Instant
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List

public data class Story(
    public val shortId: String,
    public val title: String,
    public val createdAt: Instant,
    public val url: String,
    public val score: Int,
    public val commentCount: Int,
    public val description: String,
    public val username: String,
    public val tags: List<String>,
    public val pageIndex: Int,
    public val pageSubIndex: Int,
    public val insertedAt: Instant,
) {
    public class Adapter(
        public val createdAtAdapter: ColumnAdapter<Instant, Long>,
        public val scoreAdapter: ColumnAdapter<Int, Long>,
        public val commentCountAdapter: ColumnAdapter<Int, Long>,
        public val tagsAdapter: ColumnAdapter<List<String>, String>,
        public val pageIndexAdapter: ColumnAdapter<Int, Long>,
        public val pageSubIndexAdapter: ColumnAdapter<Int, Long>,
        public val insertedAtAdapter: ColumnAdapter<Instant, Long>,
    )
}
