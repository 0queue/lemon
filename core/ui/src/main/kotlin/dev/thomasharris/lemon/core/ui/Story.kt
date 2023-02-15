package dev.thomasharris.lemon.core.ui

import android.content.res.Resources
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withAnnotation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.thomasharris.lemon.core.betterhtml.HtmlText
import dev.thomasharris.lemon.core.betterhtml.getBoundingBoxes
import dev.thomasharris.lemon.core.model.LobstersStory
import dev.thomasharris.lemon.core.model.LobstersUser
import dev.thomasharris.lemon.core.theme.LemonForLobstersTheme
import dev.thomasharris.lemon.core.theme.harmonize
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.net.URI

@OptIn(ExperimentalTextApi::class, ExperimentalFoundationApi::class)
@Composable
fun Story(
    story: LobstersStory,
    onClick: ((String) -> Unit)?,
    onLongClick: ((String) -> Unit)?,
    modifier: Modifier = Modifier,
    isCompact: Boolean = true,
    onLinkClicked: (String?) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .combinedClickable(
                enabled = onClick != null,
                onClick = {
                    onClick?.invoke(story.shortId)
                },
                onLongClick = {
                    onLongClick?.invoke(story.submitter.username)
                },
            )
            .padding(8.dp),
    ) {
        val baseTitleSize = 18.sp
        val tagSizeScale = .85f

        val title = buildAnnotatedString {
            append(story.title)

            story.tags.forEach { tag ->
                // No great analogue to the old TagSpan, which calculated the space
                // that the normal size text took up and drew the text smaller, so
                // fake some extra space... with a space
                append("  ")

                val style = SpanStyle(
                    fontSize = baseTitleSize.times(tagSizeScale),
                    color = Color.Black,
                )

                // bit annoying that tags are represented with a different kind of tag but oh well
                withAnnotation(tag.toTagKind(), "") {
                    withStyle(style) {
                        append(tag)
                    }
                }
            }

            if (story.tags.isNotEmpty())
                append(" ")

            if (story.description.isNotBlank())
                append(" ☶")
        }

        var onDraw: DrawScope.() -> Unit by remember { mutableStateOf({}) }

        Text(
            modifier = Modifier
                .drawBehind { onDraw() }
                .fillMaxWidth(),
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontSize = baseTitleSize, // but not too large
            onTextLayout = { layoutResult ->
                // hmmm
                val colorfulTextBounds = listOf(
                    "kind:showaskannounceinterview",
                    "kind:media",
                    "kind:meta",
                    "kind:default",
                )
                    .map { tag ->
                        title.getStringAnnotations(tag, 0, title.length)
                            .map { tag.toTagColors() to it }
                    }
                    .flatten()
                    .map { (colors, annotation) ->
                        layoutResult.getBoundingBoxes(annotation.start, annotation.end)
                            .map { colors to it }
                    }
                    .flatten()

                onDraw = {
                    colorfulTextBounds.forEach { (colors, bound) ->

                        val padding = 12f

                        val newSize = Size(
                            width = bound.size.width.plus(padding),
                            height = bound.size.height.times(tagSizeScale),
                        )
                        val newTopLeft = Offset(
                            x = bound.topLeft.x.minus(padding.div(2f)),
                            y = bound.topLeft.y.plus(bound.size.height).minus(newSize.height),
                        )

                        drawRoundRect(
                            color = colors.fill,
                            topLeft = newTopLeft,
                            size = newSize,
                            cornerRadius = CornerRadius(8f),
                            style = Fill,
                        )

                        drawRoundRect(
                            color = colors.stroke,
                            topLeft = newTopLeft,
                            size = newSize,
                            cornerRadius = CornerRadius(8f),
                            style = Stroke(2f),
                        )
                    }
                }
            },
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
        ) {
            Avatar(
                modifier = Modifier.align(Alignment.CenterVertically),
                fullAvatarUrl = story.submitter.fullAvatarUrl,
            )
            val colorScheme = MaterialTheme.colorScheme
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = story.infoLine(LocalContext.current.resources, colorScheme::harmonize),
                style = MaterialTheme.typography.bodySmall,
                maxLines = if (isCompact) 1 else Int.MAX_VALUE,
                overflow = if (isCompact) TextOverflow.Ellipsis else TextOverflow.Clip,
            )
        }

        if (!isCompact && story.description.isNotBlank())
            HtmlText(
                modifier = Modifier.padding(top = 8.dp),
                text = story.description,
                onLinkClicked = onLinkClicked,
            )
    }
}

