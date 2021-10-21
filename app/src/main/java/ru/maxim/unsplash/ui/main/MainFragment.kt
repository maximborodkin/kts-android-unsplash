package ru.maxim.unsplash.ui.main

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.FragmentMainBinding
import ru.maxim.unsplash.databinding.ItemPhotoBinding

class MainFragment : Fragment(R.layout.fragment_main) {
    private val binding by viewBinding(FragmentMainBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        val pages = listOf(
            MainPageFragment().apply { arguments = bundleOf("list_mode" to ListMode.Editorial) },
            MainPageFragment().apply { arguments = bundleOf("list_mode" to ListMode.Collections) },
            MainPageFragment().apply { arguments = bundleOf("list_mode" to ListMode.Following) }
        )
        binding.mainViewPager.adapter = MainPagerAdapter(childFragmentManager, lifecycle, pages)

        TabLayoutMediator(binding.mainTabLayout, binding.mainViewPager) { tab, position ->
            val title = when (position) {
                0 -> ListMode.Editorial.stringResource
                1 -> ListMode.Collections.stringResource
                2 -> ListMode.Following.stringResource
                else -> throw IllegalStateException("There is no title for position $position")
            }
            tab.setText(title)
        }.attach()

        binding.root.doOnPreDraw { startPostponedEnterTransition() }
    }

    fun openPhotoDetails(itemBinding: ItemPhotoBinding) {
        val photo = itemBinding.photo
        if (photo != null) {
            val action = MainFragmentDirections.actionMainToPhotoDetails(
                photoId = photo.id,
                photoUrl = photo.regular,
                imageWidth = itemBinding.itemPhotoImage.width,
                imageHeight = itemBinding.itemPhotoImage.height,
                blurHash = photo.blurHash
            )

            val extras = FragmentNavigatorExtras(
                itemBinding.itemPhotoImage to getString(R.string.photo_details_image_transition),
                itemBinding.itemPhotoLikeBtn to getString(R.string.photo_details_like_btn_transition),
                itemBinding.itemPhotoLikesCount to getString(R.string.photo_details_likes_count_transition),
                itemBinding.itemPhotoAuthorAvatar to getString(R.string.photo_details_author_avatar_transition),
                itemBinding.itemPhotoAuthorName to getString(R.string.photo_details_author_name_transition)
            )
            findNavController().navigate(action, extras)
        }
    }

    fun openCollectionDetails(collectionId: String) {
        val action = MainFragmentDirections.actionMainToCollectionDetails(collectionId)
        findNavController().navigate(action)
    }

    enum class ListMode(@StringRes val stringResource: Int) {
        Editorial(R.string.editorial),
        Collections(R.string.collections),
        Following(R.string.following)
    }
}