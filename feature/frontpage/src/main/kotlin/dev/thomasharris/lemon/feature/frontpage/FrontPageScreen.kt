package dev.thomasharris.lemon.feature.frontpage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import dev.thomasharris.lemon.model.LobstersStory

@Composable
fun FrontPageRoute(
    viewModel: FrontPageViewModel = hiltViewModel(),
    onClick: (String) -> Unit,
) {
    val pages = viewModel.pages.collectAsLazyPagingItems()

    FrontPageScreen(
        onClick = onClick,
        pages = pages,
    )
}

@Composable
fun FrontPageScreen(
    onClick: (String) -> Unit,
    pages: LazyPagingItems<LobstersStory>,
) {
    Column {
        Text("welcome to the frontpage")

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(
                items = pages,
                key = { _, s -> s.shortId },
            ) { index, story ->
                if (story == null)
                    Text("Null...")
                else {
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
                            text = "$index: ${story.title}",
                        )
                        Text(
                            modifier = Modifier.padding(2.dp),
                            text = "${story.commentCount} comments",
                        )
                    }
                }
            }
        }
    }
}
