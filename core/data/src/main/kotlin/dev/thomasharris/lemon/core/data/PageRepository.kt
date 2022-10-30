package dev.thomasharris.lemon.core.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.github.michaelbull.result.unwrap
import dev.thomasharris.lemon.core.database.LobstersDatabase
import dev.thomasharris.lemon.core.database.Story
import dev.thomasharris.lemon.lobstersapi.LobstersService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import javax.inject.Inject

class PageRepository @Inject constructor(
    private val lobstersService: LobstersService,
    private val lobstersDatabase: LobstersDatabase,
) {
    // alright I guess I need to deal with paging soon
}

@OptIn(ExperimentalPagingApi::class)
class LobstersMediator @Inject constructor(
    private val lobstersService: LobstersService,
    private val lobstersDatabase: LobstersDatabase,
) : RemoteMediator<Int, Story>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Story>,
    ): MediatorResult {
        val pageToLoad = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> state.lastItemOrNull()?.pageIndex?.plus(1) ?: 1
        }


        if (loadType == LoadType.REFRESH) withContext(Dispatchers.IO) {
            Log.i("TEH", "deleting")
            lobstersDatabase.lobstersQueries.deleteStories()
        }

        delay(2000)

        val stories = lobstersService.getPage(pageToLoad).unwrap()

        lobstersDatabase.transaction {
            stories.forEachIndexed { index, story ->
                Log.i("TEH", "Inserting ${story.shortId}")
                lobstersDatabase.lobstersQueries.insertStory(
                    Story(
                        shortId = story.shortId,
                        title = story.title,
                        createdAt = story.createdAt,
                        url = story.url,
                        score = story.score,
                        commentCount = story.commentCount,
                        description = story.description,
                        username = story.submitter.username,
                        tags = story.tags,
                        pageIndex = pageToLoad,
                        pageSubIndex = index,
                        insertedAt = Clock.System.now(),
                    ),
                )
            }
        }

        return MediatorResult.Success(endOfPaginationReached = false)
    }

}