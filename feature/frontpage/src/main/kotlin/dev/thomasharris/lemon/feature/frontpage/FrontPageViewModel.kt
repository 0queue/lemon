package dev.thomasharris.lemon.feature.frontpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.thomasharris.lemon.core.data.PageMediator
import dev.thomasharris.lemon.core.data.PageRepository
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
@HiltViewModel
class FrontPageViewModel @Inject constructor(
    private val pageRepository: PageRepository,
    pageMediator: PageMediator,
) : ViewModel() {
    val pages = Pager(
        config = PagingConfig(
            pageSize = 25,
            prefetchDistance = 50,
        ),
        pagingSourceFactory = pageRepository::makePagingSource,
        remoteMediator = pageMediator,
    ).flow.cachedIn(viewModelScope)
}