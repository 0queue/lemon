package dev.thomasharris.lemon.core.betterhtml

import android.graphics.Typeface
import android.util.TypedValue
import android.widget.TextView
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.widget.TextViewCompat
import dev.thomasharris.lemon.core.betterhtml.fromclaw.PressableLinkMovementMethod
import dev.thomasharris.lemon.core.betterhtml.fromclaw.fromHtml

// private fun render(text: String): AnnotatedString {
//
// }

@OptIn(ExperimentalTextApi::class)
@Composable
fun HtmlText(
    text: String,
    modifier: Modifier = Modifier,
    onLinkClicked: (String?) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
    resolver: FontFamily.Resolver = LocalFontFamilyResolver.current,
) {
    val dipToPx = with(LocalDensity.current) {
        { dip: Float ->
            dip.dp.toPx()
        }
    }

    val parsed = remember(text) {
        text.fromHtml(false, dipToPx).trim()
    }

    // https://stackoverflow.com/questions/70800896/how-to-convert-textstyle-from-jetpack-compose-to-android-graphics-typeface
    val typeface = remember(resolver, style) {
        resolver.resolve(
            fontFamily = style.fontFamily,
            fontWeight = style.fontWeight ?: FontWeight.Normal,
            fontStyle = style.fontStyle ?: FontStyle.Normal,
            fontSynthesis = style.fontSynthesis ?: FontSynthesis.All,
        ).value as Typeface
    }

    val textSize = remember(style.fontSize) {
        if (style.fontSize.isSp)
            style.fontSize.value
        else
            error("fontSize is not given in sp")
    }

    val letterSpacing = if (style.letterSpacing.isSp)
        style.letterSpacing.div(textSize).value
    else
        error("letterSpacing is not given in sp")

    // inject another textColor to this function, set to Color.Unspecified
    val textColor = style.color.takeOrElse {
        LocalContentColor.current
    }

    val lineHeight = with(LocalDensity.current) {
        style.lineHeight.roundToPx()
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(
                context,
            ).apply {
                movementMethod = PressableLinkMovementMethod(onLinkClicked)

                setTypeface(typeface)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
                setTextColor(textColor.toArgb())
                setLetterSpacing(letterSpacing)
                TextViewCompat.setLineHeight(this, lineHeight)
                setText(parsed)
            }
        },
    )
}
