package ru.maxim.unsplash.ui.main

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.FragmentMainBinding

class MainFragment : Fragment(R.layout.fragment_main) {
    private val binding by viewBinding(FragmentMainBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
    }

    enum class ListMode(@StringRes val stringResource: Int) {
        Editorial(R.string.editorial),
        Collections(R.string.collections),
        Following(R.string.following)
    }
}