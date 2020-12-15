package com.example.station.di

import com.example.station.data.location.DefaultLocationService
import com.example.station.data.location.LocationService
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
