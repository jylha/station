package dev.jylha.station.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.jylha.station.domain.DefaultGetLocationUseCase
import dev.jylha.station.domain.GetLocationUseCase
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Singleton
    @Binds
    abstract fun bindGetLocationUseCase(useCse: DefaultGetLocationUseCase): GetLocationUseCase
}

