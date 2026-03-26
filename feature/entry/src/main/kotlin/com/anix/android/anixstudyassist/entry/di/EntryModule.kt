package com.anix.android.anixstudyassist.entry.di

import com.anix.android.anixstudyassist.entry.data.impl.EntryRepositoryImpl
import com.anix.android.anixstudyassist.entry.data.remote.api.EntryApiService
import com.anix.android.anixstudyassist.entry.domain.repo.EntryRepository
import com.anix.android.anixstudyassist.entry.domain.usecase.LoginUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object EntryModule {

    @Provides
    fun provideEntryApiService(retrofit: Retrofit): EntryApiService {
        return retrofit.create(EntryApiService::class.java)
    }

    @Provides
    fun provideEntryRepository(
        authApiService: EntryApiService
    ): EntryRepository {
        return EntryRepositoryImpl(authApiService)
    }

    @Provides
    fun provideLoginUseCase(repository: EntryRepository): LoginUseCase {
        return LoginUseCase(repository)
    }
}
