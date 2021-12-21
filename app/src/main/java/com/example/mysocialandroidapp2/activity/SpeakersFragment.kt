package com.example.mysocialandroidapp2.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mysocialandroidapp2.R
import com.example.mysocialandroidapp2.adapter.CheckableUsersAdapter
import com.example.mysocialandroidapp2.adapter.OnUserClickListener
import com.example.mysocialandroidapp2.databinding.FragmentSpeakersBinding
import com.example.mysocialandroidapp2.dto.User
import com.example.mysocialandroidapp2.viewmodel.EventsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SpeakersFragment : Fragment(), OnUserClickListener {

    private val viewModel: EventsViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
    )

    private var fragmentBinding: FragmentSpeakersBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_speakers)

        val binding = FragmentSpeakersBinding.inflate(inflater, container, false)
        fragmentBinding = binding

        val userId = viewModel.appAuth.authStateFlow.value.id

//        val adapter = CheckableUsersAdapter(this, userId)
        val adapter = CheckableUsersAdapter(this, userId)
        binding.usersList.adapter = adapter

        viewModel.loadUsers()

        viewModel.allUsers.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.users) {
            }
        }

        return binding.root
    }

    override fun onUserClicked(user: User) {
        val userIdBundle = bundleOf(USER_ID to user.id)
        findNavController().navigate(R.id.action_speakersFragment_to_wallFragment, userIdBundle)
    }

    override fun onCheckUser(user: User) {
        viewModel.updateSpeakers(user.id)
    }

    override fun isCheckboxVisible(user: User): Boolean {
        return viewModel.edited.value?.authorId == viewModel.appAuth.authStateFlow.value.id
//        return viewModel.edited.value?.authorId == user.id
    }

    override fun isCheckboxChecked(user: User): Boolean {
        return viewModel.edited.value?.speakerIds?.contains(user.id) ?: false
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}