package ru.maxim.unsplash.ui.feed

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.maxim.unsplash.R
import ru.maxim.unsplash.domain.model.Collection
import ru.maxim.unsplash.domain.model.Photo
import ru.maxim.unsplash.network.exception.*
import ru.maxim.unsplash.repository.CollectionRepository
import ru.maxim.unsplash.repository.PhotoRepository
import ru.maxim.unsplash.ui.main.MainFragment.ListMode
import ru.maxim.unsplash.ui.feed.FeedViewModel.MainState.*
import ru.maxim.unsplash.ui.feed.items.BaseFeedListItem
import ru.maxim.unsplash.ui.feed.items.PageLoadingErrorItem
import ru.maxim.unsplash.ui.feed.items.mappers.*
import ru.maxim.unsplash.util.Result.*
import timber.log.Timber

class FeedViewModel private constructor(
    application: Application,
    private val photoRepository: PhotoRepository,
    private val collectionRepository: CollectionRepository,
    private val listMode: ListMode,
    private val collectionId: String?
) : AndroidViewModel(application) {

    private val items = arrayListOf<BaseFeedListItem>()
    private var currentPage = 1

    private val _mainState = MutableStateFlow<MainState>(Empty)
    val mainState: StateFlow<MainState> = _mainState.asStateFlow()

    sealed class MainState {
        object Empty : MainState()
        object Refreshing : MainState()

        object InitialLoading : MainState()
        data class InitialLoadingSuccess(
            val items: ArrayList<BaseFeedListItem>,
            val isCache: Boolean,
        ) : MainState()
        data class InitialLoadingError(
            @StringRes val message: Int?,
            val items: ArrayList<BaseFeedListItem>? = null
        ) : MainState()

        object PageLoading : MainState()
        data class PageLoadingSuccess(val itemsAdded: ArrayList<BaseFeedListItem>) : MainState()
        data class PageLoadingError(@StringRes val message: Int?) : MainState()

        data class SetLikeSuccess(val itemPosition: Int) : MainState()
        data class SetLikeError(@StringRes val message: Int?) : MainState()
    }

    fun loadNextPage(page: Int? = null) = viewModelScope.launch {
        if (page != null) currentPage = page

        if (currentPage == 1) {
            if (_mainState.value !is Refreshing) {
                _mainState.emit(InitialLoading)
            }
        } else {
            _mainState.emit(PageLoading)
        }

        val items = when(listMode) {
            ListMode.Editorial -> photoRepository.getFeedPage(currentPage)
            ListMode.Collections -> collectionRepository.getFeedPage(currentPage)
            ListMode.Profile -> photoRepository.getFeedPage(currentPage)
            ListMode.CollectionPhotos -> photoRepository.getCollectionPhotosPage(collectionId!!, currentPage)
        }

        items.collect { result ->
            when (result) {
                is Loading -> {
                    Timber.tag("RepositoryState").d("Loading data=${result.data?.size?:0}")

                    // Show cached data if now is initial loading and cache exists
                    // Ignore cache for other pages
                    if (currentPage == 1) {
                        result.data?.let { _mainState.emit(InitialLoadingSuccess(mapResponse(it), true)) }
                    }
                }
                is Success -> {
                    Timber.tag("RepositoryState").d("Success data=${result.data.size}")

                    if (currentPage == 1) {
                        _mainState.emit(InitialLoadingSuccess(mapResponse(result.data), false))
                    } else {
                        _mainState.emit(PageLoadingSuccess(mapResponse(result.data)))
                    }
                    currentPage++
                }
                is Error -> {
                    Timber.w(result.exception)
                    val errorMessage = when (result.exception) {
                        is UnauthorizedException -> R.string.unauthorized_error
                        is ForbiddenException -> R.string.common_forbidden
                        is NotFoundException -> R.string.common_not_found
                        is TimeoutException -> R.string.timeout_error
                        is ServerErrorException -> R.string.server_error
                        is NoConnectionException -> R.string.no_internet
                        else -> R.string.common_loading_error
                    }
                    Timber.tag("RepositoryState").d("Error messageRes=$errorMessage data=${result.data?.size?:0}")
                    if (currentPage == 1) {
                        _mainState.emit(
                            InitialLoadingError(errorMessage, result.data?.let { mapResponse(it) })
                        )
                    } else {
                        _mainState.emit(PageLoadingError(errorMessage))
                    }
                }
            }
        }
    }

    private fun mapResponse(response: List<Any>) = ArrayList(
        response.map {
            when (it) {
                is Photo -> it.mapToItem()
                is Collection -> it.mapToItem()
                else -> throw IllegalArgumentException("Unknown item type")
            }
        }
    )

    fun refresh() = viewModelScope.launch {
        _mainState.emit(Refreshing)
        loadNextPage(1)
    }

    fun setLike(photoId: String, itemPosition: Int) = viewModelScope.launch(IO) {
//        if (items.size < itemPosition || items[itemPosition] !is PhotoItem) {
//            withContext(Main) { _mainState.emit(SetLikeError(R.string.unable_to_set_like)) }
//        }
//        val photoItem = items[itemPosition] as PhotoItem
//        val response =
//            if (photoItem.likedByUser) photoService.removeLike(photoId)
//            else photoService.setLike(photoId)
//
//        val like = response.body()
//        if (response.isSuccessful && like != null) {
//            photoItem.apply {
//                likedByUser = like.photo.likedByUser
//                likesCount = like.photo.likes
//            }
//            withContext(Main) { _mainState.emit(SetLikeSuccess(itemPosition)) }
//        } else {
//            val error = when (response.code()) {
//                401 -> {
//                    // TODO: update auth credentials
//                    R.string.unauthorized_error
//                }
//                408 -> R.string.timeout_error
//                in 400..499 -> R.string.unable_to_set_like
//                in 500..599 -> R.string.server_error
//                else -> null
//            }
//            withContext(Main) { _mainState.emit(SetLikeError(error)) }
//        }
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

    @Suppress("UNCHECKED_CAST")
    class MainViewModelFactory(
        private val application: Application,
        private val photoRepository: PhotoRepository,
        private val collectionRepository: CollectionRepository,
        private val listMode: ListMode,
        private val collectionId: String?
    ) : ViewModelProvider.AndroidViewModelFactory(application) {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
                return FeedViewModel(
                    application,
                    photoRepository,
                    collectionRepository,
                    listMode,
                    collectionId
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class ${modelClass.simpleName}")
        }
    }
}