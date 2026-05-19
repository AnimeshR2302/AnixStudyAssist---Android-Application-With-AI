package com.anix.android.anixstudyassist.datahandler.di

import com.anix.android.anixstudyassist.datahandler.data.repository.StudyRepositoryImpl
import com.anix.android.anixstudyassist.datahandler.domain.repo.StudyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StudyModule {

    @Binds
    @Singleton
    abstract fun bindStudyRepository(
        studyRepositoryImpl: StudyRepositoryImpl
    ): StudyRepository
}
