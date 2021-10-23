package ru.maxim.unsplash.ui.collection_details

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.FragmentCollectionDetailsBinding
import ru.maxim.unsplash.ui.main.MainFragment
import ru.maxim.unsplash.ui.main.MainPageFragment

class CollectionDetailsFragment : Fragment(R.layout.fragment_collection_details) {
    private val binding by viewBinding(FragmentCollectionDetailsBinding::bind)
    private val args: CollectionDetailsFragmentArgs by navArgs()
    private val model: CollectionDetailsViewModel by viewModel { parametersOf(args.collectionId) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.collectionDetailsState.collect { state ->
                when(state) {
                    CollectionDetailsViewModel.CollectionDetailsState.Empty -> {
                        if (savedInstanceState == null) model.loadCollection()
                    }

                    CollectionDetailsViewModel.CollectionDetailsState.Loading -> {
                        binding.collectionSwipeRefresh.isRefreshing = false
                    }

                    CollectionDetailsViewModel.CollectionDetailsState.Refreshing -> {}

                    is CollectionDetailsViewModel.CollectionDetailsState.Success -> {
                        binding.collectionSwipeRefresh.isRefreshing = false
                        binding.isCache = state.isCache
                        binding.collection = state.collection

                        val photosListFragment = MainPageFragment().apply {
                            arguments = bundleOf("list_mode" to MainFragment.ListMode.Editorial)
                        }
                        childFragmentManager.beginTransaction()
                            .replace(binding.collectionDetailsPhotosList.id, photosListFragment)
                            .commit()
                    }

                    is CollectionDetailsViewModel.CollectionDetailsState.Error -> {
                        binding.collectionSwipeRefresh.isRefreshing = false

                    }
                }
            }
        }
    }
}