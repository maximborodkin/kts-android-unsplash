package ru.maxim.unsplash.ui.feed.item_delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.ItemPhotoBinding
import ru.maxim.unsplash.ui.feed.item_delegates.PhotoItemDelegate.PhotoViewHolder
import ru.maxim.unsplash.ui.feed.items.BaseFeedListItem
import ru.maxim.unsplash.ui.feed.items.PhotoItem

class PhotoItemDelegate(
    private val onLikeClick: (photoId: String, hasLike: Boolean, itemPosition: Int) -> Unit,
    private val onDownloadClick: (photoId: String) -> Unit,
    private val onPhotoClick: (
        photoId: String,
        photoUrl: String?,
        blurHash: String?,
        width: Int,
        height: Int,
        transitionExtras: Array<Pair<View, String>>
    ) -> Unit,
    private val onProfileClick: (
        userUsername: String,
        transitionExtras: Array<Pair<View, String>>
    ) -> Unit,
    private val onAddToCollectionClick: (photoId: String) -> Unit
) : BaseMainItemDelegate<PhotoItem, PhotoViewHolder>() {

    override fun isForViewType(
        item: BaseFeedListItem,
        items: MutableList<BaseFeedListItem>,
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

        fun bind(photoItem: PhotoItem) = with(binding) {
            photo = photoItem

            // Set initial ImageView size to photo dimensions
            val screenWidth = context.resources.displayMetrics.widthPixels
            val imageRatio = photoItem.width.toDouble() / photoItem.height.toDouble()
            itemPhotoImage.layoutParams.apply {
                width = screenWidth
                height = (screenWidth / imageRatio).toInt()
            }

            itemPhotoLikeBtn.setOnClickListener {
                onLikeClick(
                    photoItem.id,
                    photoItem.likedByUser,
                    absoluteAdapterPosition
                )
            }
            itemPhotoAddBtn.setOnClickListener { onAddToCollectionClick(photoItem.id) }
            itemPhotoDownloadBtn.setOnClickListener { onDownloadClick(photoItem.id) }

            val openProfileAction = {
                onProfileClick(
                    photoItem.authorUsername,
                    arrayOf(
                        itemPhotoAuthorAvatar to
                                context.getString(R.string.photo_details_author_avatar_transition),
                        itemPhotoAuthorName to
                                context.getString(R.string.photo_details_author_name_transition)
                    )
                )
            }

            itemPhotoAuthorAvatar.setOnClickListener { openProfileAction() }
            itemPhotoAuthorName.setOnClickListener { openProfileAction() }

            root.setOnClickListener {
                onPhotoClick(
                    photoItem.id,
                    photoItem.regular,
                    photoItem.blurHash,
                    itemPhotoImage.width,
                    itemPhotoImage.height,
                    arrayOf(
                        itemPhotoImage to
                                context.getString(R.string.photo_details_image_transition),
                        itemPhotoLikeBtn to
                                context.getString(R.string.photo_details_like_btn_transition),
                        itemPhotoLikesCount to
                                context.getString(R.string.photo_details_likes_count_transition),
                        itemPhotoAuthorAvatar to
                                context.getString(R.string.photo_details_author_avatar_transition),
                        itemPhotoAuthorName to
                                context.getString(R.string.photo_details_author_name_transition)
                    )
                )
            }
        }
    }
}