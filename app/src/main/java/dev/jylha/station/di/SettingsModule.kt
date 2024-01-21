package dev.jylha.station.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.jylha.station.data.settings.DefaultSettingsRepository
import dev.jylha.station.domain.SettingsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {

    @Singleton
    @Binds
    abstract fun bind(repository: DefaultSettingsRepository): SettingsRepository
}
