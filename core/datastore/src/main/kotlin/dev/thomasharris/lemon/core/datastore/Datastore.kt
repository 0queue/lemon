package dev.thomasharris.lemon.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsSerializer @Inject constructor() : Serializer<Settings> {
    override val defaultValue = DefaultSettings

    override suspend fun readFrom(input: InputStream): Settings {
        TODO("Not yet implemented")
    }

    override suspend fun writeTo(t: Settings, output: OutputStream) {
        TODO("Not yet implemented")
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatastoreModule {
    @Provides
    @Singleton
    fun provideSettingsDatastore(
        @ApplicationContext
        applicationContext: Context,
        settingsSerializer: SettingsSerializer,
    ): DataStore<Settings> = DataStoreFactory.create(
        serializer = settingsSerializer,
        produceFile = { applicationContext.dataStoreFile("settings.json") },
    )
}
