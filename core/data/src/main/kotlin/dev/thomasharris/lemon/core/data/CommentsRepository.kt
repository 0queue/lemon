package dev.thomasharris.lemon.core.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.paging3.QueryPagingSource
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.binding.binding
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.merge
import com.github.michaelbull.result.onFailure
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.thomasharris.lemon.core.database.Comment
import dev.thomasharris.lemon.core.database.CommentVisibility
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
        lobstersDatabase.commentQueries.getVisibleCommentsWithUserByStoryId(
            storyId = storyId,
            limit = limit,
            offset = offset,
            mapper = commentMapper,
        )
    }

    suspend fun loadComments(
        storyId: String,
        clearComments: Boolean = false,
    ): Result<Unit, Throwable> = binding {
        val (story, comments) = lobstersService.getStory(storyId)
            .onFailure { t -> Log.e("TEH", "lobstersService.getStory failed", t) }
            .bind()

        val now = Clock.System.now()

        withContext(Dispatchers.IO) {
            val commentsWithChildCount = comments.countChildren()

            lobstersDatabase.transaction {
                lobstersDatabase.commentQueries.deleteCommentsWithStoryId(storyId)

                val previousVersion = lobstersDatabase
                    .storyQueries
                    .getStory(storyId)
                    .executeAsOneOrNull()

                val dbStory = story.asDbStory(
                    pageIndex = previousVersion?.pageIndex,
                    pageSubIndex = previousVersion?.pageSubIndex,
                    insertedAt = now,
                )

                lobstersDatabase.storyQueries.insertStory(dbStory)

                if (clearComments)
                    lobstersDatabase.commentQueries.deleteCommentsWithStoryId(storyId)

                commentsWithChildCount.forEachIndexed { index, (comment, childCount) ->
                    lobstersDatabase.commentQueries
                        .insertComment(
                            comment.asDbComment(
                                storyId = storyId,
                                commentIndex = index,
                                insertedAt = now,
                                childCount = childCount,
                            ),
                        )

                    lobstersDatabase.userQueries
                        .insertUser(comment.commentingUser.asDbUser())
                }
            }
        }
    }

    suspend fun toggleThread(comment: LobstersComment) {
        val (newParentVisibility, newChildVisibility) = when (comment.visibility) {
            LobstersComment.Visibility.VISIBLE -> LobstersComment.Visibility.COMPACT to LobstersComment.Visibility.GONE
            LobstersComment.Visibility.COMPACT -> LobstersComment.Visibility.VISIBLE to LobstersComment.Visibility.VISIBLE
            LobstersComment.Visibility.GONE -> {
                Log.e("TEH", "Should not be able to toggle a gone item")
                return
            }
        }

        withContext(Dispatchers.IO) {
            lobstersDatabase.commentQueries.transaction {
                lobstersDatabase.commentQueries.setVisibility(
                    visibility = newParentVisibility.toDB(),
                    shortId = comment.shortId,
                )

                if (comment.childCount > 0) lobstersDatabase.commentQueries.setVisibilityInRange(
                    visibility = newChildVisibility.toDB(),
                    storyId = comment.storyId,
                    lowerIndex = comment.commentIndex,
                    // essentially the last child index, which is why the bound is inclusive
                    upperIndex = comment.commentIndex.plus(comment.childCount),
                )
            }
        }
    }

    suspend fun focusCommentThread(comment: LobstersComment) {
        withContext(Dispatchers.IO) {
            lobstersDatabase.commentQueries.transaction {
                val predecessors = lobstersDatabase.commentQueries.getPredecessors(
                    storyId = comment.storyId,
                    commentIndex = comment.commentIndex,
                ).executeAsList()

                var currentIndentLevel = comment.indentLevel

                for (c in predecessors.reversed()) {
                    when {
                        c.indentLevel < currentIndentLevel -> {
                            lobstersDatabase.commentQueries.setVisibility(
                                visibility = CommentVisibility.VISIBLE,
                                shortId = c.shortId,
                            )

                            currentIndentLevel = c.indentLevel
                        }
                        c.indentLevel == currentIndentLevel ->
                            lobstersDatabase.commentQueries.setVisibility(
                                visibility = CommentVisibility.COMPACT,
                                shortId = c.shortId,
                            )
                        else ->
                            lobstersDatabase.commentQueries.setVisibility(
                                visibility = CommentVisibility.GONE,
                                shortId = c.shortId,
                            )
                    }
                }
            }
        }
    }

    suspend fun isOutOfDate(
        storyId: String,
    ): Boolean = withContext(Dispatchers.IO) {
        val story = lobstersDatabase.storyQueries
            .getStory(storyId)
            .executeAsOneOrNull()
            ?: return@withContext true

        val oldestComment = lobstersDatabase.commentQueries
            .getOldestComment(storyId)
            .executeAsOneOrNull()
            ?.min
            ?.let(Instant::fromEpochMilliseconds)
            ?: return@withContext true

        val isOld = Clock.System
            .now()
            .minus(oldestComment) > 1.hours

        story.insertedAt != oldestComment || isOld
    }
}

