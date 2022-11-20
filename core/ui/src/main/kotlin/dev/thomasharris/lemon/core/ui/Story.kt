package dev.thomasharris.lemon.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.thomasharris.lemon.model.LobstersStory

@Composable
fun Story(
    story: LobstersStory,
    onClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(story.shortId)
            }
            .padding(16.dp),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = story.title,
        )
        Text(
            modifier = Modifier.padding(2.dp),
            text = "${story.commentCount} comments",
        )
    }
}
