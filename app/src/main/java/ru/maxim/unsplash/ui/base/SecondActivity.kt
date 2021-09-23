package ru.maxim.unsplash.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

class SecondActivity : AppCompatActivity() {
    private val tag = javaClass.simpleName + " (activity lifecycle)"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag(tag).d("onCreate()")
    }

    override fun onStart() {
        super.onStart()
        Timber.tag(tag).d("onStart()")
    }

    override fun onResume() {
        super.onResume()
        Timber.tag(tag).d("onResume()")
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
}