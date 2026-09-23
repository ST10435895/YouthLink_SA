package com.opsc.youthlinksa.util

// Pure Kotlin validation logic with no Android framework dependency.
// Kept separate from AuthViewModel specifically so it can be unit tested
// on the JVM without needing Robolectric or a mocked Android SDK - see
// ValidationUtilsTest for the actual test cases.
object ValidationUtils {

    // A practical (not fully RFC-5322-compliant) email check: something,
    // then @, then something, then a dot, then a top-level domain of at
    // least two letters. Good enough to catch the vast majority of typos
    // without rejecting real addresses.
    private val EMAIL_REGEX = Regex(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    )

    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && EMAIL_REGEX.matches(email.trim())
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    // Returns an error message if the registration fields are invalid,
    // or null if they're all fine.
    fun validateRegistration(
        firstName: String,
        surname: String,
        email: String,
        password: String
    ): String? {
        if (firstName.isBlank() || surname.isBlank() || email.isBlank() || password.isBlank()) {
            return "Please fill in all fields."
        }
        if (!isValidEmail(email)) {
            return "Please enter a valid email address."
        }
        if (!isValidPassword(password)) {
            return "Password must be at least 6 characters."
        }
        return null
    }

    // Returns an error message if the login fields are invalid,
    // or null if they're all fine.
    fun validateLogin(email: String, password: String): String? {
        if (email.isBlank() || password.isBlank()) {
            return "Please enter your email and password."
        }
        return null
    }
}
