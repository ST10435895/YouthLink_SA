package com.opsc.youthlinksa.data.network

import com.opsc.youthlinksa.data.model.*
import retrofit2.Response
import retrofit2.http.*

// Defines every endpoint the app calls on the YouthLink SA REST API.
// Retrofit generates the actual networking code from this interface.
interface ApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/profile")
    suspend fun getProfile(): Response<UserProfile>

    @PUT("api/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ApiMessage>

    @GET("api/opportunities")
    suspend fun getOpportunities(
        @Query("keyword") keyword: String? = null,
        @Query("field") field: String? = null,
        @Query("location") location: String? = null,
        @Query("type") type: String? = null
    ): Response<List<Opportunity>>

    @GET("api/opportunities/{id}")
    suspend fun getOpportunity(@Path("id") id: Int): Response<Opportunity>

    @POST("api/opportunities/{id}/save")
    suspend fun saveOpportunity(@Path("id") id: Int): Response<ApiMessage>

    @DELETE("api/opportunities/{id}/save")
    suspend fun unsaveOpportunity(@Path("id") id: Int): Response<ApiMessage>

    @GET("api/opportunities/saved/me")
    suspend fun getSavedOpportunities(): Response<List<Opportunity>>

    @GET("api/careers")
    suspend fun getCareers(@Query("field") field: String? = null): Response<List<Career>>

    @GET("api/careers/{id}")
    suspend fun getCareer(@Path("id") id: Int): Response<Career>

    @GET("api/funding")
    suspend fun getFunding(): Response<List<Funding>>

    @GET("api/events")
    suspend fun getEvents(): Response<List<Event>>
}
