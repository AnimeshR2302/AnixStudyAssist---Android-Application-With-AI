package com.anix.android.anixstudyassist.feature.landing.domain.repo

import com.anix.android.anixstudyassist.feature.landing.domain.model.LandingModel

interface LandingRepository {
    suspend fun getLandingData(): List<LandingModel>
}
