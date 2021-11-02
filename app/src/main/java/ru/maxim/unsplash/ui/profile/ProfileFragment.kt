package ru.maxim.unsplash.ui.profile

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.FragmentProfileBinding
import ru.maxim.unsplash.ui.feed.FeedActionsListener
import ru.maxim.unsplash.ui.profile.UserPhotosFeedFragment.Companion.userUsernameKey
import ru.maxim.unsplash.util.longToast

class ProfileFragment : Fragment(R.layout.fragment_profile), FeedActionsListener {

    private val binding by viewBinding(FragmentProfileBinding::bind)
//    private val args: ProfileFragmentArgs by navArgs()
    private val model: ProfileViewModel by viewModel { parametersOf(/*args.username*/null) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            profileSwipeRefresh.setOnRefreshListener { model.refresh() }
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
                        loadUserPhotos(state.user.username)
                    }
                    is ProfileViewModel.ProfileState.Error -> with(binding) {
                        profileSwipeRefresh.isRefreshing = false
                        isCache = true
                        state.cache?.let {
                            user = it
                            loadUserPhotos(it.username)
                        }
                        context?.longToast(state.messageRes)
                    }
                }
            }
        }
    }

    private fun loadUserPhotos(username: String) {
        /*
        * If fragment already set in the FragmentContainerView, just call the refresh method
        * Otherwise, set new instance of it
        **/
        val tag = "user_photos_feed_fragment"
        val userPhotosFragment =
            childFragmentManager.findFragmentByTag(tag) as? UserPhotosFeedFragment

        if (userPhotosFragment != null) {
            userPhotosFragment.refreshPage()
        } else {
            val userPhotosFeedFragment = UserPhotosFeedFragment().apply {
                arguments = bundleOf(userUsernameKey to username)
            }

            childFragmentManager.commit {
                setReorderingAllowed(true)
                add(R.id.profilePhotosList, userPhotosFeedFragment, tag)
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
        // TODO("Not yet implemented")
    }

    override fun openCollectionDetails(
        collectionId: String,
        transitionExtras: Array<Pair<View, String>>
    ) {
        // TODO("Not yet implemented")
    }
}