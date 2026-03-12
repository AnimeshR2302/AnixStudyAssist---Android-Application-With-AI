package com.anix.android.anixstudyassist.feature.auth.di

import com.anix.android.anixstudyassist.feature.auth.data.impl.AuthRepositoryImpl
import com.anix.android.anixstudyassist.feature.auth.data.remote.api.AuthApiService
import com.anix.android.anixstudyassist.feature.auth.domain.repo.AuthRepository
import com.anix.android.anixstudyassist.feature.auth.domain.usecase.LoginUseCase
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
