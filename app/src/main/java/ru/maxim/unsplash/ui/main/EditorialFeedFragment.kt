package ru.maxim.unsplash.ui.main

import kotlinx.coroutines.flow.Flow
import org.koin.android.ext.android.inject
import ru.maxim.unsplash.repository.PhotoRepository
import ru.maxim.unsplash.ui.feed.FeedFragment
import ru.maxim.unsplash.util.Result

class EditorialFeedFragment : FeedFragment() {
    private val photoRepository: PhotoRepository by inject()

    override suspend fun getItemsPage(page: Int): Flow<Result<List<Any>>> =
        photoRepository.getFeedPage(page) as Flow<Result<List<Any>>>
}