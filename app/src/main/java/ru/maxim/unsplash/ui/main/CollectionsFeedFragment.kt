package ru.maxim.unsplash.ui.main

import kotlinx.coroutines.flow.Flow
import org.koin.android.ext.android.get
import ru.maxim.unsplash.repository.CollectionRepository
import ru.maxim.unsplash.ui.feed.FeedFragment
import ru.maxim.unsplash.util.Result

class CollectionsFeedFragment : FeedFragment() {

    override suspend fun getItemsPage(page: Int): Flow<Result<List<Any>>> =
        get<CollectionRepository>().getFeedPage(page) as Flow<Result<List<Any>>>
}