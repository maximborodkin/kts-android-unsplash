package ru.maxim.unsplash.util

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PaginationScrollListener(
    private val layoutManager: LinearLayoutManager,
    private val requestNextItems: () -> Unit,
    private val visibilityThreshold: Int = DEFAULT_VISIBILITY_THRESHOLD
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(
        recyclerView: RecyclerView,
        dx: Int,
        dy: Int
    ) = with(layoutManager) {
        if (dy <= 0) return

        val scrolledOffItems = findFirstVisibleItemPosition()
        val visibleItems = childCount
        val itemsTotal = itemCount

        if (visibleItems + scrolledOffItems + visibilityThreshold >= itemsTotal) {
            requestNextItems.invoke()
        }
    }

    companion object {
        private const val DEFAULT_VISIBILITY_THRESHOLD = 5
    }
}