package dev.thomasharris.lemon.core.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import app.cash.sqldelight.paging3.QueryPagingSource
import com.github.michaelbull.result.unwrap
import dev.thomasharris.lemon.core.database.LobstersDatabase
import dev.thomasharris.lemon.core.database.Story
import dev.thomasharris.lemon.lobstersapi.LobstersService
import dev.thomasharris.lemon.model.LobstersStory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.hours

@Singleton
class PageRepository @Inject constructor(
    private val lobstersService: LobstersService,
    private val lobstersDatabase: LobstersDatabase,
) {
    fun makePagingSource(): PagingSource<Int, Story> {
        return QueryPagingSource(
            countQuery = lobstersDatabase.lobstersQueries.countStories(),
            transacter = lobstersDatabase.lobstersQueries,
            context = Dispatchers.IO,
        ) { limit, offset ->
            // todo custom mapper here?
            lobstersDatabase.lobstersQueries.getStories(limit, offset)
        }
    }

    // TODO errors from service
    suspend fun refresh() {
        val firstPage = lobstersService.getPage(1).unwrap()

        lobstersDatabase.transaction {
            lobstersDatabase.lobstersQueries.deleteStories()
            firstPage.forEachIndexed { index, story ->
                lobstersDatabase.lobstersQueries.insertStory(
                    story.asDbStory(1, index),
                )
            }
        }
    }

    // TODO errors from service
    suspend fun fetchPage(pageIndex: Int) {
        val page = lobstersService.getPage(pageIndex).unwrap()
        lobstersDatabase.transaction {
            page.forEachIndexed { index, story ->
                lobstersDatabase.lobstersQueries.insertStory(
                    story.asDbStory(pageIndex, index),
                )
            }
        }
    }

    suspend fun isOutOfDate(): Boolean {
        val oldestStory = withContext(Dispatchers.IO) {
            lobstersDatabase.lobstersQueries.getOldestStory().executeAsOneOrNull()
        }?.min?.let(Instant::fromEpochMilliseconds)

        return if (oldestStory != null) {
            val duration = Clock.System
                .now()
                .minus(oldestStory)

            duration > 1.hours
        } else true
    }
}

@OptIn(ExperimentalPagingApi::class)
@Singleton
class PageMediator @Inject constructor(
    private val pageRepository: PageRepository,
) : RemoteMediator<Int, Story>() {

    override suspend fun initialize(): InitializeAction =
        if (pageRepository.isOutOfDate())
            InitializeAction.LAUNCH_INITIAL_REFRESH
        else
            InitializeAction.SKIP_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Story>,
    ): MediatorResult = when (loadType) {
        LoadType.REFRESH -> {
            pageRepository.refresh()
            MediatorResult.Success(endOfPaginationReached = false)
        }
        LoadType.PREPEND -> MediatorResult.Success(endOfPaginationReached = true)
        LoadType.APPEND -> {
            val pageIndex = state.lastItemOrNull()?.pageIndex?.plus(1) ?: 1
            pageRepository.fetchPage(pageIndex)
            // TODO would be cool to add a page limit for conscious media consumption
            MediatorResult.Success(endOfPaginationReached = false)
        }
    }
}

fun LobstersStory.asDbStory(
    pageIndex: Int,
    pageSubIndex: Int,
) = Story(
    shortId = shortId,
    title = title,
    createdAt = createdAt,
    url = url,
    score = score,
    commentCount = commentCount,
    description = description,
    username = submitter.username,
    tags = tags,
    pageIndex = pageIndex,
    pageSubIndex = pageSubIndex,
    insertedAt = Clock.System.now(),
)