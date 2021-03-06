package ru.maxim.unsplash.ui.main

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.FragmentMainBinding
import ru.maxim.unsplash.ui.feed.FeedActionsListener
import ru.maxim.unsplash.ui.profile.ProfileFragment
import ru.maxim.unsplash.ui.profile.ProfileFragmentArgs

class MainFragment : Fragment(R.layout.fragment_main), FeedActionsListener {
    private val binding by viewBinding(FragmentMainBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        val pages = listOf(
            R.string.editorial to EditorialFeedFragment(),
            R.string.collections to CollectionsFeedFragment(),
            R.string.profile to ProfileFragment().apply {
                arguments = ProfileFragmentArgs(null).toBundle()
            }
        )
        binding.mainViewPager.adapter =
            MainPagerAdapter(childFragmentManager, lifecycle, pages.map { it.second })

        TabLayoutMediator(binding.mainTabLayout, binding.mainViewPager) { tab, position ->
            tab.setText(pages[position].first)
        }.attach()

        binding.root.doOnPreDraw { startPostponedEnterTransition() }
    }

    override fun onPhotoClick(
        photoId: String,
        photoUrl: String?,
        blurHash: String?,
        width: Int,
        height: Int,
        transitionExtras: Array<Pair<View, String>>,
    ) {

        val action = MainFragmentDirections.actionMainToPhotoDetails(
            photoId = photoId,
            photoUrl = photoUrl,
            imageWidth = width,
            imageHeight = height,
            blurHash = blurHash
        )

        val extras = FragmentNavigatorExtras(*transitionExtras)
        findNavController().navigate(action, extras)
    }

    override fun onCollectionClick(
        collectionId: String,
        transitionExtras: Array<Pair<View, String>>
    ) {
        val action = MainFragmentDirections.actionMainToCollectionDetails(collectionId)
        val extras = FragmentNavigatorExtras(*transitionExtras)
        findNavController().navigate(action, extras)
    }

    override fun onProfileClick(userUsername: String, transitionExtras: Array<Pair<View, String>>) {
        val action = MainFragmentDirections.actionMainToProfile(userUsername)
        val extras = FragmentNavigatorExtras(*transitionExtras)
        findNavController().navigate(action, extras)
    }

    override fun onAddToCollectionClick(photoId: String) {
        val action = MainFragmentDirections.actionMainToAddToCollectionDialog(photoId)
        findNavController().navigate(action)
    }
}