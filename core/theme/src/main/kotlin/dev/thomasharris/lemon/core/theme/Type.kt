package dev.thomasharris.lemon.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// using:
// - titleMedium
// - titleLarge
// - bodySmall
// - labelLarge


// for archivo at least
object FontWidth {
    val ExtraCondensed = FontVariation.width(62.5f)
    val Condensed = FontVariation.width(75f)
    val SemiCondensed = FontVariation.width(87.5f)
    val Normal = FontVariation.width(100f)
    val SemiExpanded = FontVariation.width(112.5f)
    val Expanded = FontVariation.width(125f)
}


@OptIn(ExperimentalTextApi::class)
val archivo = FontFamily(
    Font(
        resId = R.font.archivo_variable,
        weight = FontWeight.Normal,
        style = FontStyle.Normal,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Normal,
            style = FontStyle.Normal,
            FontWidth.Normal,
        ),
    ),
    Font(
        resId = R.font.archivo_variable,
        weight = FontWeight.Medium,
        style = FontStyle.Normal,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Medium,
            style = FontStyle.Normal,
            FontWidth.Normal,
        ),
    ),
    Font(
        resId = R.font.archivo_italic_variable,
        weight = FontWeight.Normal,
        style = FontStyle.Italic,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Normal,
            style = FontStyle.Italic,
            FontWidth.Normal,
        ),
    ),
)

val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = archivo,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.2.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = archivo,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp, // from 16
        lineHeight = 28.sp,
        letterSpacing = 0.2.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = archivo,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp,
    ),
)
