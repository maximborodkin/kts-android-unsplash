package ru.maxim.unsplash.ui.main

import kotlinx.coroutines.flow.Flow
import org.koin.android.ext.android.get
import ru.maxim.unsplash.repository.PhotoRepository
import ru.maxim.unsplash.ui.feed.FeedFragment
import ru.maxim.unsplash.util.Result

class EditorialFeedFragment : FeedFragment() {

    override suspend fun getItemsPage(page: Int): Flow<Result<List<Any>>> =
        get<PhotoRepository>().getFeedPage(page) as Flow<Result<List<Any>>>
}