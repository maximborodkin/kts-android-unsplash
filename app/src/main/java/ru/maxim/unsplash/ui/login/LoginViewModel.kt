package ru.maxim.unsplash.ui.login

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest
import ru.maxim.unsplash.repository.remote.service.AuthService
import ru.maxim.unsplash.util.SingleLiveEvent

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val authService = AuthService()
    private val authorizationService = AuthorizationService(application)

    private val _openAuthPageEvent = SingleLiveEvent<Intent>()
    private val _isAuthSuccess = SingleLiveEvent<Unit>()
    private val _isAuthInProgress = MutableLiveData(false)
    private val _error = MutableLiveData<String?>(null)

    val openAuthPageEvent: LiveData<Intent> = _openAuthPageEvent
    val isAuthSuccess: LiveData<Unit> = _isAuthSuccess
    val isAuthInProgress: LiveData<Boolean> = _isAuthInProgress
    val error: LiveData<String?> = _error

    // Open OAuth2 authentication page in a browser tab
    fun startLoginPage() {
        _isAuthInProgress.postValue(true)
        val authIntent = authorizationService.getAuthorizationRequestIntent(authService.authRequest)
        _openAuthPageEvent.postValue(authIntent)
    }

    fun onTokenRequestReceived(tokenRequest: TokenRequest) {
        _isAuthInProgress.postValue(false)
        authService.performTokenRequest(
            authorizationService,
            tokenRequest,
            onComplete = { _isAuthSuccess.postValue(Unit) },
            onError = { message -> _error.postValue(message) }
        )
    }

    fun onAuthFailed(exception: AuthorizationException) {
        _isAuthInProgress.postValue(false)
        _error.postValue(exception.localizedMessage)
    }
}