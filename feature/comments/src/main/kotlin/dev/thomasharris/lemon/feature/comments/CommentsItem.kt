package dev.thomasharris.lemon.feature.comments

import android.content.res.Resources
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.thomasharris.lemon.core.betterhtml.HtmlText
import dev.thomasharris.lemon.core.model.LobstersComment
import dev.thomasharris.lemon.core.model.LobstersUser
import dev.thomasharris.lemon.core.theme.CustomColors
import dev.thomasharris.lemon.core.theme.LemonForLobstersTheme
import dev.thomasharris.lemon.core.theme.LocalCustomColors
import dev.thomasharris.lemon.core.ui.Avatar
import dev.thomasharris.lemon.core.ui.format
import dev.thomasharris.lemon.core.ui.isNewUser
import dev.thomasharris.lemon.core.ui.postedAgo
import kotlinx.datetime.Instant

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommentsItem(
    item: LobstersComment,
    storyAuthor: String,
    onLinkClicked: (String?) -> Unit,
    modifier: Modifier = Modifier,
    onItemClicked: () -> Unit = {},
    onItemLongClicked: () -> Unit = {},
    onDropDownClicked: () -> Unit = {},
) {
    val customColors = LocalCustomColors.current

    // TODO remove this row
    Row(
        modifier = modifier.then(
            Modifier
                .alpha(if (item.score < -2) .7f else 1f)
                .padding(horizontal = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .combinedClickable(
                    onClick = onItemClicked,
                    onLongClick = onItemLongClicked,
                )
                .padding(
                    start = item.indentLevel
                        .times(8).dp,
                    top = 4.dp,
                    bottom = 4.dp,
                )
                .drawBehind {
                    drawRoundRect(
                        color = item
                            .indentLevel
                            .mod(customColors.indentColors.size)
                            .let(customColors.indentColors::get),
                        topLeft = Offset(4.dp.toPx(), 0f),
                        size = Size(4.dp.toPx(), size.height),
                        cornerRadius = CornerRadius(8.dp.toPx()),
                    )
                },
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
            // TODO idk why but when scrolling this makes things weirder, probably something to do with AnimatedVisibility?
//                .animateContentSize(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Avatar(
                    fullAvatarUrl = item.commentingUser.fullAvatarUrl,
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = item.infoLine(
                        storyAuthor = storyAuthor,
                        resources = LocalContext.current.resources,
                        customColors = LocalCustomColors.current,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                )

                Spacer(modifier = Modifier.weight(1f))

                AnimatedVisibility(
                    visible = item.isCompact() && item.childCount > 0,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Text(
                        text = item.childCount.toString(10),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                val rotation by animateFloatAsState(targetValue = if (item.isCompact()) 0f else 180f)

                Icon(
                    modifier = Modifier
                        .rotate(rotation)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            // not sold on this, since it blocks clicks essentially
                            enabled = item.visibility == LobstersComment.Visibility.VISIBLE,
                        ) {
                            onDropDownClicked()
                        },
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null, // TODO
                )
            }

            if (!item.isCompact())
                HtmlText(
                    text = item.comment,
                    modifier = Modifier.padding(top = 4.dp),
                    onLinkClicked = onLinkClicked,
                )
        }
    }
}

fun LobstersComment.isCompact() = visibility == LobstersComment.Visibility.COMPACT

fun LobstersComment.infoLine(
    storyAuthor: String,
    resources: Resources,
    customColors: CustomColors,
): AnnotatedString {
    return buildAnnotatedString {
        val color = when {
            commentingUser.username == storyAuthor -> customColors.author
            commentingUser.isNewUser() -> customColors.newUser
            else -> Color.Unspecified
        }

        withStyle(SpanStyle(color = color)) {
            append(commentingUser.username)
            append(" ")
        }

        if (createdAt != updatedAt) {
            append("edited")
            append(" ")
        }

        minOf(createdAt, updatedAt)
            .postedAgo()
            .format(resources)
            .let(this::append)

        val scoreText = when {
            score < -2 -> score.toString()
            score > 4 -> "+$score"
            else -> null
        }

        if (scoreText != null) {
            append(" | ")
            append(scoreText)
        }
    }
}

@Composable
@Preview
fun CommentPreview() {
    val instant = Instant.parse("2022-11-20T12:26:06.406253Z")

    val comment = LobstersComment(
        shortId = "asdf",
        storyId = "whatever",
        commentIndex = 1,
        createdAt = instant,
        updatedAt = instant,
        isDeleted = false,
        isModerated = false,
        score = 12,
        comment = """<p>Here is a comment that is very high effort and spans multiple lines on my pixel</p>""",
        indentLevel = 0,
        commentingUser = LobstersUser(
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
        visibility = LobstersComment.Visibility.VISIBLE,
        childCount = 3,
    )

    LemonForLobstersTheme {
        Surface {
            CommentsItem(
                item = comment,
                storyAuthor = "0queue",
                onLinkClicked = {},
            )
        }
    }
}
