package dev.thomasharris.lemon

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.github.michaelbull.result.unwrap
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import dev.thomasharris.lemon.core.database.LobstersDatabase
import dev.thomasharris.lemon.core.database.Story
import dev.thomasharris.lemon.lobstersapi.LobstersService
import dev.thomasharris.lemon.model.LobstersStory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
object ThrowawayModule {
    @Provides
    fun provideLobstersService() = LobstersService()
}

@HiltViewModel
class ThrowawayViewModel @Inject constructor(
    private val lobstersService: LobstersService,
    private val lobstersDatabase: LobstersDatabase,
    private val pagingSourceFactory: @JvmSuppressWildcards () -> PagingSource<Int, Story>,
) : ViewModel() {

    val counterState = MutableStateFlow(0)

    val pages = Pager(
        config = PagingConfig(
            pageSize = 25,
            prefetchDistance = 2,
        ),
        pagingSourceFactory = pagingSourceFactory,
    ).flow.cachedIn(viewModelScope)

    suspend fun getPage(): List<LobstersStory> {
        return lobstersService.getPage(1).unwrap()
    }

    fun increment() {
        viewModelScope.launch {
            delay(1000)
            counterState.value += 1
        }
    }

    fun insertStories() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Log.i("TEH", "deleting")
                lobstersDatabase.lobstersQueries.deleteStories()
            }

            delay(2000)

            val stories = lobstersService.getPage(1).unwrap()

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
                            pageIndex = 1,
                            pageSubIndex = index,
                            insertedAt = Clock.System.now(),
                        ),
                    )
                }
            }
        }
    }
}
