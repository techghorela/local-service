package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthRepository
import com.example.data.local.UserPreferences
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userPreferences: UserPreferences,
    private val authRepository: AuthRepository? = null
) : ViewModel() {

    val userProfile: StateFlow<UserProfile> = userPreferences.userProfileFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfile()
        )

    val isPartnerMode: StateFlow<Boolean> = userPreferences.isPartnerModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isLoggedIn: StateFlow<Boolean> = userPreferences.isLoggedInFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val userEmail: StateFlow<String> = userPreferences.userEmailFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun saveProfile(name: String, phone: String, address: String, block: String) {
        viewModelScope.launch {
            userPreferences.saveProfile(
                UserProfile(
                    name = name.trim(),
                    phone = phone.trim(),
                    address = address.trim(),
                    block = block
                )
            )
        }
    }

    fun setPartnerMode(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setPartnerMode(enabled)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository?.signOut() ?: userPreferences.clearAuth()
        }
    }
}

class ProfileViewModelFactory(
    private val userPreferences: UserPreferences,
    private val authRepository: AuthRepository? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(userPreferences, authRepository) as T
    }
}
