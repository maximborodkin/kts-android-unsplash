package ru.maxim.unsplash.ui.dialogs.add_to_collection

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.maxim.unsplash.R
import ru.maxim.unsplash.network.exception.*
import ru.maxim.unsplash.repository.CollectionRepository
import ru.maxim.unsplash.ui.dialogs.add_to_collection.AddToCollectionViewModel.AddToCollectionState.*
import ru.maxim.unsplash.util.Result

class AddToCollectionViewModel(
    application: Application,
    private val collectionRepository: CollectionRepository,
) : AndroidViewModel(application) {

    private val _addToCollectionState = MutableStateFlow<AddToCollectionState>(Empty)
    val addToCollectionState: StateFlow<AddToCollectionState> = _addToCollectionState.asStateFlow()

    sealed class AddToCollectionState {
        object Empty : AddToCollectionState()
        object Success : AddToCollectionState()
        data class Error(@StringRes val messageRes: Int) : AddToCollectionState()
    }

    fun addToCollection(photoId: String, collectionId: String) = viewModelScope.launch {
        val result = collectionRepository
            .addPhotoToCollection(photoId, collectionId)
            .catch { e -> _addToCollectionState.emit(Error(getErrorMessage(e))) }
            .first()

        if (result is Result.Success) {
            _addToCollectionState.emit(Success)
        } else if (result is Result.Error) {
            _addToCollectionState.emit(Error(getErrorMessage(result.exception)))
        }
    }

    private fun getErrorMessage(exception: Throwable): Int =
        when(exception) {
            is UnauthorizedException -> R.string.unauthorized_error
            is ForbiddenException -> R.string.forbidden_add_to_collection
            is NotFoundException ->
                if (exception.localizedMessage?.contains("photo", true) == true)
                    R.string.photo_not_found
                else R.string.collection_not_found
            is TimeoutException -> R.string.timeout_error
            is ServerErrorException -> R.string.server_error
            else -> R.string.common_loading_error
        }

    @Suppress("UNCHECKED_CAST")
    class AddToCollectionViewModelFactory(
        private val application: Application,
        private val collectionRepository: CollectionRepository
    ) : ViewModelProvider.AndroidViewModelFactory(application) {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddToCollectionViewModel::class.java)) {
                return AddToCollectionViewModel(application, collectionRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class ${modelClass.simpleName}")
        }
    }
}