package ru.maxim.unsplash.repository.paging_source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.maxim.unsplash.model.*
import timber.log.Timber

class MainPagingSource : PagingSource<Int, Any>() {

    override fun getRefreshKey(state: PagingState<Int, Any>): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Any> {
        val page = params.key ?: 1
        val startIndex = (page - 1) * 10
        Timber.tag("PAGING").d("load with page $page and size ${params.loadSize}")
        val endIndex = if (startIndex + params.loadSize <= items.size)
            startIndex + params.loadSize
        else
            items.size

        val result = items.subList(startIndex, endIndex)
        val nextKey = if (result.size < params.loadSize) null else page + 1
        val prevKey = if (page == 1) null else page - 1
        return LoadResult.Page(result, prevKey, nextKey)
    }

    companion object {
        private val items = List(100) {
            when ((0..1).random()) {
                0 -> getRandomPhoto(it)
                1 -> getRandomCollection(it)
                else -> throw error("")
            }
        }

        private fun getRandomPhoto(id: Int) = Photo (
            id = id.toString(),
            createdAt = "",
            updatedAt = "",
            width = 1920,
            height = 1080,
            color = "#565656",
            blurHash = "dsfjvbskjnvar;uivbe",
            likes = (0..5).random(),
            likedByUser = false,
            description = "description #${id}",
            user = getRandomUser(id),
            currentUserCollections = null,
            urls = PhotoUrls(
                raw = "https://picsum.photos/${listOf(300, 600).random()}/${listOf(200, 300, 400).random()}",
                full = "",
                regular = "",
                small = "",
                thumb = ""
            ),
            links = null
        )

        private fun getRandomCollection(id: Int) = PhotosCollection (
            id = id.toLong(),
            title = "Collection #${id}",
            publishedAt = "",
            lastCollectedAt = "",
            updatedAt = "",
            coverPhoto = getRandomPhoto(id),
            totalPhotos = (1..10).random(),
            user = getRandomUser(id),
            links = null
        )

        private fun getRandomUser(id: Int) = User (
            id = id.toString(),
            username = "user#${id}",
            name = "User $id",
            bio = null,
            totalLikes = (0..100).random(),
            totalPhotos = (0..100).random(),
            totalCollections = (0..20).random(),
            location = null,
            portfolioUrl = null,
            profileImage = UserProfileImage(
                small = "https://www.androidfreeware.net/img2/org-indiepace-popcat.jpg",
                medium = "",
                large = ""
            ),
            links = null
        )
    }
}