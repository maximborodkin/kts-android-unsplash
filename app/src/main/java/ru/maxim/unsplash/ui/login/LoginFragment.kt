package ru.maxim.unsplash.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.FragmentLoginBinding

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
        binding.lifecycleOwner = viewLifecycleOwner
        with(model) {
            binding.message = error.value
            binding.isAuthInProgress = isAuthInProgress.value
            openAuthPageEvent.observe(viewLifecycleOwner, ::openAuthPage)
            isAuthSuccess.observe(viewLifecycleOwner) { navigateMainFragment() }
            //error.observe(viewLifecycleOwner) { binding.loginMessage.text = it }
            startLoginPage()
            binding.loginBtn.setOnClickListener { startLoginPage() }
        }
    }

    private fun openAuthPage(intent: Intent) {
        authActivityResult.launch(intent)
    }

    private fun navigateMainFragment() {
        val action = LoginFragmentDirections.actionLoginToMain()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        authActivityResult.unregister()
    }
}