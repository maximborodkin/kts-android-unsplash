package ru.maxim.unsplash.ui.photo_details

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.LinearLayoutCompat.LayoutParams.MATCH_PARENT
import androidx.appcompat.widget.LinearLayoutCompat.LayoutParams.WRAP_CONTENT
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.ChangeBounds
import androidx.transition.ChangeTransform
import androidx.transition.TransitionSet
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.flow.collect
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.FragmentPhotoDetailsBinding
import ru.maxim.unsplash.databinding.ItemTagBinding
import ru.maxim.unsplash.model.Tag
import ru.maxim.unsplash.ui.photo_details.PhotoDetailsViewModel.PhotoDetailsViewModelFactory
import ru.maxim.unsplash.util.load
import ru.maxim.unsplash.util.toast

class PhotoDetailsFragment : Fragment(R.layout.fragment_photo_details) {
    private val binding by viewBinding(FragmentPhotoDetailsBinding::bind)
    private val args: PhotoDetailsFragmentArgs by navArgs()
    private val model: PhotoDetailsViewModel by viewModels {
        PhotoDetailsViewModelFactory(requireActivity().application, args.photoId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val animation =
            TransitionSet().addTransition(ChangeBounds()).addTransition(ChangeTransform())
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation

        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            photoDetailsImage.apply {
                layoutParams.apply {
                    width = args.imageWidth
                    height = args.imageHeight
                }
                setOnClickListener { photo?.urls?.full?.let { openPhotoViewer(it) } }
                load(url = args.photoUrl, blurHash = args.blurHash)
            }
            photoDetailsSwipeRefresh.setOnRefreshListener { refresh() }
            photoDetailsLikeBtn.setOnClickListener { model.setLike() }
            photoDetailsBackBtn.setOnClickListener { findNavController().popBackStack() }
            photoDetailsAuthor.setOnClickListener { photo?.user?.id?.let {openUserProfile(it)} }
            photoDetailsCacheWarning.itemCacheShownRefreshBtn.setOnClickListener { refresh() }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.photoDetailsState.collect { state ->
                when (state) {
                    is PhotoDetailsViewModel.PhotoDetailsState.Empty -> {
                        model.loadPhoto()
                    }

                    is PhotoDetailsViewModel.PhotoDetailsState.Error -> {
                        binding.photoDetailsSwipeRefresh.isRefreshing = false
                        context?.toast(state.messageRes ?: R.string.common_loading_error)
                    }

                    is PhotoDetailsViewModel.PhotoDetailsState.Refreshing -> {}

                    is PhotoDetailsViewModel.PhotoDetailsState.Success -> with(binding) {
                        photoDetailsSwipeRefresh.isRefreshing = false
                        photo = state.photo
                        isCache = state.isCache
                        state.photo.tags?.let { drawTags(it) }
                    }
                }
            }
        }
    }

    private fun drawTags(tags: ArrayList<Tag>) {
        val linearLayout = LinearLayoutCompat(requireContext()).apply {
            layoutParams = LinearLayoutCompat.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        }
        tags.map { tag ->
            ItemTagBinding.inflate(layoutInflater, linearLayout, true).apply {
                text = tag.title
                root.setOnClickListener { searchByTag(tag.title) }
            }
        }
        binding.photoDetailsTagsScroll.removeAllViews()
        binding.photoDetailsTagsScroll.addView(linearLayout)
    }

    private fun refresh() {
        binding.photoDetailsImage.load(url = args.photoUrl, blurHash = args.blurHash)
        model.refresh()
    }

    private fun searchByTag(tag: String) {
        context?.toast(tag)
    }

    private fun openUserProfile(userId: String) {
        context?.toast("Open profile with id: $userId")
    }

    private fun openPhotoViewer(photoUrl: String) {
        val action =
            PhotoDetailsFragmentDirections.actionPhotoDetailsToImageViewer(photoUrl, args.blurHash)
        findNavController().navigate(action)
    }
}