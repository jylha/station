package com.example.station.di

import android.content.Context
import androidx.room.Room
import com.example.station.data.stations.cache.StationDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object CacheModule {

    @Singleton
    @Provides
    fun provideStationDatabase(@ApplicationContext context: Context): StationDatabase {
        return Room.databaseBuilder(context, StationDatabase::class.java, StationDatabase.name)
            .fallbackToDestructiveMigration()
            .build()
    }
}
