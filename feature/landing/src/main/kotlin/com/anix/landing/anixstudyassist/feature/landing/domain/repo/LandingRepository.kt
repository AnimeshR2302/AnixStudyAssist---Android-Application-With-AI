package com.anix.landing.anixstudyassist.feature.landing.domain.repo

import com.anix.landing.anixstudyassist.feature.landing.domain.model.LandingModel

interface LandingRepository {
    suspend fun getLandingData(): List<LandingModel>
}
