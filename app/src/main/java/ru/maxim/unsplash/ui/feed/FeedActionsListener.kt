package ru.maxim.unsplash.ui.feed

import android.view.View

interface FeedActionsListener {

    fun openPhotoDetails(
        photoId: String,
        photoUrl: String?,
        blurHash: String?,
        width: Int,
        height: Int,
        transitionExtras: Array<Pair<View, String>>,
    )

    fun openCollectionDetails(collectionId: String, transitionExtras: Array<Pair<View, String>>)
}