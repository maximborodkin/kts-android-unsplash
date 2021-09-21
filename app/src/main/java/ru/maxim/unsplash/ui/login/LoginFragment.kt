package ru.maxim.unsplash.ui.login

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.FragmentLoginBinding
import ru.maxim.unsplash.ui.base.MainActivity
import ru.maxim.unsplash.util.toast

class LoginFragment : Fragment() {
    private var binding: FragmentLoginBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.loginBtn?.setOnClickListener {
            if (validateEmail() == null && validatePassword() == null) {
                (activity as? MainActivity)?.navController?.navigate(
                    LoginFragmentDirections.actionLoginFragmentToMainFragment()
                )
            }
        }

        binding?.loginBtn?.isEnabled = validateEmail() == null && validatePassword() == null

        // Clear errors after text changed
        binding?.loginEmailInput?.addTextChangedListener {
            if(binding?.loginEmailLayout?.error != null) {
                binding?.loginEmailLayout?.error = null
            }
            binding?.loginBtn?.isEnabled = validateEmail() == null && validatePassword() == null
        }
        binding?.loginPasswordInput?.addTextChangedListener {
            if (binding?.loginPasswordLayout?.error != null) {
                binding?.loginPasswordLayout?.error = null
            }
            binding?.loginBtn?.isEnabled = validateEmail() == null && validatePassword() == null
        }
    }

    /**
     * @return error string or null if validation success
     **/
    private fun validateEmail(): String? {
        val email = binding?.loginEmailInput?.text.toString()
        return if (email.isBlank()) {
            getString(R.string.this_field_is_required)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            getString(R.string.invalid_email_format)
        } else {
            null
        }
    }

    private fun validatePassword(): String? {
        val password = binding?.loginPasswordInput?.text.toString()
        return if (password.isBlank()) {
            getString(R.string.this_field_is_required)
        } else if (password.length < 8) {
            getString(R.string.password_length_error)
        } else {
            null
        }
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putString("email_error", binding?.loginEmailLayout?.error.toString())
//        outState.putString("password_error", binding?.loginPasswordLayout?.error.toString())
//    }

}