package com.example.mysocialandroidapp2.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.mysocialandroidapp2.adapter.OnUserClickListener
import com.example.mysocialandroidapp2.adapter.UsersAdapter
import com.example.mysocialandroidapp2.databinding.FragmentUsersBinding
import com.example.mysocialandroidapp2.dto.User
import com.example.mysocialandroidapp2.enumeration.UserListType
import com.example.mysocialandroidapp2.viewmodel.UsersViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val USER_LIST_TYPE = "userListType"

@AndroidEntryPoint
class UsersFragment : Fragment(), OnUserClickListener {
    // TODO: Rename and change types of parameters
//    private var userListType: String? = null
    private var userListType: UserListType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            userListType = UserListType.valueOf(it.getLong(USER_LIST_TYPE)!!)
            userListType = UserListType.fromLong(it.getLong(USER_LIST_TYPE))
        }
    }

    private val viewModel: UsersViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
    )

    private var fragmentBinding: FragmentUsersBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }

}