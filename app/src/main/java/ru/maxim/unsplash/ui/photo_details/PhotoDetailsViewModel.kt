package ru.maxim.unsplash.ui.photo_details

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.maxim.unsplash.R
import ru.maxim.unsplash.model.Photo
import ru.maxim.unsplash.repository.local.Database
import ru.maxim.unsplash.repository.local.model.DatabasePhoto.Companion.fromPhoto
import ru.maxim.unsplash.repository.local.model.DatabaseTag.Companion.fromTag
import ru.maxim.unsplash.repository.remote.RetrofitClient
import ru.maxim.unsplash.ui.photo_details.PhotoDetailsViewModel.PhotoDetailsState.*

class PhotoDetailsViewModel private constructor(
    application: Application,
    private val photoId: String
) : AndroidViewModel(application) {

    val database = Database.instance
    private val photoService = RetrofitClient.photoService
    private val _photoDetailsState = MutableStateFlow<PhotoDetailsState>(Empty)
    val photoDetailsState: StateFlow<PhotoDetailsState> = _photoDetailsState.asStateFlow()

    sealed class PhotoDetailsState {
        object Empty : PhotoDetailsState()
        object Refreshing : PhotoDetailsState()
        data class Success(val photo: Photo) : PhotoDetailsState()
        data class Error(@StringRes val messageRes: Int?) : PhotoDetailsState()
    }

    fun loadPhoto() = viewModelScope.launch(IO) {
        try {
            val response = photoService.getById(photoId)
            val responseBody = response.body()
            if (response.isSuccessful && responseBody != null) {
                cacheResponse(responseBody)
                withContext(Main) { _photoDetailsState.emit(Success(responseBody)) }
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
                withContext(Main) { _photoDetailsState.emit(Error(messageRes)) }
            }
        }catch (e: Exception) {
            try {
                loadCache()
            } catch (e: Exception) {
                withContext(Main) { _photoDetailsState.emit(Error(null)) }
            }
        }
    }

    private suspend fun cacheResponse(photo: Photo) = withContext(IO) {
        val photoDao = database.photoDao()
        val tagDao = database.tagDao()
        photoDao.insert(photo.fromPhoto())
        photo.tags?.let {
            tagDao.insertAll(it.map { tag -> tag.fromTag(photoId = photo.id) })
        }
    }

    private suspend fun loadCache() = withContext(IO) {
        val photoDao = database.photoDao()
        val photo = photoDao.getById(photoId)?.toPhoto()
        if (photo != null) {
            withContext(Main) { _photoDetailsState.emit(Success(photo)) }
        } else {
            withContext(Main) { _photoDetailsState.emit(Error(null)) }
        }
    }

    fun refresh() = viewModelScope.launch {
        withContext(Main) { _photoDetailsState.emit(Refreshing) }
        loadPhoto()
    }

    fun setLike() = viewModelScope.launch(IO) {
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
                    withContext(Main) { _photoDetailsState.emit(Success(updatedPhoto)) }
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
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