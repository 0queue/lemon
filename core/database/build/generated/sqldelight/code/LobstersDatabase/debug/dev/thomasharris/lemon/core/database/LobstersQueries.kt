package dev.thomasharris.lemon.core.database

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.SqlDriver
import kotlinx.datetime.Instant
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.List

public class LobstersQueries(
    driver: SqlDriver,
    private val storyAdapter: Story.Adapter,
    private val userAdapter: User.Adapter,
    private val commentAdapter: Comment.Adapter,
) : TransacterImpl(driver) {
    public fun <T : Any> getStories(
        mapper: (
            shortId: String,
            title: String,
            createdAt: Instant,
            url: String,
            score: Int,
            commentCount: Int,
            description: String,
            username: String,
            tags: List<String>,
            pageIndex: Int,
            pageSubIndex: Int,
            insertedAt: Instant,
        ) -> T,
    ): Query<T> = Query(
        348937354,
        arrayOf("story"),
        driver,
        "lobsters.sq",
        "getStories",
        "SELECT * FROM story",
    ) { cursor ->
        mapper(
            cursor.getString(0)!!,
            cursor.getString(1)!!,
            storyAdapter.createdAtAdapter.decode(cursor.getLong(2)!!),
            cursor.getString(3)!!,
            storyAdapter.scoreAdapter.decode(cursor.getLong(4)!!),
            storyAdapter.commentCountAdapter.decode(cursor.getLong(5)!!),
            cursor.getString(6)!!,
            cursor.getString(7)!!,
            storyAdapter.tagsAdapter.decode(cursor.getString(8)!!),
            storyAdapter.pageIndexAdapter.decode(cursor.getLong(9)!!),
            storyAdapter.pageSubIndexAdapter.decode(cursor.getLong(10)!!),
            storyAdapter.insertedAtAdapter.decode(cursor.getLong(11)!!),
        )
    }

    public fun getStories(): Query<Story> = getStories { shortId, title, createdAt, url, score,
        commentCount, description, username, tags, pageIndex, pageSubIndex, insertedAt, ->
        Story(
            shortId,
            title,
            createdAt,
            url,
            score,
            commentCount,
            description,
            username,
            tags,
            pageIndex,
            pageSubIndex,
            insertedAt,
        )
    }

    public fun <T : Any> getUsers(
        mapper: (
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
            twitterUsername: String?,
        ) -> T,
    ): Query<T> = Query(
        -1070453761,
        arrayOf("user"),
        driver,
        "lobsters.sq",
        "getUsers",
        "SELECT * FROM user",
    ) { cursor ->
        mapper(
            cursor.getString(0)!!,
            userAdapter.createdAtAdapter.decode(cursor.getLong(1)!!),
            cursor.getBoolean(2)!!,
            cursor.getString(3)!!,
            cursor.getBoolean(4)!!,
            userAdapter.karmaAdapter.decode(cursor.getLong(5)!!),
            cursor.getString(6)!!,
            cursor.getString(7),
            userAdapter.insertedAtAdapter.decode(cursor.getLong(8)!!),
            cursor.getString(9),
            cursor.getString(10),
        )
    }

    public fun getUsers(): Query<User> = getUsers { username, createdAt, isAdmin, about, isModerator,
        karma, avatarShortUrl, invitedByUser, insertedAt, githubUsername, twitterUsername, ->
        User(
            username,
            createdAt,
            isAdmin,
            about,
            isModerator,
            karma,
            avatarShortUrl,
            invitedByUser,
            insertedAt,
            githubUsername,
            twitterUsername,
        )
    }

    public fun <T : Any> getComments(
        mapper: (
            shortId: String,
            storyId: String,
            commentIndex: Int,
            shortIdUrl: String,
            createdAt: Instant,
            updatedAt: Instant,
            isDeleted: Boolean,
            isModerated: Boolean,
            score: Int,
            comment: String,
            indentLevel: Int,
            username: String,
            insertedAt: Instant,
        ) -> T,
    ): Query<T> = Query(
        -92575587,
        arrayOf("comment"),
        driver,
        "lobsters.sq",
        "getComments",
        "SELECT * FROM comment",
    ) { cursor ->
        mapper(
            cursor.getString(0)!!,
            cursor.getString(1)!!,
            commentAdapter.commentIndexAdapter.decode(cursor.getLong(2)!!),
            cursor.getString(3)!!,
            commentAdapter.createdAtAdapter.decode(cursor.getLong(4)!!),
            commentAdapter.updatedAtAdapter.decode(cursor.getLong(5)!!),
            cursor.getBoolean(6)!!,
            cursor.getBoolean(7)!!,
            commentAdapter.scoreAdapter.decode(cursor.getLong(8)!!),
            cursor.getString(9)!!,
            commentAdapter.indentLevelAdapter.decode(cursor.getLong(10)!!),
            cursor.getString(11)!!,
            commentAdapter.insertedAtAdapter.decode(cursor.getLong(12)!!),
        )
    }

    public fun getComments(): Query<Comment> = getComments { shortId, storyId, commentIndex,
        shortIdUrl, createdAt, updatedAt, isDeleted, isModerated, score, comment, indentLevel,
        username, insertedAt, ->
        Comment(
            shortId,
            storyId,
            commentIndex,
            shortIdUrl,
            createdAt,
            updatedAt,
            isDeleted,
            isModerated,
            score,
            comment,
            indentLevel,
            username,
            insertedAt,
        )
    }
}
