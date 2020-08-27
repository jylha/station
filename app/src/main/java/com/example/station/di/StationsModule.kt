package com.example.station.di

import com.example.station.data.stations.network.StationsService
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ActivityRetainedComponent::class)
class StationsModule {

    @Provides
    fun provideStationsService(): StationsService {
        val gson = GsonBuilder().create()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://rata.digitraffic.fi/api/v1/metadata/")
            .addConverterFactory(GsonConverterFactory.create(gson))

        return retrofit.build().create(StationsService::class.java)
    }
}
