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
import com.example.mysocialandroidapp2.adapter.OnUserClickListener
import com.example.mysocialandroidapp2.adapter.UsersAdapter
import com.example.mysocialandroidapp2.databinding.FragmentUsersBinding
import com.example.mysocialandroidapp2.dto.User
import com.example.mysocialandroidapp2.enumeration.UserListType
import com.example.mysocialandroidapp2.viewmodel.UsersViewModel
import dagger.hilt.android.AndroidEntryPoint

const val USER_LIST_TYPE = "USER_LIST_TYPE"
const val POST_IDS = "POST_IDS"
const val POST_ID = "POST_ID"

@AndroidEntryPoint
class UsersFragment : Fragment(), OnUserClickListener {
    private var userListType: UserListType? = null

    private val viewModel: UsersViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
    )

    private var fragmentBinding: FragmentUsersBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userListType = it.get(USER_LIST_TYPE) as UserListType
            viewModel.userIds = it.get(POST_IDS) as Set<Long>
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_users)

        val binding = FragmentUsersBinding.inflate(inflater, container, false)
        fragmentBinding = binding

        val adapter = UsersAdapter(this)
        binding.usersList.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.users) {
            }
        }

        return binding.root
    }

    override fun onUserClicked(user: User) {
        val userIdBundle = bundleOf(POST_ID to user.id)
        findNavController().navigate(R.id.action_usersFragment_to_wallFragment, userIdBundle)
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }

}