package ru.maxim.unsplash.ui.collection_details

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
import ru.maxim.unsplash.domain.model.Collection
import ru.maxim.unsplash.network.exception.*
import ru.maxim.unsplash.repository.CollectionRepository
import ru.maxim.unsplash.ui.collection_details.CollectionDetailsViewModel.CollectionDetailsState.*
import ru.maxim.unsplash.util.Result

class CollectionDetailsViewModel private constructor(
    application: Application,
    private val collectionRepository: CollectionRepository,
    private val collectionId: String
) : AndroidViewModel(application) {
    private val _collectionDetailsState = MutableStateFlow<CollectionDetailsState>(Empty)
    val collectionDetailsState: StateFlow<CollectionDetailsState> =
        _collectionDetailsState.asStateFlow()

    sealed class CollectionDetailsState {
        object Empty : CollectionDetailsState()
        object Refreshing : CollectionDetailsState()
        data class Success(val collection: Collection) : CollectionDetailsState()
        data class Error(@StringRes val messageRes: Int, val cache: Collection?) :
            CollectionDetailsState()
    }

    fun loadCollection() = viewModelScope.launch {
        val collection = collectionRepository.getById(collectionId)
        collection.collect { result ->
            when (result) {
                is Result.Loading -> {
                    result.data?.let { _collectionDetailsState.emit(Success(it)) }
                }
                is Result.Success -> {
                    result.data?.let { _collectionDetailsState.emit(Success(it)) }
                }
                is Result.Error -> {
                    val messageRes = when (result.exception) {
                        is UnauthorizedException -> {
                            //TODO: update auth credentials
                            R.string.unauthorized_error
                        }
                        is ForbiddenException -> R.string.forbidden_collection
                        is NotFoundException -> R.string.collection_not_found
                        is TimeoutException -> R.string.timeout_error
                        is ServerErrorException -> R.string.server_error
                        is NoConnectionException -> R.string.no_internet
                        else -> R.string.common_loading_error
                    }
                    _collectionDetailsState.emit(Error(messageRes, result.data))
                }
            }
        }
    }

    fun refresh() = viewModelScope.launch {
        _collectionDetailsState.emit(Refreshing)
        loadCollection()
    }

    @Suppress("UNCHECKED_CAST")
    class CollectionDetailsViewModelFactory(
        private val application: Application,
        private val collectionRepository: CollectionRepository,
        private val collectionId: String
    ) : ViewModelProvider.AndroidViewModelFactory(application) {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CollectionDetailsViewModel::class.java)) {
                return CollectionDetailsViewModel(
                    application,
                    collectionRepository,
                    collectionId
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class ${modelClass.simpleName}")
        }
    }
}