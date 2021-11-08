package com.example.mysocialandroidapp2.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class WallRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindWallRepository(impl: WallRepositoryImpl): WallRepository
}