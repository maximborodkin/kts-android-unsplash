package ru.maxim.unsplash.ui.photo_details

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.maxim.unsplash.R
import ru.maxim.unsplash.domain.model.Photo
import ru.maxim.unsplash.network.exception.*
import ru.maxim.unsplash.repository.PhotoRepository
import ru.maxim.unsplash.ui.photo_details.PhotoDetailsViewModel.PhotoDetailsState.*
import ru.maxim.unsplash.util.Result

class PhotoDetailsViewModel private constructor(
    application: Application,
    private val photoRepository: PhotoRepository,
    private val photoId: String
) : AndroidViewModel(application) {

    private val _photoDetailsState = MutableStateFlow<PhotoDetailsState>(Empty)
    val photoDetailsState: StateFlow<PhotoDetailsState> = _photoDetailsState.asStateFlow()

    sealed class PhotoDetailsState {
        object Empty : PhotoDetailsState()
        object Refreshing : PhotoDetailsState()
        data class Success(val photo: Photo) : PhotoDetailsState()
        data class Error(@StringRes val messageRes: Int, val cache: Photo?) : PhotoDetailsState()
    }

    fun loadPhoto() = viewModelScope.launch {
        photoRepository.getById(photoId).collect { result ->
            when (result) {
                is Result.Loading -> {
                    result.data?.let { _photoDetailsState.emit(Success(it)) }
                }

                is Result.Success -> {
                    result.data?.let { _photoDetailsState.emit(Success(it)) }
                }

                is Result.Error -> {
                    val errorMessage = when (result.exception) {
                        is UnauthorizedException -> R.string.unauthorized_error
                        is ForbiddenException -> R.string.forbidden_photo
                        is NotFoundException -> R.string.photo_not_found
                        is TimeoutException -> R.string.timeout_error
                        is ServerErrorException -> R.string.server_error
                        is NoConnectionException -> R.string.no_internet
                        else -> R.string.common_loading_error
                    }
                    _photoDetailsState.emit(Error(errorMessage, result.data))
                }
            }
        }
    }

    fun refresh() = viewModelScope.launch {
        _photoDetailsState.emit(Refreshing)
        loadPhoto()
    }

    fun setLike() = viewModelScope.launch {

    }

    @Suppress("UNCHECKED_CAST")
    class PhotoDetailsViewModelFactory(
        private val application: Application,
        private val photoRepository: PhotoRepository,
        private val photoId: String
    ) : ViewModelProvider.AndroidViewModelFactory(application) {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PhotoDetailsViewModel::class.java)) {
                return PhotoDetailsViewModel(application, photoRepository, photoId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class ${modelClass.simpleName}")
        }
    }
}