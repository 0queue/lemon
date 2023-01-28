package dev.thomasharris.lemon.feature.comments

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import dev.thomasharris.lemon.core.model.LobstersComment
import dev.thomasharris.lemon.core.model.LobstersStory
import dev.thomasharris.lemon.core.ui.Story

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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CommentsScreen(
    story: LobstersStory?,
    pages: LazyPagingItems<LobstersComment>,
    onBackClick: () -> Unit,
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)

    val isRefreshing = pages.loadState.refresh is LoadState.Loading
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = pages::refresh,
    )

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null, // TODO
                        )
                    }
                },
                title = {
                    Text(text = "Comments")
                },
                scrollBehavior = scrollBehavior,
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .pullRefresh(pullRefreshState)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            ) {
                LazyColumn(
                    contentPadding = innerPadding,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    if (story != null) item {
                        Story(
                            story = story,
                            onClick = null,
                        )
                    }

                    if (pages.itemCount == 0)
                        item {
                            Text("EMPTY")
                        }
                    else
                        itemsIndexed(
                            items = pages,
                            key = { _, comment -> comment.shortId },
                        ) { index, item ->
                            if (item == null)
                                Text("ITEM LOADING I GUESS")
                            else {
                                CommentsItem(
                                    item = item,
                                    storyAuthor = story?.submitter?.username ?: "",
                                )
                            }
                        }
                }

                PullRefreshIndicator(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        // this padding is also necessary
                        .padding(top = innerPadding.calculateTopPadding()),
                    refreshing = isRefreshing,
                    state = pullRefreshState,
                )
            }
        },
    )
}
