package dev.thomasharris.lemon.feature.comments

import android.content.res.Resources
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import dev.thomasharris.lemon.core.betterhtml.HtmlText
import dev.thomasharris.lemon.core.model.LobstersComment
import dev.thomasharris.lemon.core.ui.Avatar
import dev.thomasharris.lemon.core.ui.format
import dev.thomasharris.lemon.core.ui.isNewUser
import dev.thomasharris.lemon.core.ui.postedAgo

// Big ol TODO
val CommentDepthColors = listOf(
    Color.Red,
    Color.Green,
    Color.Blue,
    Color.Yellow,
    Color.Cyan,
)

@Composable
fun CommentsItem(
    item: LobstersComment,
    storyAuthor: String,
    onLinkClicked: (String?) -> Unit,
    modifier: Modifier = Modifier,
    onItemClicked: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .alpha(if (item.score < -2) .7f else 1f)
            // partial workaround for known issues with
            // animating content when using IntrinsicSize.Min
            .animateContentSize()
            .height(IntrinsicSize.Min)
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onItemClicked()
            }
            .padding(
                start = item.indentLevel
                    .minus(1)
                    .times(16).dp,
                top = 4.dp,
                end = 2.dp,
                bottom = 8.dp,
            ),
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .width(4.dp)
                .fillMaxHeight()
                .background(
                    color = item.indentLevel
                        .minus(1)
                        .mod(CommentDepthColors.size)
                        .let(CommentDepthColors::get),
                    shape = RoundedCornerShape(8.dp),
                ),
        )

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Avatar(
                    fullAvatarUrl = item.commentingUser.fullAvatarUrl,
                )
                Text(
                    text = item.infoLine(storyAuthor, LocalContext.current.resources),
                )

                Spacer(modifier = Modifier.weight(1f))

                AnimatedVisibility(
                    visible = item.isCompact() && item.childCount > 0,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Text(
                        text = item.childCount.toString(10),
                    )
                }

                val rotation by animateFloatAsState(targetValue = if (item.isCompact()) 180f else 0f)

                // TODO try to animate and rotate it?
                Icon(
                    modifier = Modifier.rotate(rotation),
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null, // TODO
                )
            }

            AnimatedVisibility(
                visible = !item.isCompact(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                HtmlText(
                    text = item.comment,
                    modifier = modifier,
                    onLinkClicked = onLinkClicked,
                )
            }
        }
    }
}

fun LobstersComment.isCompact() = visibility == LobstersComment.Visibility.COMPACT

fun LobstersComment.infoLine(
    storyAuthor: String,
    resources: Resources,
): AnnotatedString {
    return buildAnnotatedString {
        val color = when {
            commentingUser.username == storyAuthor -> Color.Blue
            commentingUser.isNewUser() -> Color.Green
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
