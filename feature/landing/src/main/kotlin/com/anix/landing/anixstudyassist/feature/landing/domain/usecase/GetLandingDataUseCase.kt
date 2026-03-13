package com.anix.landing.anixstudyassist.feature.landing.domain.usecase

import com.anix.landing.anixstudyassist.feature.landing.domain.repo.LandingRepository

class GetLandingDataUseCase(private val repository: LandingRepository) {
    suspend operator fun invoke() = repository.getLandingData()
}
