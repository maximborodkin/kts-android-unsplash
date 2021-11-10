package ru.maxim.unsplash.ui.image_viewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.maxim.unsplash.ui.view.ScaleImageView
import ru.maxim.unsplash.util.load
import ru.maxim.unsplash.util.toast

class ImageViewerFragment : Fragment() {
    private val args: ImageViewerFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (args.photoUrl.isBlank()) {
            context?.toast("Unable to load this photo")
            findNavController().popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val scaleImageView = ScaleImageView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            setOnDismissListener { findNavController().popBackStack() }
            load(url = args.photoUrl, blurHash = args.blurHash)
        }
        return scaleImageView
    }
}