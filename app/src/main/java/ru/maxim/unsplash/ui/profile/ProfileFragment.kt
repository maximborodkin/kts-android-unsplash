package ru.maxim.unsplash.ui.profile

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.FragmentProfileBinding
import ru.maxim.unsplash.ui.feed.FeedActionsListener
import ru.maxim.unsplash.ui.feed.FeedFragment
import ru.maxim.unsplash.util.longToast

class ProfileFragment : Fragment(R.layout.fragment_profile), FeedActionsListener {

    private val binding by viewBinding(FragmentProfileBinding::bind)
    private val username: String? by lazy {
        val args: ProfileFragmentArgs by navArgs()
        args.username
    }
    private val model: ProfileViewModel by viewModel { parametersOf(username) }
    private var profilePagerAdapter: ProfilePagerAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            profileSwipeRefresh.setOnRefreshListener { model.refresh() }
            profileAvatar.setOnClickListener { user?.profileImage?.large }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.profileState.collect { state ->
                when (state) {
                    ProfileViewModel.ProfileState.Empty -> {
                        if (savedInstanceState == null) model.loadUser()
                    }
                    ProfileViewModel.ProfileState.Refreshing -> with(binding) {
                        isCache = false
                    }
                    is ProfileViewModel.ProfileState.Success -> with(binding) {
                        profileSwipeRefresh.isRefreshing = false
                        isCache = false
                        user = state.user
                        loadUserTabs(state.user.username)
                    }
                    is ProfileViewModel.ProfileState.Error -> with(binding) {
                        profileSwipeRefresh.isRefreshing = false
                        isCache = true
                        state.cache?.let {
                            user = it
                            loadUserTabs(state.cache.username)
                        }
                        context?.longToast(state.messageRes)
                    }
                }
            }
        }
    }

    private fun loadUserTabs(username: String) {
        if (profilePagerAdapter == null) {
            val pages = listOf(
                R.string.photos to UserPhotosFeedFragment().apply {
                    arguments = bundleOf(UserPhotosFeedFragment.userUsernameKey to username)
                },

                R.string.collections to UserCollectionsFeedFragment().apply {
                    arguments = bundleOf(UserCollectionsFeedFragment.userUsernameKey to username)
                },

                R.string.likes to UserLikesFeedFragment().apply {
                    arguments = bundleOf(UserLikesFeedFragment.userUsernameKey to username)
                }
            )

            profilePagerAdapter =
                ProfilePagerAdapter(childFragmentManager, lifecycle, pages.map { it.second })

            binding.profileViewPager.adapter = profilePagerAdapter

            TabLayoutMediator(binding.profileTabLayout, binding.profileViewPager) { tab, position ->
                tab.setText(pages[position].first)
            }.attach()
        } else {
//            profilePagerAdapter?.fragments?.forEach { page ->
//                (page as? FeedFragment)?.refresh()
//            }
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
        (parentFragment as? FeedActionsListener)
            ?.openPhotoDetails(photoId, photoUrl, blurHash, width, height, transitionExtras)
            ?: kotlin.run {
                val action = ProfileFragmentDirections
                    .actionProfileToPhotoDetails(photoId, photoUrl, width, height, blurHash)
                val extras = FragmentNavigatorExtras(*transitionExtras)
                findNavController().navigate(action, extras)
            }
    }


    override fun openCollectionDetails(
        collectionId: String,
        transitionExtras: Array<Pair<View, String>>
    ) {
        (parentFragment as? FeedActionsListener)
            ?.openCollectionDetails(collectionId, transitionExtras)
            ?: kotlin.run {
                val action = ProfileFragmentDirections
                    .actionProfileToCollectionDetails(collectionId)
                val extras = FragmentNavigatorExtras(*transitionExtras)
                findNavController().navigate(action, extras)
            }
    }

    fun openAvatarViewer(avatarUrl: String) {
        val action = ProfileFragmentDirections.actionProfileToImageViewer(avatarUrl)
        findNavController().navigate(action)
    }

    override fun openProfile(userUsername: String, transitionExtras: Array<Pair<View, String>>) {
        // Stub
    }

    override fun onDestroyView() {
        super.onDestroyView()
        profilePagerAdapter = null
    }
}