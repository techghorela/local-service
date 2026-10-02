package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthRepository
import com.example.data.auth.AuthResult
import com.example.data.local.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isSuccess: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val isLoggedIn = userPreferences.isLoggedInFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val userEmail = userPreferences.userEmailFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val userProfile = userPreferences.userProfileFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Please enter both email and password")
            return
        }
        _uiState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            when (val result = authRepository.signIn(email.trim(), pass)) {
                is AuthResult.Success -> {
                    _uiState.value = AuthUiState(
                        isSuccess = true,
                        successMessage = "Welcome back, ${result.displayName}!"
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = AuthUiState(errorMessage = result.message)
                }
            }
        }
    }

    fun signUp(email: String, pass: String, name: String) {
        if (email.isBlank() || pass.isBlank() || name.isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Please fill in all fields")
            return
        }
        if (pass.length < 6) {
            _uiState.value = AuthUiState(errorMessage = "Password must be at least 6 characters")
            return
        }
        _uiState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            when (val result = authRepository.signUp(email.trim(), pass, name.trim())) {
                is AuthResult.Success -> {
                    _uiState.value = AuthUiState(
                        isSuccess = true,
                        successMessage = "Account created successfully!"
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = AuthUiState(errorMessage = result.message)
                }
            }
        }
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Please enter your email to reset password")
            return
        }
        _uiState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            when (val result = authRepository.sendPasswordReset(email.trim())) {
                is AuthResult.Success -> {
                    _uiState.value = AuthUiState(
                        successMessage = "Password reset email sent to $email"
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = AuthUiState(errorMessage = result.message)
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.value = AuthUiState()
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}
