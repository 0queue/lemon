package dev.thomasharris.lemon.feature.frontpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.thomasharris.lemon.core.data.PageMediator
import dev.thomasharris.lemon.core.data.PageRepository
import dev.thomasharris.lemon.core.model.LobstersStory
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
@HiltViewModel
class FrontPageViewModel @Inject constructor(
    private val pageRepository: PageRepository,
    pageMediator: PageMediator,
) : ViewModel() {

    val pages = Pager(
        config = PagingConfig(
            enablePlaceholders = false,
            pageSize = 25,
//            prefetchDistance = 25,
        ),
        pagingSourceFactory = pageRepository::makePagingSource,
        remoteMediator = pageMediator,
    )
        .flow
        .map { pagingData ->
            pagingData
                .map(FrontPageItem::Story)
                .insertSeparators { before, after ->
                    // TODO clean up all these nullables after sorting out proper
                    //      handling of page indices
                    when {
                        before == null -> null
                        after == null -> null
                        before.story.pageIndex == after.story.pageIndex?.minus(1) ->
                            after
                                .story
                                .pageIndex
                                ?.let(FrontPageItem::Separator)
                        else -> null
                    }
                }
        }
        .cachedIn(viewModelScope)
}

sealed class FrontPageItem {
    data class Story(
        val story: LobstersStory,
    ) : FrontPageItem()

    data class Separator(
        val pageNumber: Int,
    ) : FrontPageItem()
}
