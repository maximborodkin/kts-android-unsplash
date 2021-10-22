package ru.maxim.unsplash.ui.collection_details

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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.maxim.unsplash.R
import ru.maxim.unsplash.database.Database
import ru.maxim.unsplash.domain.model.Collection
import ru.maxim.unsplash.network.RetrofitClient
import ru.maxim.unsplash.ui.collection_details.CollectionDetailsViewModel.CollectionDetailsState.*

class CollectionDetailsViewModel private constructor(
    application: Application,
    private val collectionId: String
) : AndroidViewModel(application) {

    val database = Database.instance
    private val collectionService = RetrofitClient.collectionService
    private val photoService = RetrofitClient.photoService
    private val _collectionDetailsState = MutableStateFlow<CollectionDetailsState>(Empty)
    val collectionDetailsState: StateFlow<CollectionDetailsState> =
        _collectionDetailsState.asStateFlow()

    sealed class CollectionDetailsState {
        object Empty : CollectionDetailsState()
        object Loading : CollectionDetailsState()
        object Refreshing : CollectionDetailsState()
        data class Success(val collection: Collection, val isCache: Boolean = false) :
            CollectionDetailsState()
        data class Error(@StringRes val messageRes: Int?) : CollectionDetailsState()
    }

    fun loadCollection() = viewModelScope.launch {
        withContext(IO) {
            try {
                val response = collectionService.getById(collectionId)
                val collection = response.body()
                if (response.isSuccessful && collection != null) {
                    cacheCollection(collection)
                    withContext(Main) { _collectionDetailsState.emit(Success(collection)) }
                } else {
                    val messageRes = when (response.code()) {
                        401 -> {
                            //TODO: update auth credentials
                            R.string.unauthorized_error
                        }
                        403 -> R.string.not_enough_rights_collection
                        404 -> R.string.collection_not_found
                        408 -> R.string.timeout_error
                        in 500..599 -> R.string.server_error
                        else -> null
                    }
                    withContext(Main) { _collectionDetailsState.emit(Error(messageRes)) }
                }
            } catch (e: Exception) {
                try {
                    loadCache()
                } catch (e: Exception) {
                    withContext(Main) { _collectionDetailsState.emit(Error(null)) }
                }
            }
        }
    }

    private suspend fun loadCache() = withContext(IO) {
        val collectionDao = database.collectionDao()
        val collection = collectionDao.getById(collectionId)?.toCollection()
        if (collection != null) {
            withContext(Main) { _collectionDetailsState.emit(Success(collection, true)) }
        } else {
            withContext(Main) { _collectionDetailsState.emit(Error(null)) }
        }
    }

    private suspend fun cacheCollection(collection: Collection) = withContext(IO) {
        database.collectionDao().insert(collection.fromCollection())
    }

    fun loadPhotos() = viewModelScope.launch {
        withContext(IO) {

        }
    }

    fun refresh() = viewModelScope.launch {
        _collectionDetailsState.emit(Refreshing)
        loadCollection()
    }

    @Suppress("UNCHECKED_CAST")
    class CollectionDetailsViewModelFactory(
        private val application: Application,
        private val collectionId: String
    ) : ViewModelProvider.AndroidViewModelFactory(application) {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CollectionDetailsViewModel::class.java)) {
                return CollectionDetailsViewModel(application, collectionId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class ${modelClass.simpleName}")
        }
    }
}