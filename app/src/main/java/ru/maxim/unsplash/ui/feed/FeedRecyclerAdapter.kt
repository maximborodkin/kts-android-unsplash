package ru.maxim.unsplash.ui.feed

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import ru.maxim.unsplash.ui.feed.item_delegates.*
import ru.maxim.unsplash.ui.feed.items.*

class FeedRecyclerAdapter(
    onLikeClick: (photoId: String, hasLike: Boolean, itemPosition: Int) -> Unit,
    onDownloadClick: (photoId: String) -> Unit,
    feedActionsListener: FeedActionsListener,
    onRefresh: () -> Unit,
    onRetry: () -> Unit
) : AsyncListDifferDelegationAdapter<BaseFeedListItem>(ComplexDiffCallback) {

    init {
        delegatesManager
            .addDelegate(
                PhotoItemDelegate(
                    onLikeClick = onLikeClick,
                    onDownloadClick = onDownloadClick,
                    onPhotoClick = feedActionsListener::onPhotoClick,
                    onProfileClick = feedActionsListener::onProfileClick,
                    onAddToCollectionClick = feedActionsListener::onAddToCollectionClick
                )
            )
            .addDelegate(
                CollectionItemDelegate(
                    onCollectionClick = feedActionsListener::onCollectionClick,
                    onProfileClick = feedActionsListener::onProfileClick
                )
            )
            .addDelegate(InitialLoadingItemDelegate())
            .addDelegate(PageLoadingItemDelegate())
            .addDelegate(EmptyListItemDelegate(onRefresh))
            .addDelegate(InitialLoadingErrorItemDelegate(onRefresh))
            .addDelegate(PageLoadingErrorItemDelegate(onRetry))
            .addDelegate(CacheShownItemDelegate(onRefresh))
    }

    object ComplexDiffCallback : DiffUtil.ItemCallback<BaseFeedListItem>() {
        override fun areItemsTheSame(first: BaseFeedListItem, second: BaseFeedListItem): Boolean =
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
        override fun areContentsTheSame(first: BaseFeedListItem, second: BaseFeedListItem) =
            first == second
    }
}