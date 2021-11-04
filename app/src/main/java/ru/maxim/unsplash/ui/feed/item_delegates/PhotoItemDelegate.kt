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
    private val onSetLike: (photoId: String, itemPosition: Int) -> Unit,
    private val onAddToCollection: (photoId: String) -> Unit,
    private val onDownload: (photoId: String) -> Unit,
    private val onOpenPhotoDetails: (
        photoId: String,
        photoUrl: String?,
        blurHash: String?,
        width: Int,
        height: Int,
        transitionExtras: Array<Pair<View, String>>
    ) -> Unit,
    private val onOpenProfile: (
        userUsername: String,
        transitionExtras: Array<Pair<View, String>>
    ) -> Unit
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

        fun bind(photo: PhotoItem) {
            with(binding) {
                binding.photo
                this.photo = photo

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

                val openProfileAction = {
                    onOpenProfile(
                        photo.authorUsername,
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
                    onOpenPhotoDetails(
                        photo.id,
                        photo.regular,
                        photo.blurHash,
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
}