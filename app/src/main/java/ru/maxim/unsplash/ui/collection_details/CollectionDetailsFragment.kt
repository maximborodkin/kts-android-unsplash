package ru.maxim.unsplash.ui.collection_details

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.FragmentCollectionDetailsBinding
import ru.maxim.unsplash.ui.feed.FeedActionsListener
import ru.maxim.unsplash.ui.feed.FeedFragment
import ru.maxim.unsplash.ui.main.MainFragment.ListMode
import ru.maxim.unsplash.util.longToast

class CollectionDetailsFragment : Fragment(R.layout.fragment_collection_details),
    FeedActionsListener {
    private val binding by viewBinding(FragmentCollectionDetailsBinding::bind)
    private val args: CollectionDetailsFragmentArgs by navArgs()
    private val model: CollectionDetailsViewModel by viewModel { parametersOf(args.collectionId) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.collectionSwipeRefresh.setOnRefreshListener { model.refresh() }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.collectionDetailsState.collect { state ->
                when (state) {
                    CollectionDetailsViewModel.CollectionDetailsState.Empty -> {
                        if (savedInstanceState == null) model.loadCollection()
                    }

                    CollectionDetailsViewModel.CollectionDetailsState.Refreshing -> with(binding) {
                        isCache = false
                    }

                    is CollectionDetailsViewModel.CollectionDetailsState.Success -> with(binding) {
                        collectionSwipeRefresh.isRefreshing = false
                        isCache = false
                        collection = state.collection

                        loadCollectionPhotos()
                    }

                    is CollectionDetailsViewModel.CollectionDetailsState.Error -> with(binding) {
                        collectionSwipeRefresh.isRefreshing = false
                        collection = state.cache
                        isCache = true
                        context?.longToast(state.messageRes)
                        loadCollectionPhotos()
                    }
                }
            }
        }
    }


    private fun loadCollectionPhotos() {
        /*
        * If fragment already set in the FragmentContainerView, just call the refresh method
        * Otherwise, set new instance of it
        **/
        val tag = "collection_photos_list_fragment"
        val collectionPhotosFragment =
            childFragmentManager.findFragmentByTag(tag) as? FeedFragment

        if (collectionPhotosFragment != null) {
            collectionPhotosFragment.refreshPage()
        } else {
            childFragmentManager.commit {
                setReorderingAllowed(true)
                add(
                    R.id.collectionDetailsPhotosList,
                    FeedFragment::class.java,
                    bundleOf(
                        "list_mode" to ListMode.CollectionPhotos,
                        "collection_id" to args.collectionId
                    ),
                    tag
                )
            }
        }
    }

    override fun openPhotoDetails(
        photoId: String,
        photoUrl: String?,
        blurHash: String?,
        width: Int,
        height: Int,
        transitionExtras: Array<Pair<View, String>>
    ) {
        val action = CollectionDetailsFragmentDirections.actionCollectionDetailsToPhotoDetails(
            photoId = photoId,
            photoUrl = photoUrl,
            imageWidth = width,
            imageHeight = height,
            blurHash = blurHash
        )

        val extras = FragmentNavigatorExtras(*transitionExtras)
        findNavController().navigate(action, extras)
    }

    override fun openCollectionDetails(
        collectionId: String,
        transitionExtras: Array<Pair<View, String>>
    ) {
        //Stub
    }
}