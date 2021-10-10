package ru.maxim.unsplash.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.FragmentOnboardingBinding
import ru.maxim.unsplash.util.clearDrawables
import ru.maxim.unsplash.util.setDrawableEnd

class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {
    private val binding by viewBinding(FragmentOnboardingBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragments = arrayListOf(
            Fragment(R.layout.fragment_onboarding_first),
            Fragment(R.layout.fragment_onboarding_second),
            Fragment(R.layout.fragment_onboarding_third)
        )

        binding.onboardingPager.adapter = OnboardingPagerAdapter(
            childFragmentManager,
            lifecycle,
            fragments
        )

        binding.onboardingPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.onboardingBackBtn.isVisible = position > 0
                // Change next button to finish button on last fragment
                if (position == fragments.size - 1) {
                    binding.onboardingNextBtn.apply {
                        text = getString(R.string.finish)
                        clearDrawables()
                    }
                } else {
                    binding.onboardingNextBtn.apply {
                        text = getString(R.string.next)
                        setDrawableEnd(R.drawable.ic_arrow_forward)
                    }
                }
            }
        })

        binding.onboardingNextBtn.setOnClickListener {
            // Navigate to next fragment if its possible
            if (binding.onboardingPager.currentItem < fragments.size - 1)
                binding.onboardingPager.currentItem++
            // Finish onboarding when next button clicked on last fragment
            else if (binding.onboardingPager.currentItem == fragments.size - 1)
                finishOnboarding()
        }

        binding.onboardingBackBtn.setOnClickListener {
            if (binding.onboardingPager.currentItem > 0)
                binding.onboardingPager.currentItem--
        }

        binding.onboardingPageIndicator.attachToPager(binding.onboardingPager)
    }

    private fun finishOnboarding() {
        //TODO: Set isOnboardingFinished property to true in SharedPreferences

        findNavController().navigate(OnboardingFragmentDirections.actionOnboardingToLogin())
    }
}