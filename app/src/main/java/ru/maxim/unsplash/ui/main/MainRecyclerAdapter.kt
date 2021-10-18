package ru.maxim.unsplash.ui.main

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import ru.maxim.unsplash.databinding.ItemPhotoBinding
import ru.maxim.unsplash.ui.main.item_delegates.*
import ru.maxim.unsplash.ui.main.items.*

class MainRecyclerAdapter(
    onSetLike: (photoId: String, itemPosition: Int) -> Unit,
    onAddToCollection: (photoId: String) -> Unit,
    onDownload: (photoId: String) -> Unit,
    onCollectionShare: (collectionId: String) -> Unit,
    onOpenPhotoDetails: (itemBinding: ItemPhotoBinding) -> Unit,
    onOpenCollectionDetails: (collectionId: String) -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit
) : AsyncListDifferDelegationAdapter<BaseMainListItem>(ComplexDiffCallback) {

    init {
        delegatesManager
            .addDelegate(PhotoItemDelegate(onSetLike, onAddToCollection, onDownload, onOpenPhotoDetails))
            .addDelegate(CollectionItemDelegate(onCollectionShare, onOpenCollectionDetails))
            .addDelegate(InitialLoadingItemDelegate())
            .addDelegate(PageLoadingItemDelegate())
            .addDelegate(EmptyListItemDelegate(onRefresh))
            .addDelegate(InitialLoadingErrorItemDelegate(onRefresh))
            .addDelegate(PageLoadingErrorItemDelegate(onRetry))
    }

    object ComplexDiffCallback : DiffUtil.ItemCallback<BaseMainListItem>() {
        override fun areItemsTheSame(first: BaseMainListItem, second: BaseMainListItem): Boolean =
            first.javaClass == second.javaClass && when (first) {
                is PhotoItem -> first.id == (second as PhotoItem).id
                is CollectionItem -> first.id == (second as CollectionItem).id
                is InitialLoadingErrorItem ->
                    first.errorMessage == (second as InitialLoadingErrorItem).errorMessage
                is PageLoadingErrorItem ->
                    first.errorMessage == (second as PageLoadingErrorItem).errorMessage
                else -> true
            }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(first: BaseMainListItem, second: BaseMainListItem) =
            first == second
    }
}