@Composable
fun Avatar(
    fullAvatarUrl: String,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        modifier = modifier.then(
            Modifier
                .size(16.dp)
                .clip(CircleShape),
        ),
        model = ImageRequest.Builder(LocalContext.current)
            .data(fullAvatarUrl)
            .crossfade(true)
            .build(),
        contentDescription = "",
    )
}

fun LobstersStory.infoLine(
    resources: Resources,
    harmonize: (Color) -> Color,
): AnnotatedString {
    return buildAnnotatedString {
        append("%+d".format(score))
        append(" | ")
        append("by ")

        if (submitter.isNewUser())
            withStyle(SpanStyle(color = Color.Green.let(harmonize))) {
                append(submitter.username)
            }
        else
            append(submitter.username)

        append(" ${createdAt.postedAgo().format(resources)} ")
        append(resources.getQuantityString(R.plurals.numberOfComments, commentCount, commentCount))

        shortUrl()?.let {
            append(" | ")
            withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                append(it)
            }
        }
    }
}

/**
 * From user.rb#NEW_USER_DAYS
 */
fun LobstersUser.isNewUser(
    asOf: Instant = Clock.System.now(),
) = createdAt
    .minus(asOf)
    .absoluteValue
    .inWholeDays <= 70

/**
 * Returns the hostname from the url without leading www subdomains
 * If empty, return null
 * If parsing the url goes bad, return "???"
 * (It has gone bad before... hence the trimming and try catching)
 */
fun LobstersStory.shortUrl(): String? {
    if (url.isBlank())
        return null

    return url.trim()
        .let {
            try {
                URI(it)
            } catch (t: Throwable) {
                null
            }
        }
        ?.host
        ?.removePrefix("www.")
        ?: "???"
}

fun String.toTagKind(): String {
    val showAskAnnounceInterview = setOf(
        "show",
        "ask",
        "announce",
        "interview",
    )

    val meta = setOf(
        "meta",
    )

    val media = setOf(
        "ask",
        "audio",
        "pdf",
        "show",
        "slides",
        "transcript",
        "video",
    )

    return when (this) {
        in showAskAnnounceInterview -> "kind:showaskannounceinterview"
        in meta -> "kind:meta"
        in media -> "kind:media"
        else -> "kind:default"
    }
}

data class TagColors(
    val stroke: Color,
    val fill: Color,
)

// TODO these should probably be themes somewhere else
fun String.toTagColors(): TagColors {
    return when (this) {
        "kind:showaskannounceinterview" -> TagColors(
            stroke = Color(0xFFF0B2B8),
            fill = Color(0xFFf9ddde),
        )
        "kind:meta" -> TagColors(
            stroke = Color(0xFFc8c8c8),
            fill = Color(0xFFeeeeee),
        )

        "kind:media" -> TagColors(
            stroke = Color(0xFFB2CCF0),
            fill = Color(0xFFddebf9),
        )

        "kind:default" -> TagColors(
            stroke = Color(0xFFd5d458),
            fill = Color(0xFFfffcd7),
        )

        else -> error("you should finally make some enums or whatever")
    }
}

@Preview(showBackground = true)
@Composable
fun StoryPreview(
    @PreviewParameter(LobstersStoryPreviewProvider::class)
    story: LobstersStory,
) {
    LemonForLobstersTheme {
        Story(
            story = story,
            onClick = {},
            onLongClick = {},
        )
    }
}

class LobstersStoryPreviewProvider : PreviewParameterProvider<LobstersStory> {
    private val instant = Instant.parse("2022-11-20T12:26:06.406253Z")

    override val values = sequenceOf(
        LobstersStory(
            shortId = "123",
            createdAt = instant,
            title = "Here is a story about how simple everything should be, and how I'd like to ignore complexities",
            url = "https://github.com/0queue/lemon",
            score = 10,
            commentCount = 17,
            description = "Here is a description",
            submitter = LobstersUser(
                username = "0queue",
                createdAt = instant,
                isAdmin = false,
                about = "I do things on Android and other Linux systems",
                isModerator = false,
                karma = 1_000_000,
                avatarUrl = "/avatars/jcs-200.png",
                invitedByUser = null,
                githubUsername = "0queue",
                twitterUsername = null,
            ),
            tags = listOf("stuff", "things"),
            pageIndex = 0,
            pageSubIndex = null,
        ),
    )
}
