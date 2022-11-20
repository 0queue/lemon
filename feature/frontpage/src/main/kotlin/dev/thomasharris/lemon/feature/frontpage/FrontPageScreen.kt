package dev.thomasharris.lemon.feature.frontpage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import dev.thomasharris.lemon.core.model.LobstersStory
import dev.thomasharris.lemon.core.ui.Story
import dev.thomasharris.lemon.core.ui.requireNotPlaceholder

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
            items(
                items = pages,
                key = LobstersStory::shortId,
            ) { story ->
                requireNotPlaceholder(story)

                Story(
                    story = story,
                    onClick = onClick,
                )
            }
        }
    }
}
