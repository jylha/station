package com.example.station.di

import com.example.station.data.stations.StationRepository
import com.example.station.data.stations.StoreBackedStationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StationsModule {

    @Singleton
    @Binds
    abstract fun bind(repository: StoreBackedStationRepository): StationRepository
}

