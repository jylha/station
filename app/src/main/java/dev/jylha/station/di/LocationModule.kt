package dev.jylha.station.di

import dev.jylha.station.data.location.DefaultLocationService
import dev.jylha.station.data.location.LocationService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    @Singleton
    @Binds
    abstract fun bind(service: DefaultLocationService): LocationService
}
