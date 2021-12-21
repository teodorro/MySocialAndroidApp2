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
import com.example.mysocialandroidapp2.adapter.UsersAdapter
import com.example.mysocialandroidapp2.databinding.FragmentMentionsBinding
import com.example.mysocialandroidapp2.dto.User
import com.example.mysocialandroidapp2.enumeration.UserListType
import com.example.mysocialandroidapp2.viewmodel.MentionsViewModel
import com.example.mysocialandroidapp2.viewmodel.PostsViewModel
import com.example.mysocialandroidapp2.viewmodel.UsersViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MentionsFragment : Fragment(), OnUserClickListener {

    private var userListType: UserListType? = null
    var userIds: Set<Long> = emptySet()

    private val viewModel: MentionsViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
    )

    private var fragmentBinding: FragmentMentionsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userListType = it.get(USER_LIST_TYPE) as UserListType
            userIds = it.get(USER_IDS) as Set<Long>
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_mentions)

        val binding = FragmentMentionsBinding.inflate(inflater, container, false)
        fragmentBinding = binding

        val userId = viewModel.appAuth.authStateFlow.value.id

//        val adapter = CheckableUsersAdapter(this, userId)
        val adapter = UsersAdapter(this, userId)
        binding.usersList.adapter = adapter

        viewModel.loadUsers()

//        adapter.submitList(userIds)
//        viewModel.allUsers.observe(viewLifecycleOwner) { state ->
//            adapter.submitList(state.users) {
//            }
//        }

        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.users) {
            }
        }

        return binding.root
    }

    override fun onUserClicked(user: User) {
        val userIdBundle = bundleOf(USER_ID to user.id)
        findNavController().navigate(R.id.action_mentionsFragment_to_wallFragment, userIdBundle)
    }

    override fun onCheckUser(user: User) {
        viewModel.mention(user.id)
    }

    override fun isCheckboxVisible(user: User): Boolean {
        return viewModel.edited.value?.authorId == user.id
    }

    override fun isCheckboxChecked(user: User): Boolean {
        return viewModel.edited.value?.mentionIds?.contains(user.id) ?: false
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }

}