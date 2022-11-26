package dev.thomasharris.lemon.core.ui

import android.content.res.Resources
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.thomasharris.lemon.core.model.LobstersStory
import dev.thomasharris.lemon.core.model.LobstersUser
import dev.thomasharris.lemon.core.theme.LemonForLobstersTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Composable
fun Story(
    story: LobstersStory,
    onClick: ((String) -> Unit)?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                enabled = onClick != null,
                onClick = {
                    onClick?.invoke(story.shortId)
                },
            )
            .padding(8.dp),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = story.title,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            AsyncImage(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 4.dp)
                    .size(16.dp)
                    .clip(CircleShape),
                model = story.submitter.fullAvatarUrl,
                contentDescription = "",
            )
            Text(
                modifier = Modifier.padding(2.dp),
                text = story.details(LocalContext.current.resources),
            )
        }
    }
}

fun LobstersStory.details(resources: Resources): AnnotatedString {
    return buildAnnotatedString {
        append("%+d".format(score))
        append(" | ")
        append("by ")

        if (submitter.isNewUser())
            withStyle(style = SpanStyle(Color.Green)) {
                append(submitter.username)
            }
        else
            append(submitter.username)

        append(" ${createdAt.postedAgo().format(resources)} ")
        append(resources.getQuantityString(R.plurals.numberOfComments, commentCount, commentCount))
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
        ),
    )
}
