package com.anix.android.anixstudyassist.feature.landing.di

import com.anix.android.anixstudyassist.feature.landing.data.impl.LandingRepositoryImpl
import com.anix.android.anixstudyassist.feature.landing.data.remote.api.LandingApiService
import com.anix.android.anixstudyassist.feature.landing.domain.repo.LandingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object LandingModule {

    @Provides
    fun provideLandingApiService(retrofit: Retrofit): LandingApiService {
        return retrofit.create(LandingApiService::class.java)
    }

    @Provides
    fun provideLandingRepository(): LandingRepository {
        return LandingRepositoryImpl()
    }
}
