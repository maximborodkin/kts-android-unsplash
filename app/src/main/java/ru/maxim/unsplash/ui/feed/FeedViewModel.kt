package ru.maxim.unsplash.ui.feed

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.maxim.unsplash.R
import ru.maxim.unsplash.domain.model.Collection
import ru.maxim.unsplash.domain.model.Photo
import ru.maxim.unsplash.network.exception.*
import ru.maxim.unsplash.repository.CollectionRepository
import ru.maxim.unsplash.repository.PhotoRepository
import ru.maxim.unsplash.ui.feed.FeedViewModel.FeedState.*
import ru.maxim.unsplash.ui.feed.items.BaseFeedListItem
import ru.maxim.unsplash.ui.feed.items.mappers.mapToItem
import ru.maxim.unsplash.util.Result
import ru.maxim.unsplash.util.Result.*

class FeedViewModel private constructor(
    application: Application,
    private val photoRepository: PhotoRepository,
    private val collectionRepository: CollectionRepository,
    private val getItemsPage: suspend (page: Int) -> Flow<Result<List<Any>>>
) : AndroidViewModel(application) {

    private var currentPage = 1

    private val _feedState = MutableStateFlow<FeedState>(Empty)
    val feedState: StateFlow<FeedState> = _feedState.asStateFlow()

    sealed class FeedState {
        object Empty : FeedState()
        object Refreshing : FeedState()

        object InitialLoading : FeedState()
        data class InitialLoadingSuccess(
            val items: ArrayList<BaseFeedListItem>
        ) : FeedState()
        data class InitialLoadingError(
            @StringRes val message: Int?,
            val items: ArrayList<BaseFeedListItem>? = null
        ) : FeedState()

        object PageLoading : FeedState()
        data class PageLoadingSuccess(val itemsAdded: ArrayList<BaseFeedListItem>) : FeedState()
        data class PageLoadingError(@StringRes val message: Int?) : FeedState()

        data class SetLikeSuccess(val itemPosition: Int) : FeedState()
        data class SetLikeError(@StringRes val message: Int?) : FeedState()
    }

    fun loadNextPage(page: Int? = null) = viewModelScope.launch {
        if (page != null) currentPage = page

        if (currentPage == 1) {
            if (_feedState.value !is Refreshing) {
                _feedState.emit(InitialLoading)
            }
        } else {
            _feedState.emit(PageLoading)
        }

        getItemsPage(currentPage).collect { result ->
            when (result) {
                is Loading -> {

                    /*
                    * Show cached data if now is initial loading and cache exists
                    * For initial loading will be shown all cached items (independent of it count)
                    * Ignore cache for next pages (repository returns empty list)
                    * */
                    if (currentPage == 1) {
                        result.data?.let {
                            _feedState.emit(InitialLoadingSuccess(mapResponse(it)))
                        }
                    }
                }

                is Success -> {
                    if (currentPage == 1) {
                        _feedState.emit(InitialLoadingSuccess(mapResponse(result.data)))
                    } else {
                        _feedState.emit(PageLoadingSuccess(mapResponse(result.data)))
                    }
                    currentPage++
                }

                is Error -> {
                    val errorMessage = when (result.exception) {
                        is UnauthorizedException -> R.string.unauthorized_error
                        is ForbiddenException -> R.string.common_forbidden
                        is NotFoundException -> R.string.common_not_found
                        is TimeoutException -> R.string.timeout_error
                        is ServerErrorException -> R.string.server_error
                        is NoConnectionException -> R.string.no_internet
                        else -> R.string.common_loading_error
                    }

                    if (currentPage == 1) {
                        _feedState.emit(
                            InitialLoadingError(errorMessage, result.data?.let { mapResponse(it) })
                        )
                    } else {
                        _feedState.emit(PageLoadingError(errorMessage))
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
        _feedState.emit(Refreshing)
        loadNextPage(1)
    }

    fun setLike(photoId: String, itemPosition: Int) = viewModelScope.launch(IO) {

    }

    fun addToCollection(photoId: String) {

    }

    fun download(photoId: String) {

    }

    fun shareCollection(collectionId: String) {

    }

    fun retryPageLoading() {
        if (_feedState.value is PageLoadingError) {
            loadNextPage(currentPage)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class FeedViewModelFactory(
        private val application: Application,
        private val photoRepository: PhotoRepository,
        private val collectionRepository: CollectionRepository,
        private val getItemsPage: suspend (page: Int) -> Flow<Result<List<Any>>>
    ) : ViewModelProvider.AndroidViewModelFactory(application) {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
                return FeedViewModel(
                    application,
                    photoRepository,
                    collectionRepository,
                    getItemsPage
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class ${modelClass.simpleName}")
        }
    }
}