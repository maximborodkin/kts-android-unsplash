package ru.maxim.unsplash.ui.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.ActivityMainBinding
import ru.maxim.unsplash.ui.onboarding.OnboardingFirstFragmentDirections
import ru.maxim.unsplash.ui.onboarding.OnboardingSecondFragmentDirections
import ru.maxim.unsplash.ui.onboarding.OnboardingThirdFragment
import ru.maxim.unsplash.ui.onboarding.OnboardingThirdFragmentDirections
import ru.maxim.unsplash.util.clearDrawables
import ru.maxim.unsplash.util.setDrawableEnd
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    var navController: NavController? = null
    private var currentFragmentId: Int? = null
    private val tag = javaClass.simpleName + " (activity lifecycle)"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainNavHost) as NavHostFragment
        navController = navHostFragment.navController
        if (savedInstanceState != null && savedInstanceState.containsKey("current_fragment_id")) {
            currentFragmentId = savedInstanceState.getInt("current_fragment_id")
        } else {
            currentFragmentId = navController?.currentDestination?.id
        }
        initNavigation()
        Timber.tag(tag).d("onCreate()")
    }

    override fun onStart() {
        super.onStart()
        Timber.tag(tag).d("onStart()")
    }

    override fun onResume() {
        super.onResume()
        Timber.tag(tag).d("onResume()")

        startActivity(Intent(this, SecondActivity::class.java))
    }

    override fun onPause() {
        super.onPause()
        Timber.tag(tag).d("onPause()")
    }

    override fun onStop() {
        super.onStop()
        Timber.tag(tag).d("onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.tag(tag).d("onDestroy()")
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