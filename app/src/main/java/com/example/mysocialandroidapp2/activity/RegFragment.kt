package com.example.mysocialandroidapp2.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.mysocialandroidapp2.R
import com.example.mysocialandroidapp2.databinding.FragmentRegBinding
import com.example.mysocialandroidapp2.util.AndroidUtils
import com.example.mysocialandroidapp2.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegFragment : Fragment() {
    private val viewModel: AuthViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRegBinding.inflate(
            inflater,
            container,
            false
        )

        binding.buttonSignUp.setOnClickListener {
            var login = binding.editTextLogin.text.toString()
            var password = binding.editTextPassword.text.toString()
            var name = binding.editTextName.text.toString()
            if (binding.editTextLogin.text.toString().isNotBlank()) {
                try {
                    viewModel.signUp(login, password, name)
                } catch (e: Exception) {
                    Toast.makeText(this.context, e.message, Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                Toast.makeText(this.context, R.string.enterLoginPassword, Toast.LENGTH_LONG)
                    .show()
            }
        }

        viewModel.data.observe(viewLifecycleOwner) {
            if (viewModel.authenticated) {
                AndroidUtils.hideKeyboard(requireView())
                findNavController().navigateUp()
            } else {
                if (binding.editTextLogin.text.toString().isNotBlank())
                    Toast.makeText(this.context, R.string.errorLoginPassword, Toast.LENGTH_LONG)
                        .show()
            }
        }

        return binding.root
    }

}