package dev.thomasharris.lemon.feature.comments

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import dev.thomasharris.lemon.core.model.LobstersComment
import dev.thomasharris.lemon.core.model.LobstersStory
import dev.thomasharris.lemon.core.ui.Story
import dev.thomasharris.lemon.core.ui.SwipeToNavigate
import dev.thomasharris.lemon.core.ui.rememberSwipeToNavigateState
import dev.thomasharris.lemon.core.ui.requireNotPlaceholder

@Composable
fun CommentsRoute(
    viewModel: CommentsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onUrlClicked: (String?) -> Unit,
    onViewUserProfile: (String) -> Unit,
) {
    val story by viewModel.story.collectAsState()
    val pages = viewModel.pager.collectAsLazyPagingItems()

    val swipeToNavigateState = rememberSwipeToNavigateState {
        onBackClick()
    }

    SwipeToNavigate(
        state = swipeToNavigateState,
    ) {
        CommentsScreen(
            story = story,
            pages = pages,
            onBackClick = onBackClick,
            onUrlClicked = onUrlClicked,
            onItemClicked = viewModel::toggleComment,
            onItemLongClicked = onViewUserProfile,
            onItemDropDownClicked = viewModel::focusComment,
        )
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class,
)
@Composable
fun CommentsScreen(
    story: LobstersStory?,
    pages: LazyPagingItems<LobstersComment>,
    onBackClick: () -> Unit,
    onUrlClicked: (String?) -> Unit,
    onItemClicked: (LobstersComment) -> Unit,
    onItemDropDownClicked: (LobstersComment) -> Unit,
    onItemLongClicked: (String) -> Unit,
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)

    val isRefreshing = pages.loadState.refresh is LoadState.Loading
    val isError = pages.loadState.refresh is LoadState.Error

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = pages::refresh,
    )

    val snackbarHostState = remember { SnackbarHostState() }

    // TODO this relaunches on configuration change which is really annoying
    //      but since loadState changes to loading on rotation maybe that's correct?
    if (isError) LaunchedEffect(snackbarHostState) {
        snackbarHostState.showSnackbar("Failed to refresh")
    }

    // All this dragging and shadow business should get wrapped up in a "LemonSheet" or something
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    if (story != null) item(key = story.shortId) {
                        Story(
                            modifier = Modifier.padding(8.dp),
                            story = story,
                            onClick = {
                                // TODO shouldn't even be clickable if no url?
                                // but how to interact with long click for author?
                                if (story.url.isNotBlank())
                                    onUrlClicked(story.url)
                            },
                            onLongClick = onItemLongClicked,
                            isCompact = false,
                            onLinkClicked = { onUrlClicked(it) },
                        )
                    }

                    if (pages.itemCount == 0)
                        item(key = "no-comments-spacer") {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "No comments",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall,
                                fontStyle = FontStyle.Italic,
                            )
                        }
                    else {
                        items(
                            count = pages.itemCount,
                            key = pages.itemKey { comment -> comment.shortId },
                        ) { idx ->
                            val item = pages[idx]
                            requireNotPlaceholder(item)

                            CommentsItem(
                                modifier = Modifier.animateItemPlacement(),
                                item = item,
                                storyAuthor = story?.submitter?.username ?: "",
                                onLinkClicked = onUrlClicked,
                                onItemClicked = {
                                    onItemClicked(item)
                                },
                                onItemLongClicked = {
                                    onItemLongClicked(item.commentingUser.username)
                                },
                                onDropDownClicked = {
                                    onItemDropDownClicked(item)
                                },
                            )
                        }

                        item(key = "spacer-end-of-list") {
                            Spacer(modifier = Modifier.size(64.dp))
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
