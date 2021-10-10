package ru.maxim.unsplash.ui.photo_details

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.maxim.unsplash.R
import ru.maxim.unsplash.model.Photo
import ru.maxim.unsplash.repository.remote.RetrofitClient

class PhotoDetailsViewModel(application: Application, private val photoId: String) :
    AndroidViewModel(application) {

    private val photoService = RetrofitClient.photoService
    private val _error = MutableLiveData<@StringRes Int>()
    private val _photo = MutableLiveData<Photo>()
    val photo: LiveData<Photo> = _photo
    val error: LiveData<Int> = _error

    fun loadPhoto() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = photoService.getById(photoId)
            if (response.isSuccessful && response.body() != null) {
                _photo.postValue(response.body())
            } else {
                _error.value = when (response.code()) {
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
            }
        }
    }

    fun setLike() {
        if (_photo.value == null) return
        viewModelScope.launch(Dispatchers.IO) {
            val response =
                if (_photo.value!!.likedByUser) photoService.removeLike(photoId)
                else photoService.setLike(photoId)

            val like = response.body()
            if (response.isSuccessful && like != null) {
                _photo.postValue(_photo.value!!.apply {
                    likedByUser = like.photo.likedByUser
                    likes = like.photo.likes
                })
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