package ru.maxim.unsplash.ui.main

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
import ru.maxim.unsplash.ui.main.items.BaseMainListItem
import ru.maxim.unsplash.ui.main.items.PhotoItem
import ru.maxim.unsplash.ui.main.items.PhotosCollectionItem

class MainViewModel : ViewModel() {
    private val photoService = RetrofitClient.photoService
    private val collectionService = RetrofitClient.collectionService

    var currentListMode = ListMode.Editorial

    private val _items = MutableLiveData<ArrayList<BaseMainListItem>>(arrayListOf())
    val items: LiveData<ArrayList<BaseMainListItem>> = _items
    var currentPage = 1
        private set
    var errorMessage = MutableLiveData<Int?>(null)
    var isInitialLoading = MutableLiveData(false)
    var isNextPageLoading = MutableLiveData(false)

    private var currentPhotosOrderType = PhotosOrderType.Latest.queryParam

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
            val response = when (currentListMode) {
                ListMode.Editorial ->
                    photoService.getAllPaginated(currentPage++, pageSize, currentPhotosOrderType)
                ListMode.Collections ->
                    collectionService.getAllPaginated(currentPage++, pageSize)
                ListMode.Following ->
                    photoService.getAllPaginated(currentPage++, pageSize, currentPhotosOrderType)
            }
            isInitialLoading.postValue(false)
            isNextPageLoading.postValue(false)
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
            } else {
                val error = when (response.code()) {
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

    fun shareCollection(collectionId: String) {

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