package com.example.station.di

import com.example.station.data.settings.DefaultSettingsRepository
import com.example.station.data.settings.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
abstract class SettingsModule {

    @Binds
    abstract fun bindSettingsRepository(repository: DefaultSettingsRepository): SettingsRepository

}
