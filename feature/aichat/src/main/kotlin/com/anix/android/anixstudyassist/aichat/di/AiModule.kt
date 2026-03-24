package com.anix.android.anixstudyassist.aichat.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AiModule {
    // Core AI functionality is provided by AiKitModule from core:aikit
    // This module is reserved for chat-specific DI concerns
}
