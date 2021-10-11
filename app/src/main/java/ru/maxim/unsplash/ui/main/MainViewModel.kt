package ru.maxim.unsplash.ui.main

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.maxim.unsplash.R
import ru.maxim.unsplash.model.Photo
import ru.maxim.unsplash.model.PhotosCollection
import ru.maxim.unsplash.repository.remote.RetrofitClient
import ru.maxim.unsplash.ui.main.MainFragment.ListMode
import ru.maxim.unsplash.ui.main.items.*

class MainViewModel : ViewModel() {
    private val photoService = RetrofitClient.photoService
    private val collectionService = RetrofitClient.collectionService

    private val _items = MutableLiveData<ArrayList<BaseMainListItem>>(arrayListOf())
    private val _errorMessage = MutableLiveData<@StringRes Int>(null)
    private val _isInitialLoading = MutableLiveData(false)
    private val _isNextPageLoading = MutableLiveData(false)
    private val _isRefreshing = MutableLiveData(false)
    val items: LiveData<ArrayList<BaseMainListItem>> = _items
    val errorMessage: LiveData<Int> = _errorMessage
    val isInitialLoading: LiveData<Boolean> = _isInitialLoading
    val isNextPageLoading: LiveData<Boolean> = _isNextPageLoading
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private var currentPage = 1
    var currentListMode = ListMode.Editorial
    private var currentPhotosOrderType = PhotosOrderType.Latest.queryParam

    fun loadNextPage() {
        // Load 30 elements for first page
        val pageSize = if (currentPage == 1) {
            // set isInitialLoading true if this loading is not called for refresh
            _isInitialLoading.postValue(_isRefreshing.value?.not())
            initialLoadSize
        } else {
            _isNextPageLoading.postValue(true)
            pageSize
        }

        viewModelScope.launch(Dispatchers.IO) {
            val response = when (currentListMode) {
                ListMode.Editorial ->
                    photoService.getAllPaginated(currentPage, pageSize, currentPhotosOrderType)
                ListMode.Collections ->
                    collectionService.getAllPaginated(currentPage, pageSize)
                ListMode.Following ->
                    photoService.getAllPaginated(currentPage, pageSize, currentPhotosOrderType)
            }
            _isInitialLoading.postValue(false)
            _isNextPageLoading.postValue(false)
            _isRefreshing.postValue(false)
            if (response.isSuccessful && response.body() != null) {
                _items.postValue(_items.value?.apply {
                    addAll(response.body()!!.map {
                        when (it) {
                            is Photo -> PhotoItem.fromPhoto(it)
                            is PhotosCollection -> PhotosCollectionItem.fromCollection(it)
                            else -> throw IllegalArgumentException("Unknown item type")
                        }
                    })
                })
                if (_items.value.isNullOrEmpty()) {
                    _items.postValue(_items.value?.apply { add(EmptyListItem) })
                }
                currentPage++
            } else {
                val error = when (response.code()) {
                    401 -> {
                        // TODO: update auth credentials
                        R.string.unauthorized_error
                    }
                    408 -> R.string.timeout_error
                    in 400..499 -> R.string.common_loading_error
                    in 500..599 -> R.string.server_error
                    else -> null
                }
                if (_items.value.isNullOrEmpty()) {
                    _items.postValue(_items.value?.apply { add(LoadingErrorItem(_errorMessage.value)) })
                }
                error?.let { _errorMessage.postValue(it) }
            }
        }
    }

    fun refresh() {
        _isRefreshing.postValue(true)
        currentPage = 1
        _items.value?.clear()
        loadNextPage()
    }

    fun setLike(photoId: String, itemPosition: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val photoItem = (_items.value?.get(itemPosition) as? PhotoItem) ?: return@launch
            val response =
                if (photoItem.likedByUser) photoService.removeLike(photoId)
                else photoService.setLike(photoId)

            val like = response.body()
            if (response.isSuccessful && like != null) {
                val newItem = (_items.value!![itemPosition] as PhotoItem).apply {
                    likesCount = like.photo.likes
                    likedByUser = like.photo.likedByUser
                }
                _items.value!![itemPosition] = newItem
                _items.postValue(_items.value)
            }
        }
    }

    fun addToCollection(photoId: String) {

    }

    fun download(photoId: String) {

    }

    fun shareCollection(collectionId: String) {

    }

    companion object {
        private const val pageSize = 10
        private const val initialLoadSize = 30
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