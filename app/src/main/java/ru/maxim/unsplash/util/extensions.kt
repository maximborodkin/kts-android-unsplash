package ru.maxim.unsplash.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.content.res.AppCompatResources
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import ru.maxim.unsplash.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.DATE
import java.util.Calendar.YEAR

fun TextView.setDrawableStart(drawableResource: Int) =
    setCompoundDrawablesRelativeWithIntrinsicBounds(
        AppCompatResources.getDrawable(context, drawableResource), //start
        compoundDrawablesRelative[1],                              //top
        compoundDrawablesRelative[2],                              //end
        compoundDrawablesRelative[3]                               //bottom
    )

fun TextView.setDrawableEnd(drawableResource: Int) =
    setCompoundDrawablesRelativeWithIntrinsicBounds(
        this.compoundDrawablesRelative[0],                         //start
        this.compoundDrawablesRelative[1],                         //top
        AppCompatResources.getDrawable(context, drawableResource), //end
        this.compoundDrawablesRelative[3]                          //bottom
    )

fun TextView.clearDrawables() =
    setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)

fun Context.toast(text: String?) = Toast.makeText(this, text, LENGTH_SHORT).show()
fun Context.toast(resource: Int) = toast(getString(resource))
fun Context.longToast(text: String?) = Toast.makeText(this, text, LENGTH_LONG).show()
fun Context.longToast(resource: Int) = longToast(getString(resource))

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

/**
 * Extension function for [ImageView] class.
 * Loads image by [url] into current view.
 * Has additional parameters [thumbnail] and [blurHash] to using it as placeholder.
 * Using [blurHash] is preferably because in this case placeholder will be created without network
 * request and faster then thumbnail loading.
 * [onFinish] callback called after loading finished independently of result.
 * */
@SuppressLint("CheckResult")
fun ImageView.load(
    url: String,
    thumbnail: String? = null,
    blurHash: String? = null,
    onFinish: (() -> Unit)? = null
) {
    val requestBuilder = Glide.with(context).load(CacheGlideUrl(url))

    if (!blurHash.isNullOrBlank()) {
        val blurredPlaceholder = BlurHashDecoder.decode(
            blurHash = blurHash,
            width = 30,
            height = (layoutParams.height.toFloat() * 30 / layoutParams.width).toInt(),
            punch = 1F,
            useCache = false
        )
        requestBuilder.thumbnail(Glide.with(context).load(blurredPlaceholder).fitCenter())
    } else if (!thumbnail.isNullOrBlank()) {
        requestBuilder.thumbnail(Glide.with(context).load(thumbnail))
    } else {
        requestBuilder.placeholder(CircularProgressDrawable(context).apply {
            strokeWidth = 3F
            centerRadius = 64F
            start()
        })
    }
    requestBuilder
        .error(R.drawable.ic_warning)
        .finishCallback { onFinish?.invoke() }
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .into(this)
}

fun Date?.dateTimeString(): String? = this?.let { "${dateString()} ${timeString()}" }

fun Date?.dateString(): String? =
    this?.let { SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(this) }

fun Date?.simpleDateString(): String? =
    this?.let { SimpleDateFormat("d MMMM", Locale.getDefault()).format(this) }

fun Date?.timeString(): String? =
    this?.let { SimpleDateFormat("HH:mm", Locale.getDefault()).format(this) }

/**
 * Extension function for [java.util.Date] class.
 * Provides date string based on current date.
 * @return for today - only time,
 *         for date in current year - date without year,
 *         otherwise - full date.
 * */
fun Date?.adaptiveString(): String? = this?.let {
    val today = Calendar.getInstance()
    val date = Calendar.getInstance().apply { time = this@adaptiveString }
    return when {
        date[DATE] == today[DATE] -> timeString()
        date[YEAR] == today[YEAR] -> simpleDateString()
        else -> dateString()
    }
}