@OptIn(ExperimentalPagingApi::class)
class CommentsMediator @AssistedInject constructor(
    private val commentsRepository: CommentsRepository,
    @Assisted
    private val storyId: String,
) : RemoteMediator<Int, LobstersComment>() {

    override suspend fun initialize(): InitializeAction {
        val initializeAction = if (commentsRepository.isOutOfDate(storyId))
            InitializeAction.LAUNCH_INITIAL_REFRESH
        else
            InitializeAction.SKIP_INITIAL_REFRESH

        Log.i("TEH", "### comments initializeAction=$initializeAction ###")

        return initializeAction
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, LobstersComment>,
    ): MediatorResult {
        return when (loadType) {
            LoadType.REFRESH -> {
                commentsRepository
                    .loadComments(
                        storyId = storyId,
                        clearComments = true,
                    )
                    .map { MediatorResult.Success(endOfPaginationReached = false) }
                    .mapError(MediatorResult::Error)
                    .merge()
            }
            LoadType.PREPEND -> MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> MediatorResult.Success(endOfPaginationReached = true)
        }
    }

    @AssistedFactory
    interface CommentsMediatorFactory {
        fun create(storyId: String): CommentsMediator
    }
}

/**
 * Iterate backwards over the list of comments, remembering
 * the last comment index for each indent level and calculate
 * the child count off of that
 *
 * The final thread will have the total number of comments as the
 * next sibling so that child calculations work as expected
 *
 * Also just making sure that I can still work with indices...
 */
private fun List<CommentNetworkEntity>.countChildren(): List<Pair<CommentNetworkEntity, Int>> {
    // move this to mystack when it gets zero length checks
    if (isEmpty())
        return emptyList()

    val list = MyList(last().indentLevel, size)

    val result = mutableListOf<Pair<CommentNetworkEntity, Int>>()

    for (commentIndex in size.minus(1) downTo 0) {
        val comment = get(commentIndex)
        val nextSiblingOrParentIndex = list.get(comment.indentLevel)
        list.set(comment.indentLevel, commentIndex)
        val childCount = nextSiblingOrParentIndex?.minus(commentIndex)?.minus(1) ?: 0

        result.add(0, comment to childCount)
    }

    return result
}

/**
 * a weird little auto extending and retracting list
 */
private class MyList constructor(
    // indent level outside is 1 based but we are 0 based
    lastIndentLevel: Int,
    commentCount: Int,
) {

    private val stack = MutableList(lastIndentLevel.minus(1).plus(1)) { commentCount }

    // TODO plenty of zero length checks and such
    fun set(indentLevel: Int, siblingOrParentIndex: Int) {
        val zeroBasedIndentLevel = indentLevel.minus(1)

        // extend
        if (zeroBasedIndentLevel > stack.size.minus(1)) {
            // SAFETY: Throws if stack is empty.
            val extendWith = stack.last()
            val extension = List(zeroBasedIndentLevel.minus(stack.size.minus(1))) { extendWith }
            stack.addAll(extension)
        }

        // update
        stack[zeroBasedIndentLevel] = siblingOrParentIndex

        // trim
        for (i in zeroBasedIndentLevel.plus(1) until stack.size) {
            stack.removeAt(i)
        }
    }

    fun get(indentLevel: Int) = stack.getOrNull(indentLevel.minus(1))

    override fun toString() = stack.toString()
}

private fun CommentNetworkEntity.asDbComment(
    storyId: String,
    commentIndex: Int,
    insertedAt: Instant,
    childCount: Int,
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
    insertedAt = insertedAt,
    visibility = CommentVisibility.VISIBLE,
    childCount = childCount,
)

fun CommentVisibility.toModel() = when (this) {
    CommentVisibility.VISIBLE -> LobstersComment.Visibility.VISIBLE
    CommentVisibility.COMPACT -> LobstersComment.Visibility.COMPACT
    CommentVisibility.GONE -> LobstersComment.Visibility.GONE
}

fun LobstersComment.Visibility.toDB() = when (this) {
    LobstersComment.Visibility.VISIBLE -> CommentVisibility.VISIBLE
    LobstersComment.Visibility.COMPACT -> CommentVisibility.COMPACT
    LobstersComment.Visibility.GONE -> CommentVisibility.GONE
}

private val commentMapper = {
        shortId: String,
        storyId: String,
        commentIndex: Int,
        createdAt: Instant,
        updatedAt: Instant,
        isDeleted: Boolean,
        isModerated: Boolean,
        score: Int,
        comment: String,
        indentLevel: Int,
        visibility: CommentVisibility,
        childCount: Int,
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
        storyId = storyId,
        commentIndex = commentIndex,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isDeleted = isDeleted,
        isModerated = isModerated,
        score = score,
        comment = comment,
        indentLevel = indentLevel,
        commentingUser = user,
        visibility = visibility.toModel(),
        childCount = childCount,
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

internal val userMapper = {
        username: String,
        createdAt: Instant,
        isAdmin: Boolean,
        about: String,
        isModerator: Boolean,
        karma: Int,
        avatarShortUrl: String,
        invitedByUser: String?,
        insertedAt: Instant,
        githubUsername: String?,
        twitterUsername: String?, ->

    LobstersUser(
        username = username,
        createdAt = createdAt,
        about = about,
        isAdmin = isAdmin,
        isModerator = isModerator,
        karma = karma,
        avatarUrl = avatarShortUrl,
        invitedByUser = invitedByUser,
        githubUsername = githubUsername,
        twitterUsername = twitterUsername,
    )
}
