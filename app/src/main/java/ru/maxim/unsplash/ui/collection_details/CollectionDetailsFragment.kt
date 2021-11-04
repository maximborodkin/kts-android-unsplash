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
import ru.maxim.unsplash.ui.collection_details.CollectionPhotosFeedFragment.Companion.collectionIdKey
import ru.maxim.unsplash.ui.feed.FeedActionsListener
import ru.maxim.unsplash.ui.feed.FeedFragment
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
                        state.collection.let {
                            collection = it
                            loadCollectionPhotos(it.id)
                        }
                    }

                    is CollectionDetailsViewModel.CollectionDetailsState.Error -> with(binding) {
                        collectionSwipeRefresh.isRefreshing = false
                        isCache = true
                        state.cache?.let {
                            collection = it
                            loadCollectionPhotos(it.id)
                        }
                        context?.longToast(state.messageRes)
                    }
                }
            }
        }
    }


    private fun loadCollectionPhotos(collectionId: String) {
        /*
        * If fragment already set in the FragmentContainerView, just call the refresh method
        * Otherwise, set new instance of it
        **/
        val tag = "collection_photos_feed_fragment"
        val collectionPhotosFragment =
            childFragmentManager.findFragmentByTag(tag) as? CollectionPhotosFeedFragment

        if (collectionPhotosFragment != null) {
            (collectionPhotosFragment as FeedFragment).refresh()
        } else {
            val collectionPhotosFeedFragment = CollectionPhotosFeedFragment().apply {
                arguments = bundleOf(collectionIdKey to collectionId)
            }

            childFragmentManager.commit {
                setReorderingAllowed(true)
                add(R.id.collectionDetailsPhotosList, collectionPhotosFeedFragment, tag)
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

    override fun openProfile(userUsername: String, transitionExtras: Array<Pair<View, String>>) {
        val action =
            CollectionDetailsFragmentDirections.actionCollectionDetailsToProfile(userUsername)
        val extras = FragmentNavigatorExtras(
            binding.collectionDetailsAuthorAvatar to
                    getString(R.string.profile_user_avatar_transition),
            binding.collectionDetailsAuthorName to
                    getString(R.string.profile_user_name_transition)
        )
        findNavController().navigate(action, extras)
    }
}