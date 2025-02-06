package dev.jylha.station.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.jylha.station.data.settings.DefaultSettingsRepository
import dev.jylha.station.domain.SettingsRepository
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("preferences")

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {

    @Binds
    @Singleton
    abstract fun bind(repository: DefaultSettingsRepository): SettingsRepository

    companion object {

        @Provides
        @Singleton
        fun provideDataStorePreferences(
            @ApplicationContext applicationContext: Context
        ): DataStore<Preferences> {
            return applicationContext.dataStore
        }
    }
}
