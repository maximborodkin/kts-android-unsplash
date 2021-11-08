package ru.maxim.unsplash.ui.feed

import android.view.View

interface FeedActionsListener {

    fun onPhotoClick(
        photoId: String,
        photoUrl: String?,
        blurHash: String?,
        width: Int,
        height: Int,
        transitionExtras: Array<Pair<View, String>>,
    ) {}

    fun onCollectionClick(collectionId: String, transitionExtras: Array<Pair<View, String>>) {}

    fun onProfileClick(userUsername: String, transitionExtras: Array<Pair<View, String>>) {}

    fun onAddToCollectionClick(photoId: String) {}
}