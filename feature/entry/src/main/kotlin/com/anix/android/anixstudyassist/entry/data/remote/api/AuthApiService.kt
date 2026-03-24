package com.anix.android.anixstudyassist.entry.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AuthApiService {

    @GET("auth/login")
    suspend fun login(
        @Query("username") userId: String,
        @Query("password") password: String
    ): Response<Unit>
}
