package ru.maxim.unsplash.ui.main

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.maxim.unsplash.repository.CollectionRepository
import ru.maxim.unsplash.repository.PhotoRepository
import ru.maxim.unsplash.ui.main.MainFragment.ListMode
import ru.maxim.unsplash.ui.main.MainViewModel.MainState.Empty
import ru.maxim.unsplash.ui.main.items.BaseMainListItem
import ru.maxim.unsplash.ui.main.items.PageLoadingErrorItem

class MainViewModel private constructor(
    application: Application,
    private val photoRepository: PhotoRepository,
    private val collectionRepository: CollectionRepository,
    private val listMode: ListMode
) : AndroidViewModel(application) {

    private val items = arrayListOf<BaseMainListItem>()
    private var currentPage = 1
    private var currentPhotosOrderType = PhotosOrderType.Latest.queryParam

    private val _mainState = MutableStateFlow<MainState>(Empty)
    val mainState: StateFlow<MainState> = _mainState.asStateFlow()

    sealed class MainState {
        object Empty : MainState()
        object Refreshing : MainState()

        object InitialLoading : MainState()
        data class InitialLoadingSuccess(
            val items: ArrayList<BaseMainListItem>,
            val isCache: Boolean = false
        ) : MainState()

        data class InitialLoadingError(@StringRes val message: Int?) : MainState()

        object PageLoading : MainState()
        data class PageLoadingSuccess(val itemsAdded: ArrayList<BaseMainListItem>) : MainState()
        data class PageLoadingError(@StringRes val message: Int?) : MainState()

        data class SetLikeSuccess(val itemPosition: Int) : MainState()
        data class SetLikeError(@StringRes val message: Int?) : MainState()
    }

    fun loadNextPage(page: Int? = null) = viewModelScope.launch {
        if (page != null) currentPage = page

        // Load 30 elements for first page
        val pageSize = if (currentPage == 1) {
            // set isInitialLoading true if this loading is not called for refresh
            if (_mainState.value !is Refreshing) {
                _mainState.emit(InitialLoading)
            }
            initialLoadSize
        } else {
            _mainState.emit(PageLoading)
            pageSize
        }

        try {
            loadPage(pageSize)
        } catch (e: Exception) {
            if (currentPage == 1){
                loadCache()
            } else {
                withContext(Main) { _mainState.emit(InitialLoadingError(null)) }
            }
        }
    }

    private suspend fun loadPage(pageSize: Int) = withContext(IO) {
        val response = when (listMode) {
            ListMode.Editorial ->
                photoService.getAllPaginated(currentPage, pageSize, currentPhotosOrderType)
            ListMode.Collections ->
                collectionService.getAllPaginated(currentPage, pageSize)
            ListMode.Following ->
                photoService.getAllPaginated(currentPage, pageSize, currentPhotosOrderType)
        }

        val responseBody = response.body()
        if (response.isSuccessful && responseBody != null) {
            val responseItems = mapResponse(responseBody)
            cacheResponse(responseBody)
            if (_mainState.value is PageLoading) {
                items.addAll(responseItems)
                withContext(Main) {
                    _mainState.emit(PageLoadingSuccess(responseItems))
                }
            } else {
                items.clear()
                if (responseItems.isEmpty()) items.add(EmptyListItem)
                items.addAll(responseItems)
                withContext(Main) { _mainState.emit(InitialLoadingSuccess(items)) }
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

            if (_mainState.value is PageLoading) {
                withContext(Main) { _mainState.emit(PageLoadingError(error)) }
            } else {
                withContext(Main) { _mainState.emit(InitialLoadingError(error)) }
            }
        }
    }

    private suspend fun cacheResponse(response: List<Any>) = withContext(IO) {
        val photoDao = database.photoDao()
        val collectionDao = database.collectionDao()
        for(item in response) {
            when(item) {
                is Photo -> {
                    photoDao.insert(item.fromPhoto())
                }
                is Collection -> {
                    collectionDao.insert(item.fromCollection())
                }
            }
        }
    }

    private suspend fun loadCache() = withContext(IO) {
        try {
            when (listMode) {
                ListMode.Editorial -> {
                    val photoDao = database.photoDao()
                    val photos = photoDao.getAll().map { it.toPhoto() }
                    withContext(Main) {
                        _mainState.emit(InitialLoadingSuccess(mapResponse(photos), true))
                    }
                }

                ListMode.Collections -> {
                    val collectionDao = database.collectionDao()
                    val collections = collectionDao.getAll().map { it.toCollection() }
                    withContext(Main) {
                        _mainState.emit(InitialLoadingSuccess(mapResponse(collections), true))
                    }
                }

                ListMode.Following -> {
                    val photoDao = database.photoDao()
                    val photos = photoDao.getAll().map { it.toPhoto() }
                    withContext(Main) {
                        _mainState.emit(InitialLoadingSuccess(mapResponse(photos), true))
                    }
                }
            }
        } catch (e: Exception) {
            withContext(Main) { _mainState.emit(InitialLoadingError((null))) }
        }
    }

    private fun mapResponse(response: List<Any>) = ArrayList(
        response.map {
            when (it) {
                is Photo -> PhotoItem.fromPhoto(it)
                is Collection -> CollectionItem.fromCollection(it)
                else -> throw IllegalArgumentException("Unknown item type")
            }
        }
    )

    fun refresh() = viewModelScope.launch {
        _mainState.emit(Refreshing)
        loadNextPage(1)
    }

    fun setLike(photoId: String, itemPosition: Int) = viewModelScope.launch(IO) {
        if (items.size < itemPosition || items[itemPosition] !is PhotoItem) {
            withContext(Main) { _mainState.emit(SetLikeError(R.string.unable_to_set_like)) }
        }
        val photoItem = items[itemPosition] as PhotoItem
        val response =
            if (photoItem.likedByUser) photoService.removeLike(photoId)
            else photoService.setLike(photoId)

        val like = response.body()
        if (response.isSuccessful && like != null) {
            photoItem.apply {
                likedByUser = like.photo.likedByUser
                likesCount = like.photo.likes
            }
            withContext(Main) { _mainState.emit(SetLikeSuccess(itemPosition)) }
        } else {
            val error = when (response.code()) {
                401 -> {
                    // TODO: update auth credentials
                    R.string.unauthorized_error
                }
                408 -> R.string.timeout_error
                in 400..499 -> R.string.unable_to_set_like
                in 500..599 -> R.string.server_error
                else -> null
            }
            withContext(Main) { _mainState.emit(SetLikeError(error)) }
        }
    }

    fun addToCollection(photoId: String) {

    }

    fun download(photoId: String) {

    }

    fun shareCollection(collectionId: String) {

    }

    fun retryPageLoading() {
        if (items.lastOrNull() is PageLoadingErrorItem) {
            loadNextPage()
        }
    }

    companion object {
        private const val pageSize = 10
        private const val initialLoadSize = 30
    }

    @Suppress("UNCHECKED_CAST")
    class MainViewModelFactory(
        private val application: Application,
        private val photoRepository: PhotoRepository,
        private val collectionRepository: CollectionRepository,
        private val listMode: ListMode
    ) : ViewModelProvider.AndroidViewModelFactory(application) {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(
                    application,
                    photoRepository,
                    collectionRepository,
                    listMode
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class ${modelClass.simpleName}")
        }
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