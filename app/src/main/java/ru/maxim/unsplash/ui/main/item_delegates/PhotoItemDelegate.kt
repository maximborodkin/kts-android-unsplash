package ru.maxim.unsplash.ui.main.item_delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.ItemPhotoBinding
import ru.maxim.unsplash.ui.main.item_delegates.PhotoItemDelegate.PhotoViewHolder
import ru.maxim.unsplash.ui.main.items.BaseMainListItem
import ru.maxim.unsplash.ui.main.items.PhotoItem

class PhotoItemDelegate(
    private val onSetLike: (photoId: String, itemPosition: Int) -> Unit,
    private val onAddToCollection: (photoId: String) -> Unit,
    private val onDownload: (photoId: String) -> Unit,
    private val onOpenPhotoDetails: (itemBinding: ItemPhotoBinding) -> Unit
) : BaseMainItemDelegate<PhotoItem, PhotoViewHolder>() {

    override fun isForViewType(
        item: BaseMainListItem,
        items: MutableList<BaseMainListItem>,
        position: Int
    ): Boolean = item is PhotoItem

    override fun onCreateViewHolder(parent: ViewGroup): PhotoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(itemView)
    }

    override fun onBindViewHolder(
        item: PhotoItem,
        holder: PhotoViewHolder,
        payloads: MutableList<Any>
    ) = holder.bind(item)

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding by viewBinding(ItemPhotoBinding::bind)
        private val context = itemView.context
        fun bind(photo: PhotoItem) {
            with(binding) {
                this.photo = photo
                root.setOnClickListener { onOpenPhotoDetails(this) }

                // Set initial ImageView size to photo dimensions
                val screenWidth = context.resources.displayMetrics.widthPixels
                val imageRatio = photo.width.toDouble() / photo.height.toDouble()
                itemPhotoImage.layoutParams.apply {
                    width = screenWidth
                    height = (screenWidth / imageRatio).toInt()
                }

                itemPhotoLikeBtn.setOnClickListener { onSetLike(photo.id, absoluteAdapterPosition) }
                itemPhotoAddBtn.setOnClickListener { onAddToCollection(photo.id) }
                itemPhotoDownloadBtn.setOnClickListener { onDownload(photo.id) }
                executePendingBindings()
            }
        }
    }
}