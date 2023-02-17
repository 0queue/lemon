package dev.thomasharris.lemon.core.datastore

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val themeBrightness: ThemeBrightness,
    val themeDynamic: Boolean,
)

enum class ThemeBrightness {
    SYSTEM,
    DAY,
    NIGHT,
}

val DefaultSettings = Settings(
    themeBrightness = ThemeBrightness.SYSTEM,
    themeDynamic = true,
)
