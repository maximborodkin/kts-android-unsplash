package ru.maxim.unsplash.database

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

object PreferencesManager {
    private var preferences: SharedPreferences? = null

    private const val preferencesName = "application_preferences"
    private const val accessTokenKey = "access_token"
    private const val refreshTokenKey = "refresh_token"
    private const val isOnboardingDoneKey = "is_onboarding_done"

    var accessToken: String?
        get() = preferences?.getString(accessTokenKey, null)
        set(value) { preferences?.edit()?.putString(accessTokenKey, value)?.apply() }

    var refreshToken: String?
        get() = preferences?.getString(refreshTokenKey, null)
        set(value) { preferences?.edit()?.putString(refreshTokenKey, value)?.apply() }

    var isOnboardingDone: Boolean
        get() = preferences?.getBoolean(isOnboardingDoneKey, false) == true
        set(value) { preferences?.edit()?.putBoolean(isOnboardingDoneKey, value)?.apply() }

    fun init(context: Context) = with(context) {
        preferences = getSharedPreferences(preferencesName, MODE_PRIVATE)
    }

}