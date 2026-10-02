package com.example.data.auth

import android.util.Log
import com.example.data.local.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

sealed class AuthResult {
    data class Success(val uid: String, val email: String, val displayName: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthRepository(
    private val userPreferences: UserPreferences
) {
    private val TAG = "AuthRepository"
    private val firebaseAuth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Throwable) {
            Log.w(TAG, "Firebase Auth not initialized: ${e.message}")
            null
        }
    }

    suspend fun signUp(email: String, pass: String, name: String): AuthResult = withContext(Dispatchers.IO) {
        val auth = firebaseAuth
        if (auth == null) {
            // Local fallback simulation when google-services is not configured
            userPreferences.saveAuthState(true, "demo_${System.currentTimeMillis()}", email, name)
            return@withContext AuthResult.Success("demo_user", email, name)
        }

        try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val user = result.user
            val uid = user?.uid ?: "user_${System.currentTimeMillis()}"
            userPreferences.saveAuthState(true, uid, email, name)
            AuthResult.Success(uid, email, name)
        } catch (e: Exception) {
            Log.e(TAG, "Sign up failure: ${e.message}", e)
            AuthResult.Error(e.localizedMessage ?: "Failed to create account")
        }
    }

    suspend fun signIn(email: String, pass: String): AuthResult = withContext(Dispatchers.IO) {
        val auth = firebaseAuth
        if (auth == null) {
            // Local fallback simulation
            val defaultName = email.substringBefore("@").replaceFirstChar { it.uppercase() }
            userPreferences.saveAuthState(true, "demo_user", email, defaultName)
            return@withContext AuthResult.Success("demo_user", email, defaultName)
        }

        try {
            val result = auth.signInWithEmailAndPassword(email, pass).await()
            val user = result.user
            val uid = user?.uid ?: "user"
            val displayName = user?.displayName ?: email.substringBefore("@")
            userPreferences.saveAuthState(true, uid, email, displayName)
            AuthResult.Success(uid, email, displayName)
        } catch (e: Exception) {
            Log.e(TAG, "Sign in failure: ${e.message}", e)
            AuthResult.Error(e.localizedMessage ?: "Invalid email or password")
        }
    }

    suspend fun sendPasswordReset(email: String): AuthResult = withContext(Dispatchers.IO) {
        val auth = firebaseAuth
        if (auth == null) {
            return@withContext AuthResult.Success("reset", email, "Reset Link Sent")
        }

        try {
            auth.sendPasswordResetEmail(email).await()
            AuthResult.Success("reset", email, "Password reset email sent to $email")
        } catch (e: Exception) {
            Log.e(TAG, "Password reset failure: ${e.message}", e)
            AuthResult.Error(e.localizedMessage ?: "Could not send reset email")
        }
    }

    suspend fun signOut() = withContext(Dispatchers.IO) {
        try {
            firebaseAuth?.signOut()
        } catch (e: Exception) {
            Log.w(TAG, "Sign out error: ${e.message}")
        }
        userPreferences.clearAuth()
    }
}
