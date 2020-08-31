package com.example.station.di

import com.example.station.data.stations.network.StationsService
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        val gson = GsonBuilder().create()

        return Retrofit.Builder()
            .baseUrl("https://rata.digitraffic.fi/api/v1/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Singleton
    @Provides
    fun provideStationsService(retrofit: Retrofit): StationsService {
        return retrofit.create(StationsService::class.java)
    }
}