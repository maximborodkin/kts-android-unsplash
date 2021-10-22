package ru.maxim.unsplash.ui.base

import android.content.Intent
import android.os.Bundle
import android.provider.Settings.ACTION_WIRELESS_SETTINGS
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_INDEFINITE
import kotlinx.coroutines.flow.collect
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.ActivityMainBinding
import ru.maxim.unsplash.database.PreferencesManager
import ru.maxim.unsplash.util.NetworkUtils

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private var navController: NavController? = null
    private val binding by viewBinding(ActivityMainBinding::bind)
    private var noInternetSnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainNavHost) as NavHostFragment

        val graph = navHostFragment.navController.navInflater.inflate(R.navigation.nav_graph_main)
        graph.startDestination = when {
            !PreferencesManager.isOnboardingDone -> R.id.onboardingFragment
            PreferencesManager.accessToken.isNullOrBlank() -> R.id.loginFragment
            else -> R.id.mainFragment
        }
        navHostFragment.navController.graph = graph

        lifecycleScope.launchWhenStarted {
            NetworkUtils.networkStateFlow.collect { isOnline ->
                if (!isOnline) {
                    noInternetSnackbar = Snackbar
                        .make(binding.root, R.string.no_internet, LENGTH_INDEFINITE)
                        .setAction(getString(R.string.settings)) {
                            startActivity(Intent(ACTION_WIRELESS_SETTINGS))
                        }
                    noInternetSnackbar?.show()
                } else {
                    noInternetSnackbar?.dismiss()
                }
            }
        }
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
}