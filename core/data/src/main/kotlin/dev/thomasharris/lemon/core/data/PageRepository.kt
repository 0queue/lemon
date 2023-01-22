package dev.thomasharris.lemon.core.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import app.cash.sqldelight.paging3.QueryPagingSource
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dev.thomasharris.lemon.core.database.LobstersDatabase
import dev.thomasharris.lemon.core.database.Story
import dev.thomasharris.lemon.core.database.User
import dev.thomasharris.lemon.core.model.LobstersStory
import dev.thomasharris.lemon.core.model.LobstersUser
import dev.thomasharris.lemon.lobstersapi.LobstersService
import dev.thomasharris.lemon.lobstersapi.StoryNetworkEntity
import dev.thomasharris.lemon.lobstersapi.UserNetworkEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

    fun makePagingSource(): PagingSource<Int, LobstersStory> = QueryPagingSource(
        countQuery = lobstersDatabase.storyQueries.countStoriesOnFrontPage(),
        transacter = lobstersDatabase.storyQueries,
        context = Dispatchers.IO,
    ) { limit, offset ->
        lobstersDatabase.storyQueries.getStoriesOnFrontPageWithUsers(
            limit = limit,
            offset = offset,
            mapper = mapper,
        )
    }

    // TODO errors from service
    suspend fun loadPage(
        pageIndex: Int,
        clearStories: Boolean = false,
    ) {
        // TODO remove delay
        delay(2000)
        lobstersService.getPage(pageIndex).onSuccess { page ->
            withContext(Dispatchers.IO) {
                lobstersDatabase.transaction {
                    if (clearStories)
                        lobstersDatabase.storyQueries.deleteStories()

                    page.forEachIndexed { index, story ->
                        lobstersDatabase.storyQueries.insertStory(
                            story.asDbStory(pageIndex, index),
                        )
                        lobstersDatabase.userQueries.insertUser(
                            user = story.submitter.asDbUser(),
                        )
                    }
                }
            }
        }.onFailure { t ->
            Log.e("TEH", "lobstersService.getPage failed", t)
        }
    }

    suspend fun isOutOfDate(): Boolean {
        val oldestStory = withContext(Dispatchers.IO) {
            lobstersDatabase.storyQueries.getOldestStory().executeAsOneOrNull()
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
) : RemoteMediator<Int, LobstersStory>() {

//    override suspend fun initialize(): InitializeAction =
//        if (pageRepository.isOutOfDate())
//            InitializeAction.LAUNCH_INITIAL_REFRESH
//        else
//            InitializeAction.SKIP_INITIAL_REFRESH

    /**
     * Be careful when changing some loading parameters,
     * while working on this I ended up with a database with
     * pages 1 and 3 (no 2), and it ended up in a loading loop
     * and lobste.rs returned a 500 error.  It seems the
     * [app.cash.sqldelight.paging3.OffsetQueryPagingSource]
     * in that situation, when initially loading, found the
     * two pages and thought no more data was available.  This
     * triggered the [RemoteMediator] to load more pages,
     * which found 50 items and thus wanted to load page 3,
     * which it did, which overwrote the page because it exists,
     * which invalidated the PagingSource, which triggered the initial
     * load again...
     *
     * TODO never let this happen.  Maybe a check in initialize?
     */
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, LobstersStory>,
    ): MediatorResult = when (loadType) {
        LoadType.REFRESH -> {
            Log.i("TEH", "REFRESHING")
            pageRepository.loadPage(pageIndex = 1, clearStories = true)
            MediatorResult.Success(endOfPaginationReached = false)
        }
        LoadType.PREPEND -> MediatorResult.Success(endOfPaginationReached = true)
        LoadType.APPEND -> {
            Log.i("TEH", "APPENDING")
            val numberOfLoadedStories = state.pages.sumOf { page ->
                page.data.size
            }

            // TODO extract a constant which is like lobster_page_size
            val numberOfFullPages = numberOfLoadedStories.div(25)
            val pageIndex = numberOfFullPages.plus(1)

            pageRepository.loadPage(pageIndex)
            // TODO would be cool to add a page limit for conscious media consumption
            MediatorResult.Success(endOfPaginationReached = false)
        }
    }
}

fun StoryNetworkEntity.asDbStory(
    pageIndex: Int?,
    pageSubIndex: Int?,
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

fun UserNetworkEntity.asDbUser(): User = User(
    username = username,
    createdAt = createdAt,
    isAdmin = isAdmin,
    about = about,
    isModerator = isModerator,
    karma = karma,
    avatarShortUrl = avatarUrl,
    invitedByUser = invitedByUser,
    insertedAt = Clock.System.now(),
    githubUsername = githubUsername,
    twitterUsername = twitterUsername,
)

// not sure why this can't be a function
internal val mapper = {
        shortId: String,
        createdAt: Instant,
        title: String,
        url: String,
        score: Int,
        commentCount: Int,
        description: String,
        tags: List<String>,
        username: String,
        userCreatedAt: Instant,
        isAdmin: Boolean,
        about: String,
        isModerator: Boolean,
        karma: Int,
        avatarShortUrl: String,
        invitedByUser: String?,
        githubUsername: String?,
        twitterUsername: String?,
    ->

    val user = LobstersUser(
        username = username,
        createdAt = userCreatedAt,
        about = about,
        isAdmin = isAdmin,
        isModerator = isModerator,
        karma = karma,
        avatarUrl = avatarShortUrl,
        invitedByUser = invitedByUser,
        githubUsername = githubUsername,
        twitterUsername = twitterUsername,
    )

    LobstersStory(
        shortId = shortId,
        createdAt = createdAt,
        title = title,
        url = url,
        score = score,
        commentCount = commentCount,
        description = description,
        submitter = user,
        tags = tags,
    )
}
