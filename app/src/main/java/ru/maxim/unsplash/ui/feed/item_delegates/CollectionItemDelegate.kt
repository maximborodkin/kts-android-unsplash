package ru.maxim.unsplash.ui.feed.item_delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.ItemCollectionBinding
import ru.maxim.unsplash.ui.feed.item_delegates.CollectionItemDelegate.PhotosCollectionViewHolder
import ru.maxim.unsplash.ui.feed.items.BaseFeedListItem
import ru.maxim.unsplash.ui.feed.items.CollectionItem

class CollectionItemDelegate(
    private val onShare: (collectionId: String) -> Unit,
    private val onOpenCollectionDetails: (
        collectionId: String,
        transitionExtras: Array<Pair<View, String>>
    ) -> Unit,
    private val onOpenProfile: (
        userUsername: String,
        transitionExtras: Array<Pair<View, String>>
    ) -> Unit
) : BaseMainItemDelegate<CollectionItem, PhotosCollectionViewHolder>() {

    override fun isForViewType(
        item: BaseFeedListItem,
        items: MutableList<BaseFeedListItem>,
        position: Int
    ): Boolean = item is CollectionItem

    override fun onCreateViewHolder(parent: ViewGroup): PhotosCollectionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collection, parent, false)
        return PhotosCollectionViewHolder(itemView)
    }

    override fun onBindViewHolder(
        item: CollectionItem,
        holder: PhotosCollectionViewHolder,
        payloads: MutableList<Any>
    ) = holder.bind(item)

    inner class PhotosCollectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding by viewBinding(ItemCollectionBinding::bind)
        private val context = itemView.context

        fun bind(collection: CollectionItem) {
            with(binding) {
                this.collection = collection

                itemCollectionShareBtn.setOnClickListener { onShare(collection.id) }

                val openProfileAction = {
                    onOpenProfile(
                        collection.authorUsername,
                        arrayOf(
                            itemCollectionAuthorAvatar to
                                    context.getString(R.string.photo_details_author_avatar_transition),
                            itemCollectionAuthorName to
                                    context.getString(R.string.photo_details_author_name_transition)
                        )
                    )
                }
                itemCollectionAuthorAvatar.setOnClickListener { openProfileAction() }
                itemCollectionAuthorName.setOnClickListener { openProfileAction() }

                root.setOnClickListener {
                    onOpenProfile(
                        collection.id,
                        arrayOf(
                            itemCollectionTitle to
                                    context.getString(R.string.collection_details_title_transition),
                            itemCollectionAuthorAvatar to
                                    context.getString(R.string.collection_details_author_avatar_transition),
                            itemCollectionAuthorName to
                                    context.getString(R.string.collection_details_author_name_transition)
                        )
                    )
                }
            }
        }
    }
}