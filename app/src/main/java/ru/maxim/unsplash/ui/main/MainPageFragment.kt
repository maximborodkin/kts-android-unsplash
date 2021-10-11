package ru.maxim.unsplash.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.android.synthetic.main.fragment_main_page.*
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.FragmentMainPageBinding
import ru.maxim.unsplash.ui.main.MainFragment.ListMode
import ru.maxim.unsplash.ui.main.items.InitialLoadingItem
import ru.maxim.unsplash.ui.main.items.PageLoaderItem
import ru.maxim.unsplash.util.PaginationScrollListener
import ru.maxim.unsplash.util.autoCleared
import ru.maxim.unsplash.util.toast

class MainPageFragment : Fragment(R.layout.fragment_main_page) {
    private val binding by viewBinding(FragmentMainPageBinding::bind)
    private val model: MainViewModel by viewModels()
    private var mainRecyclerAdapter by autoCleared<MainRecyclerAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.currentListMode = (arguments?.get("list_mode") as? ListMode) ?: ListMode.Editorial
    }

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
            onRefresh = ::refreshPage
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

        with(model) {
            model.items.observe(viewLifecycleOwner) { items ->
                mainRecyclerAdapter.items = items
                mainRecyclerAdapter.notifyDataSetChanged()
                startPostponedEnterTransition()
            }

            isInitialLoading.observe(viewLifecycleOwner) {
                if (it) {
                    mainRecyclerAdapter.items = mainRecyclerAdapter.items.toMutableList().apply {
                        clear()
                        add(InitialLoadingItem)
                        mainRecyclerAdapter.notifyDataSetChanged()
                    }
                } else {
                    mainRecyclerAdapter.items = mainRecyclerAdapter.items.toMutableList().apply {
                        removeAll { item -> item is InitialLoadingItem }
                        mainRecyclerAdapter.notifyDataSetChanged()
                    }
                }
            }

            isNextPageLoading.observe(viewLifecycleOwner) {
                if (it) {
                    mainRecyclerAdapter.items = mainRecyclerAdapter.items.toMutableList().apply {
                        add(PageLoaderItem)
                        mainRecyclerAdapter.notifyItemInserted(mainRecyclerAdapter.itemCount)
                    }
                } else {
                    mainRecyclerAdapter.items = mainRecyclerAdapter.items.toMutableList().apply {
                        if (lastOrNull() is PageLoaderItem) removeLast()
                        mainRecyclerAdapter.notifyItemRemoved(mainRecyclerAdapter.itemCount)
                    }
                }
            }

            errorMessage.observe(viewLifecycleOwner) { it?.let { context?.toast(it) } }
            isRefreshing.observe(viewLifecycleOwner) { mainSwipeRefresh.isRefreshing = it }
            if (savedInstanceState == null) loadNextPage()
        }
    }

    private fun refreshPage() {
        model.refresh()
    }

    private fun onLoadNextPage() {
        model.loadNextPage()
    }
}