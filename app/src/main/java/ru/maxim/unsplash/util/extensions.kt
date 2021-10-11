package ru.maxim.unsplash.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import ru.maxim.unsplash.R

fun TextView.setDrawableStart(drawableResource: Int) =
    setCompoundDrawablesRelativeWithIntrinsicBounds(drawableResource, 0, 0, 0)

fun TextView.setDrawableEnd(drawableResource: Int) =
    setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, drawableResource, 0)

fun TextView.clearDrawables() =
    setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)

fun Context.toast(text: String?) =
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

fun Context.toast(resource: Int) =
    Toast.makeText(this, getString(resource), Toast.LENGTH_SHORT).show()

fun Context.longToast(text: String?) =
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()

fun Context.longToast(resource: Int) =
    Toast.makeText(this, getString(resource), Toast.LENGTH_LONG).show()

fun RequestBuilder<Drawable>.finishCallback(onFinish: () -> Unit): RequestBuilder<Drawable> {
    return listener(object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            onFinish()
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            onFinish()
            return false
        }
    })
}

fun ImageView.load(url: String, thumbnail: String? = null, onFinish: (() -> Unit)? = null) =
    Glide.with(this)
        .load(CacheGlideUrl(url))
        .thumbnail(Glide.with(this).load(thumbnail))
        .placeholder(CircularProgressDrawable(this.context).apply {
            strokeWidth = 3F
            centerRadius = 64F
            start()
        })
        .error(R.drawable.ic_warning)
        .finishCallback { onFinish?.invoke() }
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)