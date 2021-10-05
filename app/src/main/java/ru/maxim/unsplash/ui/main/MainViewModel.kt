package ru.maxim.unsplash.ui.main

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.maxim.unsplash.R
import ru.maxim.unsplash.model.Photo
import ru.maxim.unsplash.repository.remote.RetrofitClient

class MainViewModel : ViewModel() {
    private val photoService = RetrofitClient.photoService
    private var currentOrderType = PhotosOrderType.Latest.queryParam
    private val _photos = MutableLiveData<ArrayList<Photo>>(arrayListOf())
    val photos: LiveData<ArrayList<Photo>> = _photos
    var currentPage = 1
        private set
    var errorMessage = MutableLiveData<Int?>(null)
    var isInitialLoading = MutableLiveData(false)
    var isNextPageLoading = MutableLiveData(false)

    fun loadNextPage() {
        // Load 30 elements for first page
        val pageSize = if (currentPage == 1) {
            isInitialLoading.postValue(true)
            30
        } else {
            isNextPageLoading.postValue(true)
            10
        }

        viewModelScope.launch(Dispatchers.IO) {
            val response = photoService.getAllPaginated(currentPage++, pageSize, currentOrderType)
            isInitialLoading.postValue(false)
            isNextPageLoading.postValue(false)
            if (response.isSuccessful && response.body() != null) {
                _photos.postValue(_photos.value?.apply { addAll(response.body()!!) })
            } else {
                val error = when(response.code()) {
                    401 -> {
                        // TODO: update auth credentials
                        R.string.unauthorized_error
                    }
                    408 -> R.string.timeout_error
                    in 400..499 -> R.string.common_error
                    in 500..599 -> R.string.server_error
                    else -> null
                }
                errorMessage.postValue(error)
            }
        }
    }

    fun setLike(photoId: String, itemPosition: Int) {

    }

    fun addToCollection(photoId: String) {

    }

    fun download(photoId: String) {

    }

    fun shareCollection(collectionId: Long) {

    }
}

enum class PhotosOrderType(val queryParam: String) {
    Latest("latest"),
    Oldest("oldest"),
    Popular("popular")
}

enum class PhotosSearchOrderType(val queryParam: String) {
    Relevant("relevant"),
    Latest("latest")
}