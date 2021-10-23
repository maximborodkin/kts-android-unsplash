package ru.maxim.unsplash.network.service

import android.net.Uri
import net.openid.appauth.*
import ru.maxim.unsplash.database.PreferencesManager
import ru.maxim.unsplash.network.AuthConfig

class AuthService(
    private val preferencesManager: PreferencesManager
) {
    private val clientAuthentication = ClientSecretPost(AuthConfig.clientSecret)
    private val authorizationService = AuthorizationServiceConfiguration(
        Uri.parse(AuthConfig.authUri),
        Uri.parse(AuthConfig.tokenUri),
        null,
        Uri.parse(AuthConfig.logoutUri)
    )

    val authRequest = AuthorizationRequest.Builder(
        authorizationService,
        AuthConfig.clientId,
        AuthConfig.responseType,
        Uri.parse(AuthConfig.authCallback)
    ).setScope(AuthConfig.scope)
        .setCodeVerifier(null)
        .build()

    val logoutRequest = EndSessionRequest.Builder(
        authorizationService
    )
        .setIdTokenHint(preferencesManager.accessToken)
        .setPostLogoutRedirectUri(Uri.parse(AuthConfig.logoutCallback))
        .build()

    fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ) {
        authService.performTokenRequest(tokenRequest, clientAuthentication) { response, exception ->
            if (response != null && !response.accessToken.isNullOrBlank()) {
                preferencesManager.accessToken = response.accessToken
                preferencesManager.refreshToken = response.refreshToken
                onComplete()
            } else {
                onError(exception?.localizedMessage)
            }
        }
    }
}