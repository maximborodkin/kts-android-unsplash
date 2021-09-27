package ru.maxim.unsplash.ui.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.ActivityMainBinding
import ru.maxim.unsplash.ui.onboarding.OnboardingFirstFragmentDirections
import ru.maxim.unsplash.ui.onboarding.OnboardingSecondFragmentDirections
import ru.maxim.unsplash.ui.onboarding.OnboardingThirdFragmentDirections
import ru.maxim.unsplash.util.clearDrawables
import ru.maxim.unsplash.util.setDrawableEnd

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainNavHost) as NavHostFragment
        navController = navHostFragment.navController
        initNavigation()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBundle("navControllerState", navController?.saveState())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState.containsKey("navControllerState")) {
            navController?.restoreState(savedInstanceState.getBundle("navControllerState"))
        }
    }

    private fun checkBackButton() {
        binding?.onboardingBackBtn?.visibility =
            if (navController?.currentDestination?.id == R.id.onboardingFirstFragment)
                View.GONE
            else
                View.VISIBLE
    }

    private fun checkNextButton() {
        if (navController?.currentDestination?.id == R.id.onboardingThirdFragment) {
            binding?.onboardingNextBtn?.apply {
                text = getString(R.string.finish)
                clearDrawables()
            }
        } else {
            binding?.onboardingNextBtn?.apply {
                text = getString(R.string.next)
                setDrawableEnd(R.drawable.ic_arrow_forward_white)
            }
        }
        if (navController?.currentDestination?.id == R.id.loginFragment) {
            binding?.onboardingNavigationBar?.visibility = View.GONE
        }
    }

    private fun initNavigation() {
        checkBackButton()
        checkNextButton()

        binding?.onboardingNextBtn?.setOnClickListener {
            val action = when (navController?.currentDestination?.id) {
                R.id.onboardingFirstFragment ->
                    OnboardingFirstFragmentDirections.actionOnboardingFirstToSecond()
                R.id.onboardingSecondFragment ->
                    OnboardingSecondFragmentDirections.actionOnboardingSecondToThird()
                R.id.onboardingThirdFragment ->
                    OnboardingThirdFragmentDirections.actionOnboardingThirdToLogin()
                else -> throw IllegalStateException("Unknown fragment")
            }
            navController?.navigate(action)
            checkBackButton()
            checkNextButton()
        }

        binding?.onboardingBackBtn?.setOnClickListener {
            navController?.popBackStack()
            checkBackButton()
            checkNextButton()
        }
    }
}