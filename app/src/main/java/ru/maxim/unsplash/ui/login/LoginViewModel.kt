package ru.maxim.unsplash.ui.login

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest
import ru.maxim.unsplash.network.service.AuthService
import ru.maxim.unsplash.ui.login.LoginViewModel.LoginState.*
import ru.maxim.unsplash.util.NetworkUtils

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val authorizationService = AuthorizationService(application)

    sealed class LoginState {
        object Empty : LoginState()
        object Success : LoginState()
        data class Error(val message: String?) : LoginState()
        data class Process(val loginIntent: Intent) : LoginState()
    }

    private val _loginState = MutableStateFlow<LoginState>(Empty)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    // Open OAuth2 authentication page in a browser tab
    fun startLoginPage() = viewModelScope.launch {
        NetworkUtils.networkStateFlow.collect {
            if (it) {
                val authIntent = authorizationService
                    .getAuthorizationRequestIntent(AuthService.authRequest)
                launch(Main) { _loginState.emit(Process(authIntent)) }
            }
        }
    }

    fun logout() = viewModelScope.launch {
        val logoutIntent = authorizationService.getEndSessionRequestIntent(AuthService.logoutRequest)
        withContext(Main) { _loginState.emit(Process(logoutIntent)) }
    }

    fun onTokenRequestReceived(tokenRequest: TokenRequest) {
        AuthService.performTokenRequest(
            authorizationService,
            tokenRequest,
            onComplete = { viewModelScope.launch(Main) { _loginState.emit(Success) } },
            onError = { viewModelScope.launch(Main) { _loginState.emit(Error(it)) } }
        )
    }

    fun onAuthFailed(exception: AuthorizationException) = viewModelScope.launch(Main) {
        _loginState.emit(Error(exception.localizedMessage))
    }
}