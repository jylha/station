package com.example.station.di

import com.example.station.data.stations.network.StationsService
import com.example.station.data.trains.network.TrainService
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BASIC

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .client(client)
            .baseUrl("https://rata.digitraffic.fi/api/v1/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Singleton
    @Provides
    fun provideStationsService(retrofit: Retrofit): StationsService {
        return retrofit.create(StationsService::class.java)
    }

    @Singleton
    @Provides
    fun provideTrainService(retrofit: Retrofit): TrainService {
        return retrofit.create(TrainService::class.java)
    }
}
