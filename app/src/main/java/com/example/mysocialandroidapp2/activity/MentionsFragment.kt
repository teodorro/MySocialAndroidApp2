package com.example.mysocialandroidapp2.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.mysocialandroidapp2.R
import com.example.mysocialandroidapp2.databinding.FragmentMentionsBinding
import com.example.mysocialandroidapp2.databinding.FragmentUsersBinding
import com.example.mysocialandroidapp2.viewmodel.PostsViewModel
import com.example.mysocialandroidapp2.viewmodel.UsersViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MentionsFragment : Fragment() {

    private val viewModel: PostsViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
    )

    private var fragmentBinding: FragmentMentionsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mentions, container, false)
    }

}