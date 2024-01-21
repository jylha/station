package dev.jylha.station.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.jylha.station.data.stations.StoreBackedStationRepository
import dev.jylha.station.domain.StationRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StationsModule {

    @Singleton
    @Binds
    abstract fun bind(repository: StoreBackedStationRepository): StationRepository
}

