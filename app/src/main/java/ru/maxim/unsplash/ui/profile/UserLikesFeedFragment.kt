package ru.maxim.unsplash.ui.profile

import android.os.Bundle
import kotlinx.coroutines.flow.Flow
import org.koin.android.ext.android.get
import ru.maxim.unsplash.repository.PhotoRepository
import ru.maxim.unsplash.ui.feed.FeedFragment
import ru.maxim.unsplash.util.Result

class UserLikesFeedFragment : FeedFragment() {
    private var userUsername: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userUsername = arguments?.getString(userUsernameKey) ?:
        throw IllegalArgumentException(
            "$userUsernameKey argument must be provided to UserLikesFeedFragment"
        )
    }

    override suspend fun getItemsPage(page: Int): Flow<Result<List<Any>>> =
        get<PhotoRepository>().getUserLikedPage(
            userUsername ?: throw IllegalStateException("$userUsername cannot be null"),
            page
        ) as Flow<Result<List<Any>>>

    companion object {
        const val userUsernameKey = "user_username"
    }
}