package ru.maxim.unsplash.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

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