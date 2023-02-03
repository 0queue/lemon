package dev.thomasharris.lemon.feature.comments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import dev.thomasharris.lemon.core.model.LobstersComment
import dev.thomasharris.lemon.core.model.LobstersStory
import dev.thomasharris.lemon.core.ui.Story
import dev.thomasharris.lemon.core.ui.requireNotPlaceholder
import okhttp3.internal.toHexString

@Composable
fun CommentsRoute(
    viewModel: CommentsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
) {
    val story by viewModel.story.collectAsState()
    val pages = viewModel.pager.collectAsLazyPagingItems()

    val context = LocalContext.current

    val closeButtonIcon = remember(context) {
        // TODO technically now have two sources of truth on that back icon...
        context.getDrawable(R.drawable.baseline_arrow_back_24)!!.toBitmap()
    }

    val colorSurface = MaterialTheme.colorScheme.surface

    CommentsScreen(
        story = story,
        pages = pages,
        onBackClick = onBackClick,
        onUrlClicked = { url ->
            // TODO this should be hoisted even further? It is kind of module-internal though
            if (url != null)
                context.launchUrl(
                    url = url,
                    closeButtonIcon = closeButtonIcon,
                    toolbarColor = colorSurface.toArgb(),
                )
        },
        onItemClicked = viewModel::toggleComment,
        onItemDropDownClicked = viewModel::focusComment,
    )
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
                            onClick = {
                                // TODO shouldn't even be clickable if no url?
                                // but how to interact with long click for author?
                                if (story.url.isNotBlank())
                                    onUrlClicked(story.url)
                            },
                            isCompact = false,
                            onLinkClicked = { onUrlClicked(it) },
                        )
                    }

                    if (pages.itemCount == 0)
                        item {
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
                            items = pages,
                            key = { comment -> comment.shortId },
                        ) { item ->
                            requireNotPlaceholder(item)

                            CommentsItem(
                                modifier = Modifier.animateItemPlacement(),
                                item = item,
                                storyAuthor = story?.submitter?.username ?: "",
                                onLinkClicked = onUrlClicked,
                                onItemClicked = {
                                    onItemClicked(item)
                                },
                                onDropDownClicked = {
                                    onItemDropDownClicked(item)
                                },
                            )
                        }

                        item {
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

// TODO Uri.parse is very throwy, handle it by showing
//      an error toast or snackbar
fun Context.launchUrl(
    url: String,
    closeButtonIcon: Bitmap,
    @ColorInt
    toolbarColor: Int,
) {
    val defaultColors = CustomTabColorSchemeParams.Builder()
        .setToolbarColor(toolbarColor)
        .build()

    Log.i("TEH", "Toolbar color: ${toolbarColor.toHexString()}")

    CustomTabsIntent.Builder()
        .setStartAnimations(this, R.anim.slide_in_from_right, R.anim.nothing)
        // Not currently working...
        .setExitAnimations(this, R.anim.nothing, R.anim.slide_out_to_right)
        .setCloseButtonIcon(closeButtonIcon)
        .setDefaultColorSchemeParams(defaultColors)
        .build()
        .launchUrl(this, Uri.parse(url))
}

fun Drawable.toBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(
        intrinsicWidth,
        intrinsicHeight,
        Bitmap.Config.ARGB_8888,
    )

    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}
