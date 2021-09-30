package ru.maxim.unsplash.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.SimpleItemAnimator
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.FragmentMainBinding

class MainFragment : Fragment(R.layout.fragment_main) {
    private val binding by viewBinding(FragmentMainBinding::bind)
    private val model: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = MainRecyclerAdapter()
        binding.mainRecycler.adapter = adapter
        // Disable change animation until notifyItemChanged called for every event
        (binding.mainRecycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        lifecycleScope.launch {
            model.photos.observe(viewLifecycleOwner) {
                adapter.submitData(lifecycle, it)
            }
        }
    }
}