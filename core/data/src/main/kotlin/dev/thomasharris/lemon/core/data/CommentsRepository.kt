package dev.thomasharris.lemon.core.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.paging3.QueryPagingSource
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.thomasharris.lemon.core.database.Comment
import dev.thomasharris.lemon.core.database.LobstersDatabase
import dev.thomasharris.lemon.core.model.LobstersComment
import dev.thomasharris.lemon.core.model.LobstersStory
import dev.thomasharris.lemon.core.model.LobstersUser
import dev.thomasharris.lemon.lobstersapi.CommentNetworkEntity
import dev.thomasharris.lemon.lobstersapi.LobstersService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.hours

@Singleton
class CommentsRepository @Inject constructor(
    private val lobstersService: LobstersService,
    private val lobstersDatabase: LobstersDatabase,
) {

    fun storyFlow(
        storyId: String,
    ): Flow<LobstersStory?> = lobstersDatabase
        .storyQueries
        .getStoryWithUser(
            storyId = storyId,
            mapper = storyMapper,
        )
        .asFlow()
        .map { query ->
            withContext(Dispatchers.IO) {
                query.executeAsOneOrNull()
            }
        }

    fun makePagingSource(
        storyId: String,
    ): PagingSource<Int, LobstersComment> = QueryPagingSource(
        countQuery = lobstersDatabase.commentQueries.countCommentsWithStoryId(storyId),
        transacter = lobstersDatabase.commentQueries,
        context = Dispatchers.IO,
    ) { limit, offset ->
        lobstersDatabase.commentQueries.getCommentsWithUserByStoryId(
            storyId = storyId,
            limit = limit,
            offset = offset,
            mapper = commentMapper,
        )
    }

    suspend fun loadComments(
        storyId: String,
        clearComments: Boolean = false,
    ) {
        lobstersService.getStory(storyId).onSuccess { (story, comments) ->
            withContext(Dispatchers.IO) {
                lobstersDatabase.transaction {
                    lobstersDatabase.commentQueries.deleteCommentsWithStoryId(storyId)

                    val previousVersion = lobstersDatabase
                        .storyQueries
                        .getStory(storyId)
                        .executeAsOneOrNull()

                    val dbStory = story.asDbStory(
                        pageIndex = previousVersion?.pageIndex,
                        pageSubIndex = previousVersion?.pageSubIndex,
                    )

                    lobstersDatabase.storyQueries.insertStory(dbStory)

                    if (clearComments)
                        lobstersDatabase.commentQueries.deleteCommentsWithStoryId(storyId)

                    comments.forEachIndexed { index, comment ->
                        lobstersDatabase.commentQueries
                            .insertComment(comment.asDbComment(storyId, index))

                        lobstersDatabase.userQueries
                            .insertUser(comment.commentingUser.asDbUser())
                    }
                }
            }
        }.onFailure { t ->
            Log.e("TEH", "lobstersService.getStory failed", t)
        }
    }

    suspend fun isOutOfDate(
        shortId: String,
    ): Boolean = withContext(Dispatchers.IO) {
        val oldestComment = lobstersDatabase.commentQueries
            .getOldestComment(shortId)
            .executeAsOneOrNull()
            ?.min
            ?.let(Instant::fromEpochMilliseconds)
            ?: return@withContext true

        Clock.System
            .now()
            .minus(oldestComment) > 1.hours
    }
}

@OptIn(ExperimentalPagingApi::class)
class CommentsMediator @AssistedInject constructor(
    private val commentsRepository: CommentsRepository,
    @Assisted
    private val storyId: String,
) : RemoteMediator<Int, LobstersComment>() {

//    override suspend fun initialize(): InitializeAction =
//        if (commentsRepository.isOutOfDate(storyId))
//            InitializeAction.LAUNCH_INITIAL_REFRESH
//        else
//            InitializeAction.SKIP_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, LobstersComment>,
    ): MediatorResult = when (loadType) {
        LoadType.REFRESH -> {
            // TODO maybe check if out of date here?
            commentsRepository.loadComments(
                storyId = storyId,
                clearComments = true,
            )
            MediatorResult.Success(endOfPaginationReached = false)
        }
        LoadType.PREPEND -> MediatorResult.Success(endOfPaginationReached = true)
        LoadType.APPEND -> {
            commentsRepository.loadComments(
                storyId = storyId,
                clearComments = true,
            )
            MediatorResult.Success(endOfPaginationReached = true)
        }
    }

    @AssistedFactory
    interface CommentsMediatorFactory {
        fun create(storyId: String): CommentsMediator
    }
}

private fun CommentNetworkEntity.asDbComment(
    storyId: String,
    commentIndex: Int,
) = Comment(
    shortId = shortId,
    storyId = storyId,
    commentIndex = commentIndex,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isDeleted = isDeleted,
    isModerated = isModerated,
    score = score,
    comment = comment,
    indentLevel = indentLevel,
    username = commentingUser.username,
    insertedAt = Clock.System.now(),
)

private val commentMapper = {
        shortId: String,
        _: String,
        createdAt: Instant,
        updatedAt: Instant,
        isDeleted: Boolean,
        isModerated: Boolean,
        score: Int,
        comment: String,
        indentLevel: Int,
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

    LobstersComment(
        shortId = shortId,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isDeleted = isDeleted,
        isModerated = isModerated,
        score = score,
        comment = comment,
        indentLevel = indentLevel,
        commentingUser = user,
    )
}

private val storyMapper = {
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
        pageIndex = null,
        pageSubIndex = null,
    )
}
