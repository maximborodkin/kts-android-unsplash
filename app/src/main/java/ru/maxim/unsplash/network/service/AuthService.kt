package ru.maxim.unsplash.network.service

import android.net.Uri
import net.openid.appauth.*
import ru.maxim.unsplash.database.PreferencesManager
import ru.maxim.unsplash.network.AuthConfig

object AuthService {
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
        .setIdTokenHint(PreferencesManager.accessToken)
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
                PreferencesManager.accessToken = response.accessToken
                PreferencesManager.refreshToken = response.refreshToken
                onComplete()
            } else {
                onError(exception?.localizedMessage)
            }
        }
    }
}