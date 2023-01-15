package dev.thomasharris.lemon.core.betterhtml

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer

@OptIn(ExperimentalTextApi::class)
@Composable
fun OldHtmlText(
    text: String,
    style: TextStyle = LocalTextStyle.current,
) {
    var onDraw: DrawScope.() -> Unit by remember { mutableStateOf({}) }
    val textMeasurer = rememberTextMeasurer()

    val rendered = remember(text) {
        text // .parseHtml()
    }

    Text(
        text = rendered,
    )
}
