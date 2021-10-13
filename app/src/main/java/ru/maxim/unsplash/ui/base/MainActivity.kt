package ru.maxim.unsplash.ui.base

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.ActivityMainBinding
import ru.maxim.unsplash.util.NetworkUtils

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private var navController: NavController? = null
    private val binding by viewBinding(ActivityMainBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainNavHost) as NavHostFragment
        navController = navHostFragment.navController
        lifecycleScope.launchWhenStarted {
            NetworkUtils.networkStateFlow.collect {
                if (!it)
                    Snackbar
                        .make(binding.root, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.settings)) {
                            startActivity(Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS))
                        }
                        .show()

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