package ru.maxim.unsplash.ui.login

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest
import ru.maxim.unsplash.network.service.AuthService
import ru.maxim.unsplash.repository.UserRepository
import ru.maxim.unsplash.ui.login.LoginViewModel.LoginState.*
import ru.maxim.unsplash.util.NetworkUtils
import ru.maxim.unsplash.util.Result

class LoginViewModel private constructor(
    application: Application,
    private val authService: AuthService,
    private val userRepository: UserRepository,
    private val authorizationService: AuthorizationService,
    private val networkUtils: NetworkUtils
) : AndroidViewModel(application) {

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
        networkUtils.networkStateFlow.collect {
            if (it) {
                val authIntent = authorizationService
                    .getAuthorizationRequestIntent(authService.authRequest)
                launch(Main) { _loginState.emit(Process(authIntent)) }
            }
        }
    }

    fun onTokenRequestReceived(tokenRequest: TokenRequest) {
        authService.performTokenRequest(
            authorizationService,
            tokenRequest,
            onComplete = {
                resolveCurrentUser()
                viewModelScope.launch(Main) { _loginState.emit(Success) }
            },
            onError = { viewModelScope.launch(Main) { _loginState.emit(Error(it)) } }
        )
    }

    // Load current user to update username field in SharedPreferences
    private fun resolveCurrentUser() = viewModelScope.launch {
        userRepository.getCurrentUser().filter { result -> result is Result.Success }.first()
    }

    fun onAuthFailed(exception: AuthorizationException) = viewModelScope.launch(Main) {
        _loginState.emit(Error(exception.localizedMessage))
    }

    @Suppress("UNCHECKED_CAST")
    class LoginViewModelFactory(
        private val application: Application,
        private val authService: AuthService,
        private val userRepository: UserRepository,
        private val authorizationService: AuthorizationService,
        private val networkUtils: NetworkUtils
    ) : ViewModelProvider.AndroidViewModelFactory(application) {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                return LoginViewModel(
                    application,
                    authService,
                    userRepository,
                    authorizationService,
                    networkUtils
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class ${modelClass.simpleName}")
        }
    }
}