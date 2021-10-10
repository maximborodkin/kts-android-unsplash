package ru.maxim.unsplash.util

import com.bumptech.glide.load.model.GlideUrl

class CacheGlideUrl(private val url: String) : GlideUrl(url) {

    override fun getCacheKey(): String =
        if(url.contains("?")) {
            url.split("?").first()
        } else url
}
