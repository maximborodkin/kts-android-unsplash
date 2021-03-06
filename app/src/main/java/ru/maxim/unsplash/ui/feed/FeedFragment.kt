package ru.maxim.unsplash.ui.feed

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.FragmentFeedBinding
import ru.maxim.unsplash.ui.feed.FeedViewModel.FeedState.*
import ru.maxim.unsplash.ui.feed.items.*
import ru.maxim.unsplash.util.*

/**
 * The abstract fragment that represents a paginated list of items.
 * Every item must have a personal class inherited of
 * [ru.maxim.unsplash.ui.feed.items.BaseFeedListItem] in /items directory and item delegate
 * in /item_delegates. The delegates must be registered in [FeedRecyclerAdapter].
 *
 * For using this class create implementation and realize [getItemsPage] method based on data that
 * you want to show.
 * */
abstract class FeedFragment : Fragment(R.layout.fragment_feed) {

    private val binding by viewBinding(FragmentFeedBinding::bind)
    private val model: FeedViewModel by viewModel { parametersOf(::getItemsPage) }
    private var feedRecyclerAdapter by autoCleared<FeedRecyclerAdapter>()
    private var cacheWarningSnackbar: Snackbar? = null

    protected abstract suspend fun getItemsPage(page: Int): Flow<Result<List<Any>>>

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()

        feedRecyclerAdapter = FeedRecyclerAdapter(
            onLikeClick = model::setLike,
            onDownloadClick = model::download,
            feedActionsListener = parentFragment as FeedActionsListener,
            onRefresh = ::loadInitialPage,
            onRetry = ::retryLoading
        )

        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            feedRecycler.adapter = feedRecyclerAdapter
            feedRecycler.addOnScrollListener(
                PaginationScrollListener(
                    feedRecycler.layoutManager as LinearLayoutManager,
                    ::onLoadNextPage
                )
            )
            feedSwipeRefresh.setOnRefreshListener(::refresh)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.feedState.collect { state ->
                when (state) {
                    Empty -> {
                        if (savedInstanceState == null) loadInitialPage()
                    }

                    Refreshing -> with(binding) {
                        feedSwipeRefresh.isRefreshing = true
                    }

                    InitialLoading -> with(feedRecyclerAdapter) {
                        if (items.isEmpty()){
                            items = listOf(InitialLoadingItem)
                            notifyDataSetChanged()
                        }
                    }

                    is InitialLoadingSuccess -> with(binding) {
                        feedSwipeRefresh.isRefreshing = false
                        var newItems = state.items
                        cacheWarningSnackbar?.dismiss()
                        if(newItems.isEmpty() )
                            newItems = if(state.isCache) {
                                listOf(InitialLoadingItem)
                            } else {
                                listOf(EmptyListItem)
                            }

                        feedRecyclerAdapter.items = newItems
                        feedRecyclerAdapter.notifyDataSetChanged()
                    }

                    is InitialLoadingError -> with(binding) {
                        feedSwipeRefresh.isRefreshing = false
                        val cache = state.items
                        if (cache != null && cache.isNotEmpty()) {
                            cacheWarningSnackbar =
                                Snackbar.make(
                                    view,
                                    R.string.cached_data_shown,
                                    Snackbar.LENGTH_INDEFINITE
                                )
                                    .setAction(R.string.refresh) { refresh() }
                                    .also { it.show() }

                            feedRecyclerAdapter.items = cache
                        } else {
                            feedRecyclerAdapter.items = listOf(
                                InitialLoadingErrorItem(state.message)
                            )
                        }
                        feedRecyclerAdapter.notifyDataSetChanged()
                        context?.longToast(state.message)
                    }

                    is PageLoading -> with(feedRecyclerAdapter) {
                        items = items.toMutableList() + PageLoadingItem
                        binding.feedRecycler.post { notifyDataSetChanged() }
                    }

                    is PageLoadingSuccess -> with(feedRecyclerAdapter) {
                        if (state.itemsAdded.isNotEmpty()) {
                            items = state.itemsAdded
                            binding.feedRecycler.post { notifyDataSetChanged() }
                        }
                    }

                    is PageLoadingError -> with(feedRecyclerAdapter) {
                        items = items.toMutableList().apply {
                            if (lastOrNull() is PageLoadingItem) removeLast()
                        } + PageLoadingErrorItem(state.message)
                        notifyDataSetChanged()
                    }

                    is SetLikeSuccess -> feedRecyclerAdapter.notifyItemChanged(state.itemPosition)

                    is SetLikeError -> state.message.let { context?.toast(it) }
                }
            }
        }
    }

    private fun retryLoading() {
        if (model.feedState.value is PageLoadingError)
            model.retryPageLoading()
    }

    private fun loadInitialPage() {
        if (model.feedState.value != InitialLoading)
            model.loadNextPage(1)
    }

    fun refresh() {
        val currentState = model.feedState.value
        if (currentState != Refreshing && currentState != InitialLoading) {
            model.refresh()
        } else {
            binding.feedSwipeRefresh.isRefreshing = false
        }
    }

    private fun onLoadNextPage() {
        val currentState = model.feedState.value
        /* Start page loading only if another page loading not running now and
         * previous loading is not failed
         * */
        if (currentState !is PageLoading && currentState !is PageLoadingError) {
            model.loadNextPage()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cacheWarningSnackbar?.dismiss()
    }
}