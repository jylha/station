package com.example.station.di

import com.example.station.data.location.DefaultLocationService
import com.example.station.data.location.LocationService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class LocationModule {

    @Singleton
    @Binds
    abstract fun bind(service: DefaultLocationService): LocationService
}
