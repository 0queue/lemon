package dev.thomasharris.lemon.core.betterhtml

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

/**
 * Annotate a string based on the html it contains
 *
 * Based on the better html library of claw for lobsters
 */
fun String.parseHtml(
    inline: Boolean = false,
): AnnotatedString {
    val parsed = Jsoup.parse(this.replace("\\\\n", "\\n"))

    val body = parsed.body()

    AnnotatedString(
        "asdf",
        spanStyles = listOf(),
        paragraphStyles = listOf(),
    )

    return body
        .children()
        .map(Element::render2)
        .concat(AnnotatedString("\n"))

//    return body
//        .children()
//        .map(Element::render)
//        .concat(AnnotatedString("\n"))
}

private fun Element.render2() = buildAnnotatedString {
    val i = pushStyle(ParagraphStyle(textIndent = 0.textIndent()))
    render(
        element = this@render2,
        lastParagraphStyleIndex = i,
        indentLevel = 0,
    )
}

private fun AnnotatedString.Builder.render(
    element: Element,
    lastParagraphStyleIndex: Int,
    indentLevel: Int = 0,
) {
    println("RENDER ${element.tagName()} $lastParagraphStyleIndex")
    when (element.tagName()) {
        "p" -> {
            withStyle(SpanStyle(color = Color.Blue)) {
                forEachTextual(element) {
                    render(
                        element = it,
                        lastParagraphStyleIndex = lastParagraphStyleIndex,
                    )
                }
            }
        }
        "blockquote" -> {
            val nextIndentLevel = indentLevel.plus(1)

            pop(lastParagraphStyleIndex)
            val lpsi = pushStyle(ParagraphStyle(textIndent = nextIndentLevel.textIndent()))

            withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                forEachTextual(element) {
                    render(
                        element = it,
                        lastParagraphStyleIndex = lpsi,
                        indentLevel = nextIndentLevel,
                    )
                }
            }
        }
        else -> append(element.text())
    }
}

private fun Element.render(
    indentLevel: Int = 0,
): AnnotatedString {
    return when (tagName()) {
        "p" -> {
            buildAnnotatedString {
                withStyle(SpanStyle(color = Color.Blue)) {
                    textuals { it.render() }
//                        .reduce { acc, e -> acc.plus(AnnotatedString("\n")).plus(e) }
//                        .let(this::append)
                        .concat()
                        .let(this::append)
                }
            }
        }
        "blockquote" -> {
            buildAnnotatedString {
                withStyle(ParagraphStyle(textIndent = indentLevel.plus(1).textIndent())) {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        textuals { it.render(indentLevel.plus(1)) }
                            .concat()
                            .let(this::append)
                    }
                }
            }
        }
        "ul" -> {
            buildAnnotatedString {
                val nextIndentLevel = indentLevel.plus(1)
                withStyle(ParagraphStyle(textIndent = nextIndentLevel.textIndent())) {
                    children()
                        .map { it.render(nextIndentLevel) }
                        .concat(AnnotatedString("\n"))
                        .let(this::append)
                }
            }
        }
        "li" -> {
            buildAnnotatedString {
                withStyle(SpanStyle(color = Color.Green)) {
                    textuals { it.render() }
                        .concat()
                        .let(this::append)
                }
            }
        }
        "code" -> {
            buildAnnotatedString {
                withStyle(SpanStyle(fontFamily = FontFamily.Monospace)) {
                    text().let(this::append)
                }
            }
        }
        else -> {
            text().let(::AnnotatedString)
        }
    }
}

private fun Node.textuals(
    textBlock: (String) -> AnnotatedString = { AnnotatedString(it) },
    elementBlock: (Element) -> AnnotatedString,
): List<AnnotatedString> = childNodes().mapNotNull {
    when (it) {
        is Element -> elementBlock(it)
        is TextNode -> textBlock(it.text())
        else -> null
    }
}

private fun AnnotatedString.Builder.forEachTextual(
    element: Element,
    textBlock: AnnotatedString.Builder.(String) -> Unit = { append(it) },
    elementBlock: AnnotatedString.Builder.(Element) -> Unit,
) {
    element.childNodes().forEach {
        println("TEXTUAL CHECK ${it.javaClass.name}")
        when (it) {
            is TextNode -> textBlock(it.text().also { t -> println("TEXTNODE IS |$t|") })
            is Element -> elementBlock(it)
        }
    }
}

// private fun List<AnnotatedString>.concat(): AnnotatedString =
//    fold(AnnotatedString(""), AnnotatedString::plus)

private fun List<AnnotatedString>.concat(
    annotatedString: AnnotatedString = AnnotatedString(""),
) = reduce { acc, e -> acc.plus(annotatedString).plus(e) }

private fun AnnotatedString.paragraph() = buildAnnotatedString {
    append("\n")
    append(this@paragraph)
    append("\n")
}

private fun Int.textIndent() = 16
    .times(this)
    .sp
    .let { TextIndent(it, it) }

private inline val <T> T.exhaustive
    get() = this
