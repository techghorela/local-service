package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.HisarBlock
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class UserPreferences(private val context: Context) {

    companion object {
        val KEY_NAME = stringPreferencesKey("user_name")
        val KEY_PHONE = stringPreferencesKey("user_phone")
        val KEY_ADDRESS = stringPreferencesKey("user_address")
        val KEY_BLOCK = stringPreferencesKey("user_block")
        val KEY_EMAIL = stringPreferencesKey("user_email")
        val KEY_UID = stringPreferencesKey("user_uid")
        val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val KEY_PARTNER_MODE = booleanPreferencesKey("partner_mode_enabled")
        val KEY_PARTNER_ID = stringPreferencesKey("partner_id")
    }

    val userProfileFlow: Flow<UserProfile> = context.dataStore.data.map { preferences ->
        UserProfile(
            name = preferences[KEY_NAME] ?: "",
            phone = preferences[KEY_PHONE] ?: "",
            address = preferences[KEY_ADDRESS] ?: "",
            block = preferences[KEY_BLOCK] ?: HisarBlock.HISAR_1.displayName
        )
    }

    val selectedBlockFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_BLOCK] ?: HisarBlock.HISAR_1.displayName
    }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_IS_LOGGED_IN] ?: false
    }

    val userEmailFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_EMAIL] ?: ""
    }

    val partnerModeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_PARTNER_MODE] ?: false
    }

    val isPartnerModeFlow: Flow<Boolean> get() = partnerModeFlow

    val partnerIdFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_PARTNER_ID] ?: "partner_hisar_01"
    }

    suspend fun saveProfile(profile: UserProfile) {
        context.dataStore.edit { preferences ->
            preferences[KEY_NAME] = profile.name
            preferences[KEY_PHONE] = profile.phone
            preferences[KEY_ADDRESS] = profile.address
            preferences[KEY_BLOCK] = profile.block
        }
    }

    suspend fun saveAuthState(isLoggedIn: Boolean, uid: String?, email: String?, displayName: String?) {
        context.dataStore.edit { preferences ->
            preferences[KEY_IS_LOGGED_IN] = isLoggedIn
            preferences[KEY_UID] = uid ?: ""
            preferences[KEY_EMAIL] = email ?: ""
            if (!displayName.isNullOrBlank()) {
                preferences[KEY_NAME] = displayName
            }
        }
    }

    suspend fun setPartnerMode(enabled: Boolean, partnerId: String = "partner_hisar_01") {
        context.dataStore.edit { preferences ->
            preferences[KEY_PARTNER_MODE] = enabled
            preferences[KEY_PARTNER_ID] = partnerId
        }
    }

    suspend fun clearAuth() {
        context.dataStore.edit { preferences ->
            preferences[KEY_IS_LOGGED_IN] = false
            preferences[KEY_UID] = ""
            preferences[KEY_EMAIL] = ""
        }
    }

    suspend fun setSelectedBlock(block: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_BLOCK] = block
        }
    }
}
