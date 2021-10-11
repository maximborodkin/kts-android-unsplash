package ru.maxim.unsplash.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import ru.maxim.unsplash.R

@BindingAdapter(value = ["image", "thumbnail"], requireAll = false)
fun loadImage(imageView: ImageView, image: String?, thumbnail: String?) {
    if (image.isNullOrEmpty()) return
    imageView.load(image, thumbnail)
}
