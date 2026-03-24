package com.anix.android.anixstudyassist.aikit.di

import android.content.Context
import com.anix.android.anixstudyassist.aikit.data.repository.OnDeviceAiRepositoryImpl
import com.anix.android.anixstudyassist.aikit.domain.repository.OnDeviceAiRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiKitModule {

    @Provides
    @Singleton
    fun provideOnDeviceAiRepository(
        @ApplicationContext context: Context
    ): OnDeviceAiRepository {
        return OnDeviceAiRepositoryImpl(context)
    }
}
