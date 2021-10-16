package ru.maxim.unsplash.ui.photo_details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.transition.ChangeBounds
import androidx.transition.ChangeTransform
import androidx.transition.TransitionSet
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.android.synthetic.main.fragment_photo_details.*
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.FragmentPhotoDetailsBinding
import ru.maxim.unsplash.databinding.ItemTagBinding
import ru.maxim.unsplash.ui.photo_details.PhotoDetailsViewModel.PhotoDetailsViewModelFactory
import ru.maxim.unsplash.util.load
import ru.maxim.unsplash.util.toast

class PhotoDetailsFragment : Fragment(R.layout.fragment_photo_details) {
    private val binding by viewBinding(FragmentPhotoDetailsBinding::bind)
    private val args: PhotoDetailsFragmentArgs by navArgs()
    private val model: PhotoDetailsViewModel by viewModels {
        PhotoDetailsViewModelFactory(requireActivity().application, args.photoId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation =
            TransitionSet().addTransition(ChangeBounds()).addTransition(ChangeTransform())
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
        // Defer ImageView transition animation until photo is loaded
        postponeEnterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            photoDetailsImage.layoutParams.height = args.photoHeight
            photoDetailsSwipeRefresh.setOnRefreshListener { model.loadPhoto() }
            photoDetailsLikeBtn.setOnClickListener { model.setLike() }
        }

        with(model) {
            photo.observe(viewLifecycleOwner) { photo ->
                binding.photo = photo

                binding.photoDetailsImage.load(photo.urls.regular) { startPostponedEnterTransition() }

                photo.tags?.forEach { tag ->
                    ItemTagBinding
                        .inflate(layoutInflater, binding.photoDetailsTagsLayout, true)
                        .apply {
                            text = tag.title
                            root.setOnClickListener { context?.toast(text) }
                        }
                }
            }

    private fun openUserProfile(userId: String) {
        context?.toast("Open profile with id: $userId")
    }

    private fun openPhotoViewer(photoUrl: String) {
        val action = PhotoDetailsFragmentDirections.actionPhotoDetailsToImageViewer(photoUrl)
        findNavController().navigate(action)
    }
}