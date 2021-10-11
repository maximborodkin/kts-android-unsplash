package ru.maxim.unsplash.ui.login

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.maxim.unsplash.databinding.FragmentLoginBinding
import ru.maxim.unsplash.util.autoCleared

class LoginFragment : Fragment() {
    private val binding: FragmentLoginBinding by viewBinding(FragmentLoginBinding::bind)
    private val model: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            viewModel = model
            loginBtn.setOnClickListener { model.login() }

            // Handle done or enter button from keyboard
            loginPasswordInput.setOnEditorActionListener { _, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        event.action == KeyEvent.ACTION_UP && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                    model.login()
                    true
                } else false
            }
        }
        model.isLoginAccepted.observe(viewLifecycleOwner) { if (it) navigateMainFragment() }
        return binding.root
    }

    private fun navigateMainFragment() {
        val action = LoginFragmentDirections.actionLoginToMain()
        findNavController().navigate(action)
    }
}