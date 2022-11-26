package dev.thomasharris.lemon.core.ui

import android.content.res.Resources
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.DurationUnit

fun Instant.postedAgo(
    now: Instant = Clock.System.now(),
): Pair<Duration, DurationUnit> {
    val ago = now
        .minus(this)
        .absoluteValue

    val unit = listOf(
        DurationUnit.DAYS,
        DurationUnit.HOURS,
        DurationUnit.MINUTES,
    ).firstOrNull { unit ->
        ago.toLong(unit) > 0
    } ?: DurationUnit.MINUTES

    return ago to unit
}

fun Pair<Duration, DurationUnit>.format(resources: Resources): String {
    val id = when (second) {
        DurationUnit.DAYS -> R.plurals.numberOfDays
        DurationUnit.HOURS -> R.plurals.numberOfHours
        DurationUnit.MINUTES -> R.plurals.numberOfMinutes
        else -> R.plurals.numberOfMinutes
    }

    val n = first.toInt(second)

    return resources.getQuantityString(id, n, n)
}
