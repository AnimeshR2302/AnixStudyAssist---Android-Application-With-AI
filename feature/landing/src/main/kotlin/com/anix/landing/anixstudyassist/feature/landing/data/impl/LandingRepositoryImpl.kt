package com.anix.landing.anixstudyassist.feature.landing.data.impl

import com.anix.landing.anixstudyassist.feature.landing.domain.model.LandingModel
import com.anix.landing.anixstudyassist.feature.landing.domain.repo.LandingRepository
import javax.inject.Inject

class LandingRepositoryImpl @Inject constructor() : LandingRepository {
    override suspend fun getLandingData(): List<LandingModel> {
        return listOf(
            LandingModel("1", "Class 1"),
            LandingModel("2", "Class 2")
        )
    }
}
