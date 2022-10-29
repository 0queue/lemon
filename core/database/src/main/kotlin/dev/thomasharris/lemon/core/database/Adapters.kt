package dev.thomasharris.lemon.core.database

import app.cash.sqldelight.ColumnAdapter
import kotlinx.datetime.Instant

object InstantAdapter : ColumnAdapter<Instant, Long> {
    override fun decode(databaseValue: Long) =
        Instant.fromEpochMilliseconds(databaseValue)

    override fun encode(value: Instant): Long =
        value.toEpochMilliseconds()
}

object IntAdapter : ColumnAdapter<Int, Long> {
    override fun decode(databaseValue: Long): Int =
        databaseValue.toInt()

    override fun encode(value: Int): Long =
        value.toLong()
}

object ListStringAdapter : ColumnAdapter<List<String>, String> {
    override fun decode(databaseValue: String): List<String> =
        databaseValue.split(",")

    override fun encode(value: List<String>): String =
        value.joinToString(separator = ",")
}
