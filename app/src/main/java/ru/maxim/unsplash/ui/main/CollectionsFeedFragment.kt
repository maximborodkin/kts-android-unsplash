package ru.maxim.unsplash.ui.main

import kotlinx.coroutines.flow.Flow
import org.koin.android.ext.android.inject
import ru.maxim.unsplash.repository.CollectionRepository
import ru.maxim.unsplash.ui.feed.FeedFragment
import ru.maxim.unsplash.util.Result

class CollectionsFeedFragment : FeedFragment() {
    private val collectionRepository: CollectionRepository by inject()

    override suspend fun getItemsPage(page: Int): Flow<Result<List<Any>>> =
        collectionRepository.getFeedPage(page) as Flow<Result<List<Any>>>
}