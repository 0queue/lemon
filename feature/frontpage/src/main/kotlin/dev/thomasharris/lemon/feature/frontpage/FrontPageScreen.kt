package dev.thomasharris.lemon.feature.frontpage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
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

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class,
)
@Composable
fun FrontPageScreen(
    onClick: (String) -> Unit,
    pages: LazyPagingItems<FrontPageItem>,
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
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Lemon for Lobsters")
                },
                scrollBehavior = scrollBehavior,
            )
        },
        content = { innerPadding ->
            Box(
                // Order! Prioritize top bar state changing over pull to refresh to feel "right"
                // when it is the other way around, top app bar will often react "sluggishly"
                modifier = Modifier
                    .pullRefresh(pullRefreshState)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            ) {
                LazyColumn(
                    contentPadding = innerPadding,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(
                        items = pages,
                        key = {
                            when (it) {
                                is FrontPageItem.Story -> it.story.shortId
                                is FrontPageItem.Separator -> "uniquekey:${it.pageNumber}"
                            }
                        },
                    ) { item ->
                        requireNotPlaceholder(item)

                        when (item) {
                            is FrontPageItem.Story -> {
                                Story(
                                    story = item.story,
                                    onClick = onClick,
                                    isCompact = true,
                                )
                            }
                            is FrontPageItem.Separator -> Separator(item.pageNumber)
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

@Composable
fun Separator(
    pageNumber: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Divider(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            thickness = 1.dp,
        )
        Text("Page $pageNumber")
        Divider(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            thickness = 1.dp,
        )
    }
}

@Preview
@Composable
fun SeparatorPreview() {
    Separator(pageNumber = 3)
}
