package ru.maxim.unsplash.persistence

import android.content.Context
import android.content.Context.MODE_PRIVATE

class PreferencesManager(context: Context) {
    private val preferences = context.getSharedPreferences(preferencesName, MODE_PRIVATE)

    var accessToken: String?
        get() = preferences.getString(accessTokenKey, null)
        set(value) { preferences.edit().putString(accessTokenKey, value).apply() }

    var refreshToken: String?
        get() = preferences.getString(refreshTokenKey, null)
        set(value) { preferences.edit().putString(refreshTokenKey, value).apply() }

    var isOnboardingDone: Boolean
        get() = preferences.getBoolean(isOnboardingDoneKey, false)
        set(value) { preferences.edit().putBoolean(isOnboardingDoneKey, value).apply() }

    var currentUserUsername: String?
        get() = preferences.getString(currentUserUsernameKey, null)
        set(value) { preferences.edit().putString(currentUserUsernameKey, value).apply() }

    companion object {
        private const val preferencesName = "application_preferences"
        private const val accessTokenKey = "access_token"
        private const val refreshTokenKey = "refresh_token"
        private const val isOnboardingDoneKey = "is_onboarding_done"
        private const val currentUserUsernameKey = "current_user_username"
    }
}