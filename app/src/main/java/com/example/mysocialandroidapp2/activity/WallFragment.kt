package com.example.mysocialandroidapp2.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mysocialandroidapp2.R
import com.example.mysocialandroidapp2.adapter.OnInteractionListener
import com.example.mysocialandroidapp2.adapter.PostsAdapter
import com.example.mysocialandroidapp2.databinding.FragmentPostsBinding
import com.example.mysocialandroidapp2.databinding.FragmentWallBinding
import com.example.mysocialandroidapp2.dto.Post
import com.example.mysocialandroidapp2.enumeration.UserListType
import com.example.mysocialandroidapp2.viewmodel.PostsViewModel
import com.example.mysocialandroidapp2.viewmodel.WallViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WallFragment : Fragment() {

    private val viewModel: WallViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentWallBinding.inflate(inflater, container, false)

        val adapter = WallAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onShowPicAttachment(post: Post) {
                viewModel.selectedPost.value = post
                //findNavController().navigate(R.id.action_feedFragment_to_picFragment)
            }

            override fun onShowUsers(post: Post, userListType: UserListType) {
                val ids = when(userListType){
                    UserListType.LIKES -> post.likeOwnerIds
                    UserListType.MENTIONS -> post.mentionIds
                }
                val listTypeBundle = bundleOf(USER_LIST_TYPE to userListType, POST_IDS to ids)
                findNavController().navigate(R.id.action_nav_posts_to_usersFragment, listTypeBundle)
            }
        })
        binding.recyclerView.adapter = adapter

        return binding.root
    }


}