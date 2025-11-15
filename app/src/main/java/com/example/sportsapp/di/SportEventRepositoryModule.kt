package com.example.sportsapp.di

import com.example.sportsapp.data.remote.dao.FirebaseDao
import com.example.sportsapp.data.repository.SportEventRepositoryImplementation
import com.example.sportsapp.domain.repository.SportEventRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SportEventRepositoryModule {

    @Provides
    @Singleton
    fun provideSportEventRepository(
        db: FirebaseDao
    ): SportEventRepository {
        return SportEventRepositoryImplementation(db)
    }

}