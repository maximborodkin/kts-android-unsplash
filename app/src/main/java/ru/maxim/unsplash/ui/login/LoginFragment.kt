package ru.maxim.unsplash.ui.login

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.flow.collect
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.FragmentLoginBinding
import ru.maxim.unsplash.ui.login.LoginViewModel.LoginState.*

class LoginFragment : Fragment(R.layout.fragment_login) {
    private val binding: FragmentLoginBinding by viewBinding(FragmentLoginBinding::bind)
    private val model: LoginViewModel by viewModels()
    private val authActivityResult = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val tokenRequest = AuthorizationResponse.fromIntent(result.data!!)
                ?.createTokenExchangeRequest()
            if (tokenRequest == null) {
                AuthorizationException.fromIntent(result.data!!)?.let { model::onAuthFailed }
            } else {
                model.onTokenRequestReceived(tokenRequest)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            loginBtn.setOnClickListener { model.startLoginPage() }
            message = getString(R.string.login_page_start_message) // Initial message
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.loginState.collect { state ->
                when(state) {
                    is Empty -> { model.startLoginPage() }
                    is Error -> { binding.message = state.message }
                    is Process -> { authActivityResult.launch(state.loginIntent) }
                    is Success -> {
                        findNavController().navigate(LoginFragmentDirections.actionLoginToMain())
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        authActivityResult.unregister()
    }
}