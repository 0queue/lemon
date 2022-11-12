package dev.thomasharris.lemon.core.data

import app.cash.sqldelight.coroutines.asFlow
import com.github.michaelbull.result.unwrap
import dev.thomasharris.lemon.core.database.Comment
import dev.thomasharris.lemon.core.database.LobstersDatabase
import dev.thomasharris.lemon.lobstersapi.CommentNetworkEntity
import dev.thomasharris.lemon.lobstersapi.LobstersService
import dev.thomasharris.lemon.model.LobstersComment
import dev.thomasharris.lemon.model.LobstersUser
import kotlinx.coroutines.Dispatchers
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

    suspend fun commentsFlow(storyId: String) {
        val commentsFlow = lobstersDatabase.lobstersQueries
            .getCommentsWithUserByStoryId(
                storyId = storyId,
                mapper = commentMapper,
            )
            .asFlow()
            .map { query ->
                withContext(Dispatchers.IO) {
                    query.executeAsList()
                }
            }

        // TODO fetch single story as well
    }

    suspend fun fetchComments(
        shortId: String,
        forceRefresh: Boolean = false,
    ) {
        if (!forceRefresh && !shouldRefresh(shortId))
            return

        val (story, comments) = lobstersService.getStory(shortId).unwrap()

        withContext(Dispatchers.IO) {
            lobstersDatabase.lobstersQueries.transaction {
                lobstersDatabase.lobstersQueries.deleteCommentsWithStoryId(shortId)

                val previousVersion = lobstersDatabase.lobstersQueries
                    .getStory(shortId)
                    .executeAsOneOrNull()

                val dbStory = story.asDbStory(
                    pageIndex = previousVersion?.pageIndex,
                    pageSubIndex = previousVersion?.pageSubIndex,
                )

                lobstersDatabase.lobstersQueries.insertStory(dbStory)

                comments.forEachIndexed { index, comment ->
                    lobstersDatabase.lobstersQueries
                        .insertComment(comment.asDbComment(shortId, index))
                }
            }
        }
    }

    private suspend fun shouldRefresh(
        shortId: String,
    ): Boolean = withContext(Dispatchers.IO) {
        val oldestComment = lobstersDatabase.lobstersQueries
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
        storyId: String,
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
        isAdmin = isAdmin,
        about = about,
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
