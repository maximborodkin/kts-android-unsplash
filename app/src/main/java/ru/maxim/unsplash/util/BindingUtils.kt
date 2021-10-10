package ru.maxim.unsplash.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

@BindingAdapter(value = ["image", "thumbnail"], requireAll = false)
fun loadImage(imageView: ImageView, image: String?, thumbnail: String?){
    if (image.isNullOrEmpty()) return
    Glide.with(imageView.context)
        .load(CacheGlideUrl(image))
        .thumbnail(Glide.with(imageView.context).load(thumbnail))
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(imageView)
}
