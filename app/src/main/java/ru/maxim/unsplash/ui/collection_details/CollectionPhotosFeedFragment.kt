package ru.maxim.unsplash.ui.collection_details

import android.content.Context
import android.os.Bundle
import kotlinx.coroutines.flow.Flow
import org.koin.android.ext.android.get
import ru.maxim.unsplash.repository.PhotoRepository
import ru.maxim.unsplash.ui.feed.FeedFragment
import ru.maxim.unsplash.util.Result

class CollectionPhotosFeedFragment : FeedFragment() {
    private var collectionId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collectionId = arguments?.getString(collectionIdKey) ?:
            throw IllegalArgumentException(
                "$collectionIdKey argument must be provided to CollectionPhotosFeedFragment"
            )
    }

    override suspend fun getItemsPage(page: Int): Flow<Result<List<Any>>> =
        get<PhotoRepository>().getCollectionPhotosPage(
            collectionId ?: throw IllegalStateException("$collectionIdKey cannot be null"),
            page
        ) as Flow<Result<List<Any>>>

    companion object {
        const val collectionIdKey = "collection_id"
    }
}