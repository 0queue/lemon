package dev.thomasharris.lemon.core.betterhtml.fromclaw

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

const val LEADING_MARGIN = 32 + 8

/**
 * good test area: https://lobste.rs/s/xvqqvy/dynamic_linking.json
 *
 * also good: https://lobste.rs/s/m24zv1/xi_editor_retrospective
 * (andyc going ham with the formatting)
 */
fun String.fromHtml(inline: Boolean = false, dipToPx: (Float) -> Float = { it }): CharSequence {
    val parsed = Jsoup.parse(this.replace(Regex("\\\\n"), "\n"))

    val body = parsed.body()

    return body.children().map {
        val res = it.render(dipToPx)

        // generally, elements should care for themselves whether they are paragraphs
        // or not, this is important when, for example, nesting lists.  But the top
        // level should always be made of paragraphs, so if a top level <strong/>
        // for example sneaks through, make it a paragraph
        if (!inline && !res.startsWith("\n") && !res.endsWith("\n"))
            res.paragraph()
        else
            res
    }.concat()
}

private fun Element.render(dipToPx: (Float) -> Float, indentation: Int = 0): CharSequence {
    return when (tagName()) {
        "p" -> {
            textuals(::identity) { it.render(dipToPx, indentation) }.concat().trim().paragraph()
        }
        "a" -> {
            val url = this.attr("abs:href") // may be empty

            text().span(
                PressableSpan(
                    url,
                ),
            )
        }
        "blockquote" -> {
            textuals(::identity) { it.render(dipToPx, indentation + 1) }.concat().trim().span {
                span(
                    MyQuoteSpan(
                        stripeWidth = dipToPx(2f).toInt(),
                        indentation = indentation,
                    ),
                )
                span(StyleSpan(Typeface.ITALIC))
            }.paragraph()
        }
        "pre" -> {
            textuals(::identity) { it.render(dipToPx, indentation) }.concat().span {
                span(
                    MyQuoteSpan(
                        stripeWidth = dipToPx(2f).toInt(),
                        indentation = indentation,
                        color = Color.TRANSPARENT,
                    ),
                )
                span(TypefaceSpan("monospace"))
            }.paragraph()
        }
        "del" -> {
            text().span(StrikethroughSpan())
        }
        "em" -> {
            text().span(StyleSpan(Typeface.ITALIC))
        }
        "code" -> {
            text().span(TypefaceSpan("monospace"))
        }
        "strong" -> {
            text().span(StyleSpan(Typeface.BOLD))
        }
        "ul" -> {
            children().map {
                it.render(dipToPx, indentation).span(
                    MyBulletSpan(
                        indentation,
                    ),
                )
            }.concat().trim().paragraph()
        }
        "ol" -> {
            val startAt = attr("start").ifEmpty { "1" }.toInt()
            val max = children().size
            children().map { el ->
                el.render(dipToPx, indentation)
                    .span(
                        MyNumberedBulletSpan(
                            indentation,
                            el.elementSiblingIndex() + startAt,
                            max,
                        ),
                    )
            }.concat().trim().paragraph()
        }
        "li" -> {
            textuals(::identity) {
                it.render(dipToPx, indentation + 1)
            }.concat().trim() + "\n"
        }
        "hr" -> {
            "-".span(HrSpan(dipToPx(2f).toInt())).paragraph()
        }
        else -> {
            text().span(ForegroundColorSpan(Color.CYAN))
        }
    }
}

private fun List<CharSequence>.concat(): CharSequence = TextUtils.concat(*this.toTypedArray())
private operator fun CharSequence.plus(that: CharSequence): CharSequence =
    TextUtils.concat(this, that)

private fun Node.textuals(
    textBlock: (String) -> CharSequence,
    elementBlock: (Element) -> CharSequence,
): List<CharSequence> = childNodes().mapNotNull {
    when (it) {
        is Element -> elementBlock(it)
        is TextNode -> textBlock(it.text())
        else -> null
    }
}

private fun CharSequence.span(block: SpannableString.() -> Unit) =
    SpannableString(this).apply(block)

private fun CharSequence.span(span: Any): SpannableString =
    SpannableString(this).apply { span(span) }

private fun SpannableString.span(span: Any) =
    setSpan(span, 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

private fun CharSequence.paragraph(): CharSequence = listOf("\n", this, "\n").concat()

private fun identity(s: String): CharSequence = s
