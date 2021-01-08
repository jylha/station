package dev.jylha.station.di

import android.content.Context
import androidx.room.Room
import dev.jylha.station.data.StationDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CacheModule {

    @Singleton
    @Provides
    fun provideStationDatabase(@ApplicationContext context: Context): StationDatabase {
        return Room.databaseBuilder(context, StationDatabase::class.java, StationDatabase.name)
            .fallbackToDestructiveMigration()
            .build()
    }
}
