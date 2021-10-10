package ru.maxim.unsplash.util

import com.bumptech.glide.load.model.GlideUrl
import java.net.URL

class CacheGlideUrl(private val url: String) : GlideUrl(url) {

    override fun getCacheKey(): String =
        if(url.contains("ixid=")) {
            url.replace("ixid=\\w+&", "")
        } else url
}
