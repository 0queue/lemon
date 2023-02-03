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
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.hours

/**
 * Why does RemoteMediator go REFRESH -> load page 1 -> APPEND -> load page 1?
 *
 * Click on story -> invalidate the paging source due to update. Problem?
 * update: Not the invalidate, but rather loading from an offset causes it to jump by that offset
 * ? https://issuetracker.google.com/issues/235319241
 * idk try placeholders :(
 */
@Singleton
class PageRepository @Inject constructor(
    private val lobstersService: LobstersService,
    private val lobstersDatabase: LobstersDatabase,
) {

    fun makePagingSource(): PagingSource<Int, LobstersStory> = QueryPagingSource(
        countQuery = lobstersDatabase.storyQueries.countStoriesOnFrontPage(),
        transacter = lobstersDatabase,
        context = Dispatchers.IO,
    ) { limit, offset ->

        Log.i("TEH", "QueryPagingSource queryProvider limit=$limit offset=$offset")
        lobstersDatabase.storyQueries.getStoriesOnFrontPageWithUsers(
            limit = limit,
            offset = offset,
            mapper = mapper,
        )
    }.also {
        it.registerInvalidatedCallback {
            Log.i("TEH", ">>> INVALIDATED <<<")
        }
    }

    // TODO errors from service
    suspend fun loadPage(
        pageIndex: Int,
        clearStories: Boolean = false,
    ) {
        Log.i(
            "TEH",
            "PageRepository.loadPage(pageIndex = $pageIndex, clearStories = $clearStories)",
        )
        lobstersService.getPage(pageIndex).onSuccess { page ->
            withContext(Dispatchers.IO) {
                lobstersDatabase.transaction {
                    if (clearStories) {
                        lobstersDatabase.storyQueries.deleteStories()
                    }

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

        Log.i("TEH", "PageRepository.loadPage(...) IS FINISHED")
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

    override suspend fun initialize(): InitializeAction {
        val initializeAction = if (pageRepository.isOutOfDate())
            InitializeAction.LAUNCH_INITIAL_REFRESH
        else
            InitializeAction.SKIP_INITIAL_REFRESH

        Log.i("TEH", "*** INITIALIZING $initializeAction ***")

        return initializeAction
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, LobstersStory>,
    ): MediatorResult {
        Log.i("TEH", "PageMediator.load($loadType, $state)")

        return when (loadType) {
            LoadType.REFRESH -> {
                pageRepository.loadPage(pageIndex = 1, clearStories = true)
                MediatorResult.Success(endOfPaginationReached = false)
            }
            LoadType.PREPEND -> MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val numberOfLoadedStories = state.pages.sumOf { page ->
                    page.data.size
                }

                // TODO extract a constant which is like lobster_page_size
                // TODO actually this logic outside of actualPageToLoad is no longer used because
                //      it is flat out wrong
                val numberOfFullPages = numberOfLoadedStories.div(25)
                val pageIndex = numberOfFullPages.plus(1)

                val pageIndexHistogram = state
                    .pages
                    .map { it.data }
                    .flatten()
                    .groupBy { it.pageIndex }
                    .mapValues { (_, l) -> l.size }

                val actualPageToLoad = state
                    .pages
                    .map { it.data }
                    .flatten()
                    .mapNotNull { it.pageIndex }
                    .maxOrNull()
                    ?.plus(1)
                    ?: 1

                Log.i(
                    "TEH",
                    """
                    Append has decided to load $pageIndex
                    - number of *loaded* stories: $numberOfLoadedStories
                    - number of full pages: $numberOfFullPages
                    - histogram: $pageIndexHistogram
                    - having counted the data: $actualPageToLoad
                    """.trimIndent(),
                )

                pageRepository.loadPage(actualPageToLoad)
                // TODO would be cool to add a page limit for conscious media consumption
                MediatorResult.Success(endOfPaginationReached = false)
            }
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
        pageIndex: Int,
        // TODO probably is non null, should update query to select where not null
        pageSubIndex: Int?,
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
        pageIndex = pageIndex,
        pageSubIndex = pageSubIndex,
    )
}
