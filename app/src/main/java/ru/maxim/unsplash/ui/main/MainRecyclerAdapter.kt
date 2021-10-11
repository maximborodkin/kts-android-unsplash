package ru.maxim.unsplash.ui.main

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import ru.maxim.unsplash.databinding.ItemPhotoBinding
import ru.maxim.unsplash.ui.main.item_delegates.*
import ru.maxim.unsplash.ui.main.items.BaseMainListItem
import ru.maxim.unsplash.ui.main.items.LoadingErrorItem
import ru.maxim.unsplash.ui.main.items.PhotoItem
import ru.maxim.unsplash.ui.main.items.PhotosCollectionItem

class MainRecyclerAdapter(
    onSetLike: (photoId: String, itemPosition: Int) -> Unit,
    onAddToCollection: (photoId: String) -> Unit,
    onDownload: (photoId: String) -> Unit,
    onCollectionShare: (collectionId: String) -> Unit,
    onOpenPhotoDetails: (photoId: String, itemBinding: ItemPhotoBinding) -> Unit,
    onOpenCollectionDetails: (collectionId: String) -> Unit,
    onRefresh: () -> Unit
) : AsyncListDifferDelegationAdapter<BaseMainListItem>(ComplexDiffCallback) {

    init {
        delegatesManager
            .addDelegate(PhotoItemDelegate(onSetLike, onAddToCollection, onDownload, onOpenPhotoDetails))
            .addDelegate(PhotosCollectionItemDelegate(onCollectionShare, onOpenCollectionDetails))
            .addDelegate(InitialLoadingItemDelegate())
            .addDelegate(PageLoadingItemDelegate())
            .addDelegate(EmptyListItemDelegate(onRefresh))
            .addDelegate(LoadingErrorItemDelegate(onRefresh))
    }

    object ComplexDiffCallback : DiffUtil.ItemCallback<BaseMainListItem>() {
        override fun areItemsTheSame(first: BaseMainListItem, second: BaseMainListItem): Boolean =
            first.javaClass == second.javaClass && when (first) {
                is PhotoItem -> first.id == (second as PhotoItem).id
                is PhotosCollectionItem -> first.id == (second as PhotosCollectionItem).id
                is LoadingErrorItem -> first.errorMessage == (second as LoadingErrorItem).errorMessage
                else -> true
            }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(first: BaseMainListItem, second: BaseMainListItem) =
            first == second
    }
}