package com.example.mysocialandroidapp2.activity

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
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

    private var avatarUri: Uri? = null

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
            var login = binding.editTextLogin.text.toString().trimIndent()
            var password = binding.editTextPassword.text.toString()
            var name = binding.editTextName.text.toString()
            try {
                var isValid = viewModel.validateUserData(login, password, name)

                if (isValid.isEmpty()) {
                    viewModel.signUp(login, password, name, avatarUri)
                } else {
                    Toast.makeText(this.context, isValid, Toast.LENGTH_LONG)
                        .show()
                }
            } catch (e: Exception) {
                Toast.makeText(this.context, e.message, Toast.LENGTH_LONG)
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

        viewModel.photo.observe(viewLifecycleOwner) {
            avatarUri = it.uri
            if (it.uri == null)
                binding.photo.setImageResource(R.drawable.ic_avatar)
            else
                binding.photo.setImageURI(it.uri)
            binding.photo.visibility = View.VISIBLE
        }

        binding.materialButtonLoadAvatar.setOnClickListener {
            findNavController().navigate(R.id.action_regFragment_to_avatarFragment)
        }

        return binding.root
    }


}