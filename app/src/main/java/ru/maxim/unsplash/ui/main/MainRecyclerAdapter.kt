package ru.maxim.unsplash.ui.main

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import ru.maxim.unsplash.model.Photo
import ru.maxim.unsplash.ui.main.item_delegates.InitialLoadingItemDelegate
import ru.maxim.unsplash.ui.main.item_delegates.PageLoadingItemDelegate
import ru.maxim.unsplash.ui.main.item_delegates.PhotoItemDelegate
import ru.maxim.unsplash.ui.main.item_delegates.PhotosCollectionItemDelegate
import ru.maxim.unsplash.ui.main.items.*

class MainRecyclerAdapter(
    onSetLike: (photoId: String, itemPosition: Int) -> Unit,
    onAddToCollection: (photoId: String) -> Unit,
    onDownload: (photoId: String) -> Unit,
    onCollectionShare: (collectionId: Long) -> Unit
) : AsyncListDifferDelegationAdapter<Any>(ComplexDiffCallback) {

    init {
        delegatesManager
            .addDelegate(PhotoItemDelegate(onSetLike, onAddToCollection, onDownload))
            .addDelegate(PhotosCollectionItemDelegate(onCollectionShare))
            .addDelegate(InitialLoadingItemDelegate())
            .addDelegate(PageLoadingItemDelegate())

    }

    object ComplexDiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(first: Any, second: Any): Boolean =
        first.javaClass == second.javaClass && when(first) {
            is PhotoItem -> first.id == (second as PhotoItem).id
            is PhotosCollectionItem -> first.id == (second as PhotosCollectionItem).id
            else -> true
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(first: Any, second: Any): Boolean = first == second
    }
}