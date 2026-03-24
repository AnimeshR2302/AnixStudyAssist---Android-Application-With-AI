package com.anix.android.anixstudyassist.entry.di

import com.anix.android.anixstudyassist.entry.data.impl.AuthRepositoryImpl
import com.anix.android.anixstudyassist.entry.data.remote.api.AuthApiService
import com.anix.android.anixstudyassist.entry.domain.repo.AuthRepository
import com.anix.android.anixstudyassist.entry.domain.usecase.LoginUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    fun provideAuthRepository(
        authApiService: AuthApiService
    ): AuthRepository {
        return AuthRepositoryImpl(authApiService)
    }

    @Provides
    fun provideLoginUseCase(repository: AuthRepository): LoginUseCase {
        return LoginUseCase(repository)
    }
}
