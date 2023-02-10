package dev.thomasharris.lemon.feature.comments

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
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
import kotlinx.coroutines.launch
import java.lang.Float.max
import kotlin.math.roundToInt

@Composable
fun CommentsRoute(
    viewModel: CommentsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onUrlClicked: (String?) -> Unit,
    onViewUserProfile: (String) -> Unit,
) {
    val story by viewModel.story.collectAsState()
    val pages = viewModel.pager.collectAsLazyPagingItems()

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

    val offsetX = remember { Animatable(0f) }
    var composableWidth: Int? by remember { mutableStateOf(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isLastDragAmountPositive: Boolean by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // TODO this relaunches on configuration change which is really annoying
    //      but since loadState changes to loading on rotation maybe that's correct?
    if (isError) LaunchedEffect(snackbarHostState) {
        snackbarHostState.showSnackbar("Failed to refresh")
    }

    // All this dragging and shadow business should get wrapped up in a "LemonSheet" or something
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier
            .onSizeChanged {
                composableWidth = it.width
            }
            .offset {
                IntOffset(
                    offsetX.value
                        .div(1.5f)
                        .roundToInt(),
                    0,
                )
            }
            .shadow(4.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        coroutineScope.launch {
                            val w = composableWidth

                            if (w != null && offsetX.value.div(1.5f) > w * .3f && isLastDragAmountPositive) {
                                onBackClick()
                            } else {
                                offsetX.animateTo(0f)
                            }
                        }
                    },
                    onDragCancel = {
                        Toast
                            .makeText(context, "DRAG CANCEL", Toast.LENGTH_SHORT)
                            .show()
                    },
                ) { change, dragAmount ->
                    change.consume()
                    // zero is positive now, take that mathematicians
                    isLastDragAmountPositive = dragAmount >= 0
                    coroutineScope.launch {
                        offsetX.snapTo(max(offsetX.value + dragAmount, 0f))
                    }
                }
            },
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
                            onLongClick = onItemLongClicked,
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
                                onItemLongClicked = {
                                    onItemLongClicked(item.commentingUser.username)
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
// fun Context.launchUrl(
//    url: String,
//    closeButtonIcon: Bitmap,
//    @ColorInt
//    toolbarColor: Int,
// ) {
//    val defaultColors = CustomTabColorSchemeParams.Builder()
//        .setToolbarColor(toolbarColor)
//        .build()
//
//    Log.i("TEH", "Toolbar color: ${toolbarColor.toHexString()}")
//
//    CustomTabsIntent.Builder()
//        .setStartAnimations(this, R.anim.slide_in_from_right, R.anim.nothing)
//        // Not currently working...
//        .setExitAnimations(this, R.anim.nothing, R.anim.slide_out_to_right)
//        .setCloseButtonIcon(closeButtonIcon)
//        .setDefaultColorSchemeParams(defaultColors)
//        .build()
//        .launchUrl(this, Uri.parse(url))
// }
//
// fun Drawable.toBitmap(): Bitmap {
//    val bitmap = Bitmap.createBitmap(
//        intrinsicWidth,
//        intrinsicHeight,
//        Bitmap.Config.ARGB_8888,
//    )
//
//    val canvas = Canvas(bitmap)
//    setBounds(0, 0, canvas.width, canvas.height)
//    draw(canvas)
//    return bitmap
// }
