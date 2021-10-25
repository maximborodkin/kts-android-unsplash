package ru.maxim.unsplash.repository.remote

import net.openid.appauth.ResponseTypeValues

object AuthConfig {
    const val authUri = "https://unsplash.com/oauth/authorize"
    const val tokenUri = "https://unsplash.com/oauth/token"
    const val authCallback = "school://ru.maxim.unsplash/oauth2/callback"
    const val responseType = ResponseTypeValues.CODE
    const val scope = "public read_user write_user read_photos write_photos write_likes write_followers read_collections write_collections"
    const val clientId = "ZYRSmrQzgxj-zIhJA8-kv-9ezJPe_MQoP5a1yfxU67M"
    const val clientSecret = "hGzaC9ueeZPHtMaq3mC6pTF6IUgG9gglffC51-VT_qs"

    const val logoutUri = "https://unsplash.com/logout"
    const val logoutCallback = "school://ru.maxim.unsplash/oauth2/logout_callback"
}
