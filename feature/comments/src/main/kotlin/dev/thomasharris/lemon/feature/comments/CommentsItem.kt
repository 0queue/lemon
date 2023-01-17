package dev.thomasharris.lemon.feature.comments

import android.content.res.Resources
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .padding(
                start = item.indentLevel
                    .minus(1)
                    .times(16).dp,
                top = 4.dp,
                end = 2.dp,
                bottom = 16.dp,
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
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null, // TODO
                )
            }
            HtmlText(
                modifier = modifier,
                text = item.comment,
            )
        }
    }
}

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

//        val t = Date(min(comment.createdAt.time, comment.updatedAt.time)).postedAgo()
//        val action = if (comment.createdAt != comment.updatedAt) "edited " else ""
//        val scoreText = comment.score.let { s ->
//            when {
//                s < -2 -> " | $s"
//                s > 4 -> " | +$s"
//                else -> ""
//            }
//        }
//
//        commentAuthor.text =
//            SpannableString("${comment.username} $action${t.toString(root.context)}$scoreText").apply {
//                // CAREFUL slightly hardcoded here
//                when {
//                    comment.username == comment.storyAuthor -> R.color.comment_original_poster
//                    comment.userCreatedAt?.isNewUser() == true -> R.color.new_author
//                    else -> null
//                }?.let { c ->
//                    setSpan(
//                        ForegroundColorSpan(ContextCompat.getColor(root.context, c)),
//                        0,
//                        comment.username.length,
//                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE
//                    )
//                }
//            }
//
// username (normal/author/new user) action (none/edited) $ago score (+/-/none)
