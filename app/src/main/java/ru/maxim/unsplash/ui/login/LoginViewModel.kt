package ru.maxim.unsplash.ui.login

import android.util.Patterns
import androidx.lifecycle.*
import ru.maxim.unsplash.util.SingleLiveEvent

class LoginViewModel(private val state: SavedStateHandle) : ViewModel() {
    // Two-way data binding
    val email = state.getLiveData<String>("email")
    val password = state.getLiveData<String>("password")

    private val _errorMessage = state.getLiveData<String>("error_message")
    val errorMessage: LiveData<String> = _errorMessage
    private val _isButtonEnabled = MediatorLiveData<Boolean>()
    val isButtonEnabled: LiveData<Boolean> = _isButtonEnabled
    private val _isLoginAccepted = SingleLiveEvent<Boolean>()
    val isLoginAccepted: SingleLiveEvent<Boolean> = _isLoginAccepted

    init {
        _isButtonEnabled.apply {
            addSource(email) { _isButtonEnabled.value = validateEmail() && validatePassword() }
            addSource(password) { _isButtonEnabled.value = validateEmail() && validatePassword() }
        }
    }

    private fun validateEmail(): Boolean =
        !email.value.isNullOrBlank() && Patterns.EMAIL_ADDRESS.matcher(email.value!!).matches()

    private fun validatePassword(): Boolean =
        !password.value.isNullOrBlank() && password.value?.length!! > 7

    fun login() {
        // Network request to login endpoint

        // Set flag to indicate successful login (now its just fields validation)
        _isLoginAccepted.value = _isButtonEnabled.value
    }
}