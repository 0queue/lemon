package dev.thomasharris.lemon.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import materialcolorutilities.blend.Blend

/**
 * These are colors generally used on a surface, not as a container or anything
 */
@Immutable
data class CustomColors(
    val newUser: Color,
    val author: Color,
    val indentColors: List<Color>,
)

val LocalCustomColors = staticCompositionLocalOf {
    CustomColors(
        newUser = Color.Unspecified,
        author = Color.Unspecified,
        indentColors = listOf(Color.Unspecified),
    )
}

fun dynamicCustomColors(
    seed: CustomColors,
    source: Color,
) = CustomColors(
    newUser = seed.newUser harmonize source,
    author = seed.author harmonize source,
    indentColors = seed.indentColors.map { it harmonize source },
)

private infix fun Color.harmonize(sourceColor: Color): Color {
    return Blend.harmonize(this.toArgb(), sourceColor.toArgb()).let(::Color)
}
