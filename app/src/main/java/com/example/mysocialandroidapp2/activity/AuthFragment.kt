package com.example.mysocialandroidapp2.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.mysocialandroidapp2.R
import com.example.mysocialandroidapp2.databinding.FragmentAuthBinding
import com.example.mysocialandroidapp2.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : Fragment() {

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
        val binding = FragmentAuthBinding.inflate(
            inflater,
            container,
            false
        )

        binding.buttonLogin.setOnClickListener {
            var login = binding.editTextLogin.text.toString()
            var password = binding.editTextTextPassword.text.toString()
            if (binding.editTextLogin.text.toString().isNotBlank()) {
                try {
                    viewModel.signIn(login, password)
                } catch (e: Exception) {
                    Toast.makeText(this.context, e.message, Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                Toast.makeText(this.context, R.string.enterLoginPassword, Toast.LENGTH_LONG)
                    .show()
            }
        }

        return binding.root
    }
}