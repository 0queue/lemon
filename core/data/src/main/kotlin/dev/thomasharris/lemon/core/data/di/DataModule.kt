package dev.thomasharris.lemon.core.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.thomasharris.lemon.lobstersapi.LobstersService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideLobstersService(): LobstersService =
        LobstersService()
}
