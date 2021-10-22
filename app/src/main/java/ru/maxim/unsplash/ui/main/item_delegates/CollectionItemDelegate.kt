package ru.maxim.unsplash.ui.main.item_delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.ItemCollectionBinding
import ru.maxim.unsplash.ui.main.item_delegates.CollectionItemDelegate.PhotosCollectionViewHolder
import ru.maxim.unsplash.ui.main.items.BaseMainListItem
import ru.maxim.unsplash.ui.main.items.CollectionItem

class CollectionItemDelegate(
    private val onShare: (collectionId: String) -> Unit,
    private val onOpenCollectionDetails: (collectionId: String) -> Unit
) : BaseMainItemDelegate<CollectionItem, PhotosCollectionViewHolder>() {

    override fun isForViewType(
        item: BaseMainListItem,
        items: MutableList<BaseMainListItem>,
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

        fun bind(collection: CollectionItem) {
            with(binding) {
                this.collection = collection

                root.setOnClickListener { onOpenCollectionDetails(collection.id) }
                itemCollectionShareBtn.setOnClickListener { onShare(collection.id) }
            }
        }
    }
}