package com.opsc.youthlinksa.data.model

// These classes mirror the JSON shapes returned by the YouthLink SA REST API.
// Gson (via Retrofit) automatically converts JSON into these objects.

data class RegisterRequest(
    val first_name: String,
    val surname: String,
    val email: String,
    val password: String
)

data class RegisterResponse(
    val message: String,
    val user_id: Int
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: UserProfile
)

data class UserProfile(
    val user_id: Int,
    val first_name: String,
    val surname: String,
    val email: String,
    val language: String?,
    val location: String? = null,
    val education_level: String? = null,
    val experience: String? = null,
    val notification_preference: Boolean? = null
)

data class UpdateProfileRequest(
    val language: String? = null,
    val location: String? = null,
    val education_level: String? = null,
    val experience: String? = null,
    val notification_preference: Boolean? = null
)

data class Opportunity(
    val opportunity_id: Int,
    val opportunity_title: String,
    val organisation: String?,
    val description: String?,
    val opportunity_type: String?,
    val field: String?,
    val location: String?,
    val education_requirement: String?,
    val experience_requirement: String?,
    val closing_date: String?,
    val application_link: String?
)

data class Career(
    val career_id: Int,
    val career_title: String,
    val field: String?,
    val description: String?,
    val education_required: String?,
    val experience_required: String?
)

data class Funding(
    val funding_id: Int,
    val funding_title: String,
    val provider: String?,
    val description: String?,
    val amount: String?,
    val eligibility: String?,
    val closing_date: String?,
    val application_link: String?
)

data class Event(
    val event_id: Int,
    val event_title: String,
    val description: String?,
    val event_type: String?,
    val location: String?,
    val start_date: String?,
    val end_date: String?
)

data class ApiMessage(
    val message: String? = null,
    val error: String? = null
)
