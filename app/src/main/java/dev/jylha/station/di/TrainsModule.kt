package dev.jylha.station.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.jylha.station.data.trains.StoreBackedTrainRepository
import dev.jylha.station.domain.TrainRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TrainsModule {

    @Singleton
    @Binds
    abstract fun bind(repository: StoreBackedTrainRepository): TrainRepository
}
