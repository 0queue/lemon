package dev.thomasharris.lemon.feature.comments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.thomasharris.lemon.core.data.CommentsMediator
import dev.thomasharris.lemon.core.data.CommentsRepository
import dev.thomasharris.lemon.core.model.LobstersComment
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
@HiltViewModel
class CommentsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    commentsMediatorFactory: CommentsMediator.CommentsMediatorFactory,
    private val commentsRepository: CommentsRepository,
) : ViewModel() {

    private val args = CommentsArgs.fromSavedState(savedStateHandle)

    private val commentsMediator = commentsMediatorFactory.create(args.storyId)

    val pager = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false,
        ),
        pagingSourceFactory = {
            commentsRepository.makePagingSource(args.storyId)
        },
        remoteMediator = commentsMediator,
    ).flow.cachedIn(viewModelScope)

    val id: String
        get() = args.storyId

    val story = commentsRepository
        .storyFlow(args.storyId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    fun toggleComment(item: LobstersComment) {
        viewModelScope.launch {
            val newVisibility = when (item.visibility) {
                LobstersComment.Visibility.VISIBLE -> LobstersComment.Visibility.COMPACT
                LobstersComment.Visibility.COMPACT -> LobstersComment.Visibility.VISIBLE
                LobstersComment.Visibility.GONE -> LobstersComment.Visibility.GONE
            }
            commentsRepository.setVisibility(item.shortId, newVisibility)
        }
    }
}
