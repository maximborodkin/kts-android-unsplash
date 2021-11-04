package ru.maxim.unsplash.ui.profile

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.maxim.unsplash.R
import ru.maxim.unsplash.domain.model.User
import ru.maxim.unsplash.network.exception.*
import ru.maxim.unsplash.persistence.PreferencesManager
import ru.maxim.unsplash.repository.UserRepository
import ru.maxim.unsplash.ui.profile.ProfileViewModel.ProfileState.*
import ru.maxim.unsplash.util.Result

class ProfileViewModel private constructor(
    application: Application,
    private val userRepository: UserRepository,
    private val preferencesManager: PreferencesManager,
    private val username: String?
) : AndroidViewModel(application) {

    private val _profileState = MutableStateFlow<ProfileState>(Empty)
    val profileState = _profileState.asStateFlow()

    sealed class ProfileState {
        object Empty : ProfileState()
        object Refreshing : ProfileState()
        data class Success(val user: User) : ProfileState()
        data class Error(val cache: User?, @StringRes val messageRes: Int) : ProfileState()
    }

    fun loadUser() = viewModelScope.launch {
        val userRequest =
            if (username.isNullOrBlank())
                userRepository.getCurrentUser()
            else
                userRepository.getByUsername(username)

        userRequest.collect { result ->
            when (result) {
                is Result.Loading -> {
                    result.data?.let { _profileState.emit(Success(it)) }
                }
                is Result.Success -> {
                    result.data?.let {
                        _profileState.emit(Success(it))

                        if (username.isNullOrBlank()) {
                            preferencesManager.currentUserUsername = result.data.username
                        }
                    }
                }
                is Result.Error -> {
                    val errorMessage = when (result.exception) {
                        is UnauthorizedException -> R.string.unauthorized_error
                        is ForbiddenException -> R.string.forbidden_user
                        is NotFoundException -> R.string.user_not_found
                        is TimeoutException -> R.string.timeout_error
                        is ServerErrorException -> R.string.server_error
                        is NoConnectionException -> R.string.no_internet
                        else -> R.string.common_loading_error
                    }
                    _profileState.emit(Error(result.data, errorMessage))
                }
            }
        }
    }

    fun refresh() = viewModelScope.launch {
        _profileState.emit(Refreshing)
        loadUser()
    }

    @Suppress("UNCHECKED_CAST")
    class ProfileViewModelFactory(
        private val application: Application,
        private val userRepository: UserRepository,
        private val preferencesManager: PreferencesManager,
        private val username: String?
    ) : ViewModelProvider.AndroidViewModelFactory(application) {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                return ProfileViewModel(
                    application,
                    userRepository,
                    preferencesManager,
                    username
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class ${modelClass.simpleName}")
        }
    }
}