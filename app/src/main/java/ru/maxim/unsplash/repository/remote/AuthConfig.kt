package ru.maxim.unsplash.repository.remote

import net.openid.appauth.ResponseTypeValues

object AuthConfig {
    const val authUri = "https://unsplash.com/oauth/authorize"
    const val tokenUri = "https://unsplash.com/oauth/token"
    const val authCallback = "school://ru.maxim.unsplash/oauth2/callback"
    const val responseType = ResponseTypeValues.CODE
    const val scope = "public read_user write_user read_photos write_photos write_likes write_followers read_collections write_collections"
    const val clientId = "clientId"
    const val clientSecret = "clientSecret"

    var accessToken: String? = null
}