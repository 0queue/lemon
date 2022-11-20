package dev.thomasharris.lemon.feature.comments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import dev.thomasharris.lemon.core.model.LobstersComment
import dev.thomasharris.lemon.core.model.LobstersStory

@Composable
fun CommentsRoute(
    viewModel: CommentsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
) {
    val story by viewModel.story.collectAsState()
    val pages = viewModel.pager.collectAsLazyPagingItems()

    CommentsScreen(
        story = story,
        pages = pages,
        onBackClick = onBackClick,
    )
}

@Composable
fun CommentsScreen(
    story: LobstersStory?,
    pages: LazyPagingItems<LobstersComment>,
    onBackClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("story ${story?.shortId}: ${story?.title ?: "LOADING"}")
        Text(
            modifier = Modifier.padding(4.dp),
            text = "# of comments ${story?.commentCount}",
        )
        Button(
            modifier = Modifier.padding(4.dp),
            onClick = onBackClick,
        ) {
            Text("go back")
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(
                items = pages,
                key = { _, comment -> comment.shortId },
            ) { index, item ->
                if (item == null)
                    Text("ITEM LOADING I GUESS")
                else {
                    Text(
                        modifier = Modifier.padding(
                            start = item.indentLevel.times(16).dp,
                            top = 4.dp,
                            end = 2.dp,
                            bottom = 16.dp,
                        ),
                        text = "$index: ${item.comment}",
                    )
                }
            }
        }
    }
}
