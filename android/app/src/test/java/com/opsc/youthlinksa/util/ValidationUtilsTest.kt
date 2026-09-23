package com.opsc.youthlinksa.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

// Tests the validation rules that stop invalid registration/login input
// from ever reaching the API - this is the "main functionality" the
// assignment brief asks for automated testing of, since it's what keeps
// the app from crashing or sending bad requests on invalid user input.
class ValidationUtilsTest {

    // --- Email validation ---

    @Test
    fun `valid email addresses are accepted`() {
        assertTrue(ValidationUtils.isValidEmail("student@example.com"))
        assertTrue(ValidationUtils.isValidEmail("chloe.langenhoven@varsitycollege.co.za"))
        assertTrue(ValidationUtils.isValidEmail("first+tag@sub.domain.org"))
    }

    @Test
    fun `emails missing an at symbol are rejected`() {
        assertFalse(ValidationUtils.isValidEmail("not-an-email.com"))
    }

    @Test
    fun `emails missing a domain are rejected`() {
        assertFalse(ValidationUtils.isValidEmail("someone@"))
    }

    @Test
    fun `emails missing a top-level domain are rejected`() {
        assertFalse(ValidationUtils.isValidEmail("someone@example"))
    }

    @Test
    fun `blank email is rejected`() {
        assertFalse(ValidationUtils.isValidEmail(""))
        assertFalse(ValidationUtils.isValidEmail("   "))
    }

    // --- Password validation ---

    @Test
    fun `password with 6 or more characters is accepted`() {
        assertTrue(ValidationUtils.isValidPassword("abc123"))
        assertTrue(ValidationUtils.isValidPassword("aVeryLongPassword"))
    }

    @Test
    fun `password shorter than 6 characters is rejected`() {
        assertFalse(ValidationUtils.isValidPassword("abc12"))
        assertFalse(ValidationUtils.isValidPassword(""))
    }

    // --- Registration form validation ---

    @Test
    fun `valid registration details return no error`() {
        val error = ValidationUtils.validateRegistration(
            firstName = "Chloe",
            surname = "Langenhoven",
            email = "chloe@example.com",
            password = "password123"
        )
        assertNull(error)
    }

    @Test
    fun `registration with a blank field returns an error`() {
        val error = ValidationUtils.validateRegistration(
            firstName = "",
            surname = "Langenhoven",
            email = "chloe@example.com",
            password = "password123"
        )
        assertEquals("Please fill in all fields.", error)
    }

    @Test
    fun `registration with an invalid email returns an error`() {
        val error = ValidationUtils.validateRegistration(
            firstName = "Chloe",
            surname = "Langenhoven",
            email = "not-an-email",
            password = "password123"
        )
        assertEquals("Please enter a valid email address.", error)
    }

    @Test
    fun `registration with a short password returns an error`() {
        val error = ValidationUtils.validateRegistration(
            firstName = "Chloe",
            surname = "Langenhoven",
            email = "chloe@example.com",
            password = "123"
        )
        assertEquals("Password must be at least 6 characters.", error)
    }

    // --- Login form validation ---

    @Test
    fun `valid login details return no error`() {
        val error = ValidationUtils.validateLogin("chloe@example.com", "password123")
        assertNull(error)
    }

    @Test
    fun `login with a blank password returns an error`() {
        val error = ValidationUtils.validateLogin("chloe@example.com", "")
        assertEquals("Please enter your email and password.", error)
    }
}
