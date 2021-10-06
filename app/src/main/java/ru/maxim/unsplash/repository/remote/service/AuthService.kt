package ru.maxim.unsplash.repository.remote.service

import android.net.Uri
import net.openid.appauth.*
import ru.maxim.unsplash.repository.remote.AuthConfig

class AuthService {
    private val clientAuthentication = ClientSecretPost(AuthConfig.clientSecret)

    val authRequest = AuthorizationRequest.Builder(
        AuthorizationServiceConfiguration(
            Uri.parse(AuthConfig.authUri),
            Uri.parse(AuthConfig.tokenUri)
        ),
        AuthConfig.clientId,
        AuthConfig.responseType,
        Uri.parse(AuthConfig.authCallback)
    ).setScope(AuthConfig.scope)
        .setCodeVerifier(null)
        .build()

    fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ) {
        authService.performTokenRequest(tokenRequest, clientAuthentication) { response, exception ->
            if (response != null && response.accessToken.orEmpty().isNotBlank()) {
                onComplete()
                AuthConfig.accessToken = response.accessToken
            } else {
                onError(exception?.localizedMessage)
            }
        }
    }
}