package ru.maxim.unsplash.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter(value = ["image", "thumbnail", "blurHash"], requireAll = false)
fun loadImage(imageView: ImageView, image: String?, thumbnail: String?, blurHash: String?) {
    if (image.isNullOrEmpty()) return
    imageView.load(url = image, thumbnail = thumbnail, blurHash = blurHash)
}
