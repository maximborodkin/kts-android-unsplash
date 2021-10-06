package ru.maxim.unsplash.ui.main.item_delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.ItemPhotosCollectionBinding
import ru.maxim.unsplash.ui.main.item_delegates.PhotosCollectionItemDelegate.PhotosCollectionViewHolder
import ru.maxim.unsplash.ui.main.items.BaseMainListItem
import ru.maxim.unsplash.ui.main.items.PhotosCollectionItem

class PhotosCollectionItemDelegate(
    private val onShare: (collectionId: String) -> Unit,
    private val onOpenCollectionDetails: (collectionId: String) -> Unit
) : BaseMainItemDelegate<PhotosCollectionItem, PhotosCollectionViewHolder>() {

    override fun isForViewType(
        item: BaseMainListItem,
        items: MutableList<BaseMainListItem>,
        position: Int
    ): Boolean =
        item is PhotosCollectionItem

    override fun onCreateViewHolder(parent: ViewGroup): PhotosCollectionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photos_collection, parent, false)
        return PhotosCollectionViewHolder(itemView)
    }

    override fun onBindViewHolder(
        item: PhotosCollectionItem,
        holder: PhotosCollectionViewHolder,
        payloads: MutableList<Any>
    ) {
        holder.bind(item)
    }

    inner class PhotosCollectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding by viewBinding(ItemPhotosCollectionBinding::bind)

        fun bind(collection: PhotosCollectionItem) {
            with(binding) {
                this.collection = collection

                root.setOnClickListener { onOpenCollectionDetails(collection.id) }
                collection.authorAvatar.let {
                    Glide.with(itemView.context).load(it).into(itemCollectionAuthorAvatar)
                }
                collection.cover.let {
                    Glide.with(itemView.context).load(it).into(itemCollectionCover)
                }
                itemCollectionShareBtn.setOnClickListener { onShare(collection.id) }
            }
        }
    }
}