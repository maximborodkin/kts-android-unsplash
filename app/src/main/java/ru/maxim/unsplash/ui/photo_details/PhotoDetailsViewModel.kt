package ru.maxim.unsplash.ui.photo_details

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.maxim.unsplash.R
import ru.maxim.unsplash.model.Photo
import ru.maxim.unsplash.repository.remote.RetrofitClient
import ru.maxim.unsplash.ui.photo_details.PhotoDetailsViewModel.PhotoDetailsState.*

class PhotoDetailsViewModel private constructor(application: Application, private val photoId: String) :
    AndroidViewModel(application) {

    private val photoService = RetrofitClient.photoService
    private val _photoDetailsState = MutableStateFlow<PhotoDetailsState>(Empty)
    val photoDetailsState: StateFlow<PhotoDetailsState> = _photoDetailsState.asStateFlow()

    sealed class PhotoDetailsState {
        object Empty : PhotoDetailsState()
        object Refreshing : PhotoDetailsState()
        data class Success(val photo: Photo) : PhotoDetailsState()
        data class Error(@StringRes val messageRes: Int?) : PhotoDetailsState()
    }

    fun loadPhoto() = viewModelScope.launch(Dispatchers.IO) {
        val response = photoService.getById(photoId)
        val responseBody = response.body()
        if (response.isSuccessful && responseBody != null) {
            launch(Dispatchers.Main) { _photoDetailsState.emit(Success(responseBody)) }
        } else {
            val messageRes = when (response.code()) {
                401 -> {
                    //TODO: update auth credentials
                    R.string.unauthorized_error
                }
                403 -> R.string.not_enough_rights_photo
                404 -> R.string.photo_not_found
                408 -> R.string.timeout_error
                in 500..599 -> R.string.server_error
                else -> null
            }
            launch { _photoDetailsState.emit(Error(messageRes)) }
        }
    }

    fun refresh() = viewModelScope.launch {
        _photoDetailsState.emit(Refreshing)
        loadPhoto()
    }

    fun setLike() = viewModelScope.launch(Dispatchers.IO) {
        _photoDetailsState.collect { state ->
            // If current state is success, photo was loaded and available for update
            if (state is Success) {
                val response =
                    if (state.photo.likedByUser) photoService.removeLike(photoId)
                    else photoService.setLike(photoId)
                val like = response.body()
                if (response.isSuccessful && like != null) {
                    val updatedPhoto = state.photo.apply {
                        likedByUser = like.photo.likedByUser
                        likes = like.photo.likes
                    }
                    launch(Dispatchers.Main) { _photoDetailsState.emit(Success(updatedPhoto)) }
                }
            }
        }
    }

    class PhotoDetailsViewModelFactory(
        private val application: Application,
        private val photoId: String
    ) : ViewModelProvider.AndroidViewModelFactory(application) {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PhotoDetailsViewModel::class.java)) {
                return PhotoDetailsViewModel(application, photoId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class ${modelClass.simpleName}")
        }
    }
}