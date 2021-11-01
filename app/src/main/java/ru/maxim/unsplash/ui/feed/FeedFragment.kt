package ru.maxim.unsplash.ui.feed

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.FragmentMainPageBinding
import ru.maxim.unsplash.ui.main.MainFragment.ListMode
import ru.maxim.unsplash.ui.feed.FeedViewModel.MainState.*
import ru.maxim.unsplash.ui.feed.items.InitialLoadingErrorItem
import ru.maxim.unsplash.ui.feed.items.InitialLoadingItem
import ru.maxim.unsplash.ui.feed.items.PageLoadingErrorItem
import ru.maxim.unsplash.ui.feed.items.PageLoadingItem
import ru.maxim.unsplash.util.PaginationScrollListener
import ru.maxim.unsplash.util.autoCleared
import ru.maxim.unsplash.util.longToast
import ru.maxim.unsplash.util.toast
import timber.log.Timber

class FeedFragment : Fragment(R.layout.fragment_main_page) {
    private val binding by viewBinding(FragmentMainPageBinding::bind)
    private val model: FeedViewModel by viewModel {
        val mode = arguments?.get("list_mode")
        val collectionId = arguments?.getString("collection_id")

        if (mode !is ListMode)
            throw IllegalArgumentException("list_mode must be provided to MainPageFragment")
        else if (mode == ListMode.CollectionPhotos && collectionId.isNullOrBlank())
            throw IllegalArgumentException("collectionId must be provided for CollectionPhotos mode")
        else
            parametersOf(mode, collectionId)
    }
    private var mainRecyclerAdapter by autoCleared<FeedRecyclerAdapter>()
    private var cacheWarningSnackbar: Snackbar? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()

        mainRecyclerAdapter = FeedRecyclerAdapter(
            onSetLike = model::setLike,
            onAddToCollection = model::addToCollection,
            onDownload = model::download,
            onCollectionShare = model::shareCollection,
            feedActionsListener = parentFragment as FeedActionsListener,
            onRefresh = ::loadInitialPage,
            onRetry = ::retryLoading
        )

        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            mainRecycler.adapter = mainRecyclerAdapter
            mainRecycler.addOnScrollListener(
                PaginationScrollListener(
                    mainRecycler.layoutManager as LinearLayoutManager,
                    ::onLoadNextPage
                )
            )
            mainSwipeRefresh.setOnRefreshListener(::refreshPage)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.mainState.collect { state ->
                when (state) {
                    Empty -> {
                        Timber.tag("MainState").d("Empty")
                        if (savedInstanceState == null) loadInitialPage()
                    }

                    Refreshing -> {
                        Timber.tag("MainState").d("Refreshing")
                    }

                    InitialLoading -> {
                        Timber.tag("MainState").d("InitialLoading")

                        mainRecyclerAdapter.items = listOf(InitialLoadingItem)
                    }

                    is InitialLoadingSuccess -> {
                        Timber.tag("MainState")
                            .d("InitialLoadingSuccess, isCache=${state.isCache}, items=${state.items.size}")

                        binding.mainSwipeRefresh.isRefreshing = false
                        val newItems = state.items
                        if (!state.isCache) cacheWarningSnackbar?.dismiss()
                        mainRecyclerAdapter.items = newItems
                        mainRecyclerAdapter.notifyDataSetChanged()
                    }

                    is InitialLoadingError -> {
                        Timber.tag("MainState")
                            .d("InitialLoadingError, cause=${state.message?.let { getString(it) }}, items=${state.items?.size ?: 0}")

                        binding.mainSwipeRefresh.isRefreshing = false
                        val cache = state.items
                        if (cache != null && cache.isNotEmpty()) {
                            cacheWarningSnackbar =
                                Snackbar
                                    .make(
                                        binding.mainRecycler,
                                        R.string.cached_data_shown,
                                        Snackbar.LENGTH_INDEFINITE
                                    )
                                    .setAction(R.string.refresh) { refreshPage() }
                                    .also { it.show() }

                            mainRecyclerAdapter.items = cache
                        } else {
                            mainRecyclerAdapter.items = listOf(
                                InitialLoadingErrorItem(
                                    state.message ?: R.string.common_loading_error
                                )
                            )
                        }
                        mainRecyclerAdapter.notifyDataSetChanged()
                        state.message?.let { context?.longToast(it) }
                    }

                    is PageLoading -> {
                        Timber.tag("MainState")
                            .d("PageLoading, itemsNow=${mainRecyclerAdapter.items.size}")

                        with(mainRecyclerAdapter) {
                            items = items.toMutableList() + PageLoadingItem
//                            notifyItemInserted(itemCount - 1)
                            binding.mainRecycler.post { notifyDataSetChanged() }
                        }
                    }

                    is PageLoadingSuccess -> {

                        with(mainRecyclerAdapter) {
//                            items.lastOrNull { it == PageLoadingItem || it is PageLoadingErrorItem }
//                                ?.let {
//                                    items = items.toMutableList().apply { removeLast() }
//                                    notifyItemRemoved(items.size)
//                                }
                            Timber.tag("MainState")
                                .d("PageLoadingSuccess, itemsNow=${mainRecyclerAdapter.items.size}, newItems=${state.itemsAdded.size}")


                            if (state.itemsAdded.isNotEmpty()) {
                                items = state.itemsAdded
                                binding.mainRecycler.post { notifyDataSetChanged() }
                            }
//                            items = items.toMutableList().apply {
//                                removeAll { it == PageLoadingItem || it is PageLoadingErrorItem }
//                            } + state.itemsAdded
                            //notifyDataSetChanged()
                            //notifyItemRangeInserted(itemCount - state.itemsAdded.size, itemCount - 1)
                        }
                        Timber.tag("MainState")
                            .d("PageLoadingSuccess, itemsAfterAdding=${mainRecyclerAdapter.items.size}")
                    }

                    is PageLoadingError -> {
                        Timber.tag("MainState")
                            .d("PageLoadingError, cause=${state.message?.let { getString(it) }}")

                        with(mainRecyclerAdapter) {
                            items = items.toMutableList().apply {
                                if (lastOrNull() is PageLoadingItem) removeLast()
                            } + PageLoadingErrorItem(state.message)
                            //notifyItemChanged(itemCount - 1)
                            notifyDataSetChanged()
                        }
                    }

                    is SetLikeSuccess -> mainRecyclerAdapter.notifyItemChanged(state.itemPosition)
                    is SetLikeError -> {
                        state.message?.let { context?.toast(it) }
                    }
                }
            }
        }
    }

    private fun retryLoading() {
        if (model.mainState.value is PageLoadingError)
            model.retryPageLoading()
    }

    private fun loadInitialPage() {
        if (model.mainState.value != InitialLoading)
            model.loadNextPage(1)
    }

    fun refreshPage() {
        val currentState = model.mainState.value
        if (currentState != Refreshing && currentState != InitialLoading)
            model.refresh()
        else
            binding.mainSwipeRefresh.isRefreshing = false
    }

    private fun onLoadNextPage() {
        val currentState = model.mainState.value
        /* Start page loading only if another page loading not running already and
         * previous loading is not failed or
         * if initial loading fully finished and has final data (not temporary cache)
         * */
        if (currentState !is PageLoading &&
            currentState !is PageLoadingError ||
            currentState is InitialLoadingSuccess && !currentState.isCache
        ) {
            model.loadNextPage()
        }
    }
}