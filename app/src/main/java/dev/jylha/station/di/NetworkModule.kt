package dev.jylha.station.di

import dev.jylha.station.BuildConfig
import dev.jylha.station.data.stations.network.StationService
import dev.jylha.station.data.trains.network.TrainService
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        val gson = GsonBuilder().create()

        val logging = HttpLoggingInterceptor()
        logging.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
        else HttpLoggingInterceptor.Level.NONE

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
    fun provideStationService(retrofit: Retrofit): StationService {
        return retrofit.create(StationService::class.java)
    }

    @Singleton
    @Provides
    fun provideTrainService(retrofit: Retrofit): TrainService {
        return retrofit.create(TrainService::class.java)
    }
}
