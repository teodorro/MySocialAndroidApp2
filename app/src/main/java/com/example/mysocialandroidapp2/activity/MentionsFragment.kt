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
import com.example.mysocialandroidapp2.adapter.MentionsAdapter
import com.example.mysocialandroidapp2.adapter.OnPostMentionListener
import com.example.mysocialandroidapp2.adapter.OnUserClickListener
import com.example.mysocialandroidapp2.adapter.UsersAdapter
import com.example.mysocialandroidapp2.databinding.FragmentMentionsBinding
import com.example.mysocialandroidapp2.databinding.FragmentUsersBinding
import com.example.mysocialandroidapp2.dto.Post
import com.example.mysocialandroidapp2.dto.User
import com.example.mysocialandroidapp2.enumeration.UserListType
import com.example.mysocialandroidapp2.viewmodel.PostsViewModel
import com.example.mysocialandroidapp2.viewmodel.UsersViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MentionsFragment : Fragment(), OnPostMentionListener {

    private val viewModel: PostsViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
    )

    private var fragmentBinding: FragmentMentionsBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMentionsBinding.inflate(inflater, container, false)
        fragmentBinding = binding

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_mentions)

        val adapter = MentionsAdapter(this, viewModel.edited.value)
        binding.usersList.adapter = adapter

        viewModel.loadUsers()

        viewModel.allUsers.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.users) {
            }
        }

        return binding.root
    }

    override fun onUserClicked(user: User) {
        val userIdBundle = bundleOf(POST_ID to user.id)
        findNavController().navigate(R.id.action_mentionsFragment_to_wallFragment, userIdBundle)
    }

    override fun onMention(user: User) {
        viewModel.mention(user.id)
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }

}