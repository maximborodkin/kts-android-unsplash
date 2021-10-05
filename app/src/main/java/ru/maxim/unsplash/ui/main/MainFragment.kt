package ru.maxim.unsplash.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.FragmentMainBinding
import ru.maxim.unsplash.ui.main.items.InitialLoaderItem
import ru.maxim.unsplash.ui.main.items.PageLoaderItem
import ru.maxim.unsplash.ui.main.items.PhotoItem
import ru.maxim.unsplash.util.PaginationScrollListener
import ru.maxim.unsplash.util.autoCleared
import ru.maxim.unsplash.util.toast

class MainFragment : Fragment(R.layout.fragment_main) {
    private val binding by viewBinding(FragmentMainBinding::bind)
    private val model: MainViewModel by viewModels()
    private var mainRecyclerAdapter by autoCleared<MainRecyclerAdapter>()
    private val currentListMode: ListMode = ListMode.Editorial

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        mainRecyclerAdapter = MainRecyclerAdapter(
            model::setLike,
            model::addToCollection,
            model::download,
            model::shareCollection)

        with(binding.mainRecycler) {
            adapter = mainRecyclerAdapter
            addOnScrollListener(
                PaginationScrollListener(layoutManager as LinearLayoutManager, ::onLoadNextPage)
            )
            // Disable change animation until notifyItemChanged called for every event
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        with(model) {
            photos.observe(viewLifecycleOwner) { photos ->
                mainRecyclerAdapter.items = photos.map {
                    PhotoItem(
                        id = it.id,
                        width = it.width,
                        height = it.height,
                        source = it.urls.full,
                        thumbnail = it.urls.thumb,
                        likesCount = it.likes,
                        likedByUser = it.likedByUser,
                        authorName = it.user.name,
                        authorAvatar = it.user.profileImage.small)
                }
                mainRecyclerAdapter.notifyDataSetChanged()
            }

            isInitialLoading.observe(viewLifecycleOwner) {
                if (it) {
                    mainRecyclerAdapter.items = mainRecyclerAdapter.items.toMutableList().apply {
                        clear()
                        add(InitialLoaderItem())
                        mainRecyclerAdapter.notifyDataSetChanged()
                    }
                } else {
                    mainRecyclerAdapter.items = mainRecyclerAdapter.items.toMutableList().apply {
                        removeAll { item -> item is InitialLoaderItem }
                        mainRecyclerAdapter.notifyDataSetChanged()
                    }
                }
            }

            isNextPageLoading.observe(viewLifecycleOwner) {
                if (it) {
                    mainRecyclerAdapter.items = mainRecyclerAdapter.items.toMutableList().apply {
                        add(PageLoaderItem())
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
            loadNextPage()
        }
    }

    private fun onLoadNextPage() {
        model.loadNextPage()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mainRecycler.adapter = null
    }

    enum class ListMode {
        Editorial,
        Collections,
        Following
    }
}