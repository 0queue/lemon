package dev.thomasharris.lemon.core.data.di

import androidx.paging.PagingSource
import app.cash.sqldelight.paging3.QueryPagingSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.thomasharris.lemon.core.database.LobstersDatabase
import dev.thomasharris.lemon.core.database.Story
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun providePagingSourceFactory(
        lobstersDatabase: LobstersDatabase,
    ): () -> PagingSource<Int, Story> = {
        QueryPagingSource(
            countQuery = lobstersDatabase.lobstersQueries.countStories(),
            transacter = lobstersDatabase.lobstersQueries,
            context = Dispatchers.IO,
        ) { limit, offset ->
            // todo custom mapper here?
            lobstersDatabase.lobstersQueries.getStories(limit, offset)
        }
    }
}
