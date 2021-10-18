package ru.maxim.unsplash.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.flow.collect
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.FragmentMainPageBinding
import ru.maxim.unsplash.ui.main.MainFragment.ListMode
import ru.maxim.unsplash.ui.main.MainViewModel.MainState.*
import ru.maxim.unsplash.ui.main.MainViewModel.MainViewModelFactory
import ru.maxim.unsplash.ui.main.items.*
import ru.maxim.unsplash.util.PaginationScrollListener
import ru.maxim.unsplash.util.autoCleared
import ru.maxim.unsplash.util.toast

class MainPageFragment : Fragment(R.layout.fragment_main_page) {
    private val binding by viewBinding(FragmentMainPageBinding::bind)
    private val model: MainViewModel by viewModels {
        MainViewModelFactory(
            requireActivity().application,
            arguments?.get("list_mode") as? ListMode ?: ListMode.Editorial
        )
    }
    private var mainRecyclerAdapter by autoCleared<MainRecyclerAdapter>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()

        val parentFragment = parentFragment as MainFragment
        mainRecyclerAdapter = MainRecyclerAdapter(
            onSetLike = model::setLike,
            onAddToCollection = model::addToCollection,
            onDownload = model::download,
            onCollectionShare = model::shareCollection,
            onOpenPhotoDetails = parentFragment::openPhotoDetails,
            onOpenCollectionDetails = parentFragment::openCollectionDetails,
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
                    Empty -> if (savedInstanceState == null) loadInitialPage()
                    Refreshing -> {
                    }

                    InitialLoading -> mainRecyclerAdapter.items = listOf(InitialLoadingItem)

                    is InitialLoadingSuccess -> {
                        binding.mainSwipeRefresh.isRefreshing = false
                        val newItems = state.items
                        if (state.isCache) newItems.add(0, CacheShownItem)
                        mainRecyclerAdapter.items = newItems
                        mainRecyclerAdapter.notifyDataSetChanged()
                    }

                    is InitialLoadingError -> {
                        binding.mainSwipeRefresh.isRefreshing = false
                        mainRecyclerAdapter.items = listOf(
                            InitialLoadingErrorItem(state.message ?: R.string.common_loading_error)
                        )
                        mainRecyclerAdapter.notifyDataSetChanged()
                        state.message?.let { context?.toast(it) }
                    }

                    is PageLoading -> {
                        with(mainRecyclerAdapter) {
                            items = items.toMutableList() + PageLoadingItem
                            notifyItemInserted(itemCount - 1)
                        }
                    }

                    is PageLoadingSuccess -> {
                        with(mainRecyclerAdapter) {
                            items = items.toMutableList().apply {
                                removeAll { it == PageLoadingItem || it is PageLoadingErrorItem }
                            } + state.itemsAdded
                            notifyDataSetChanged()
                            //notifyItemRangeInserted(itemCount - state.itemsAdded.size, itemCount - 1)
                        }
                    }

                    is PageLoadingError -> {
                        with(mainRecyclerAdapter) {
                            items = items.toMutableList().apply {
                                if (lastOrNull() is PageLoadingItem) removeLast()
                            } + PageLoadingErrorItem(state.message)
                            notifyItemChanged(itemCount - 1)
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

    private fun refreshPage() {
        if (model.mainState.value != Refreshing && model.mainState.value != InitialLoading)
            model.refresh()
        else
            binding.mainSwipeRefresh.isRefreshing = false
    }

    private fun onLoadNextPage() {
        val currentState = model.mainState.value
        if (currentState !is PageLoading ||
            currentState is InitialLoadingSuccess && !currentState.isCache)
            model.loadNextPage()
    }
}