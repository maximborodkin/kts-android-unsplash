package ru.maxim.unsplash.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.ItemPhotoBinding
import ru.maxim.unsplash.databinding.ItemPhotosCollectionBinding
import ru.maxim.unsplash.model.Photo
import ru.maxim.unsplash.model.PhotosCollection
import ru.maxim.unsplash.util.toast

class MainRecyclerAdapter :
    PagingDataAdapter<Any, MainRecyclerAdapter.MainListViewHolder>(PhotosDiffCallback) {

    override fun onBindViewHolder(holder: MainListViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Photo -> PHOTO_ITEM_ID
            is PhotosCollection -> COLLECTION_ITEM_ID
            else -> throw IllegalArgumentException("Unknown item type ${getItem(position)}")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            PHOTO_ITEM_ID -> {
                val view = inflater.inflate(R.layout.item_photo, parent, false)
                PhotoViewHolder(view)
            }
            COLLECTION_ITEM_ID -> {
                val view = inflater.inflate(R.layout.item_photos_collection, parent, false)
                PhotosCollectionViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown viewType $viewType")
        }
    }

    companion object {
        const val PHOTO_ITEM_ID = 1
        const val COLLECTION_ITEM_ID = 2
    }


    sealed class MainListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(model: Any)
    }

    inner class PhotoViewHolder(itemView: View) : MainListViewHolder(itemView) {
        private val binding by viewBinding(ItemPhotoBinding::bind)

        override fun bind(model: Any) {
            val photo = model as? Photo ?: throw IllegalArgumentException(
                "ru.maxim.unsplash.model.Photo object required for PhotoViewHolder"
            )
            with(binding) {
                this.photo = photo
                photo.user.profileImage?.small?.let {
                    Glide.with(itemView.context).load(it).into(itemPhotoAuthorAvatar)
                }
                photo.urls?.raw?.let {
                    Glide.with(itemView.context).load(it).into(itemPhotoImage)
                }
                itemPhotoLikeBtn.setOnClickListener {
                    photo.likes++
                    photo.likedByUser = !photo.likedByUser
                    notifyItemChanged(this@PhotoViewHolder.bindingAdapterPosition)
                }
                itemPhotoDownloadBtn.setOnClickListener { itemView.context.toast("Download") }
                itemPhotoAddBtn.setOnClickListener { itemView.context.toast("Add to collection") }
            }
        }
    }

    inner class PhotosCollectionViewHolder(itemView: View) : MainListViewHolder(itemView) {
        private val binding by viewBinding(ItemPhotosCollectionBinding::bind)

        override fun bind(model: Any) {
            val photosCollection = model as? PhotosCollection ?: throw IllegalArgumentException(
                "ru.maxim.unsplash.model.PhotosCollection object required for PhotosCollectionViewHolder"
            )
            with(binding) {
                this.collection = photosCollection
                photosCollection.user.profileImage?.small?.let {
                    Glide.with(itemView.context).load(it).into(itemCollectionAuthorAvatar)
                }
                photosCollection.coverPhoto.urls?.raw?.let {
                    Glide.with(itemView.context).load(it).into(itemCollectionCover)
                }
            }
        }

    }
}

private object PhotosDiffCallback : DiffUtil.ItemCallback<Any>() {

    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean =
        oldItem is Photo && newItem is Photo && oldItem.id == newItem.id ||
        oldItem is PhotosCollection && newItem is PhotosCollection && oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean =
        oldItem is Photo && newItem is Photo && oldItem == newItem ||
        oldItem is PhotosCollection && newItem is PhotosCollection && oldItem == newItem
}

