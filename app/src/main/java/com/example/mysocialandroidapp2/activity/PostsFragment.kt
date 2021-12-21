package com.example.mysocialandroidapp2.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.work.*
import com.example.mysocialandroidapp2.R
import com.example.mysocialandroidapp2.adapter.OnPostInteractionListener
import com.example.mysocialandroidapp2.adapter.PostsAdapter
import com.example.mysocialandroidapp2.databinding.FragmentPostsBinding
import com.example.mysocialandroidapp2.dto.Post
import com.example.mysocialandroidapp2.dto.User
import com.example.mysocialandroidapp2.enumeration.UserListType
import com.example.mysocialandroidapp2.model.UsersFeedModel
import com.example.mysocialandroidapp2.viewmodel.PostsViewModel
import com.example.mysocialandroidapp2.viewmodel.emptyPost
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class PostsFragment : Fragment() {

    @Inject
    lateinit var workManager: WorkManager

    private var newPostsWasPressed: Boolean = false

    private val viewModel: PostsViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
    )

    private var fragmentBinding: FragmentPostsBinding? = null

    private lateinit var allUsers: List<User>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPostsBinding.inflate(inflater, container, false)

        fragmentBinding = binding

        viewModel.loadPosts()
        val userId = viewModel.appAuth.authStateFlow.value.id

        val adapter = PostsAdapter(
            object : OnPostInteractionListener {
                override fun onEdit(post: Post) {
                    viewModel.edit(post)
                    findNavController().navigate(R.id.action_nav_posts_to_newPostFragment)
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

//                    var a =
//                        if (post.authorId != viewModel.appAuth.authStateFlow.value.id) post.mentionIds
//                        else (if (viewModel.allUsers.value != null)
//                            viewModel.allUsers.value!!.users.map { x -> x.id }.toMutableSet()
//                            else emptySet())

                    val ids = when (userListType) {
                        UserListType.LIKES -> post.likeOwnerIds
//                        UserListType.MENTIONS -> post.mentionIds
                        UserListType.MENTIONS ->
                            if (post.authorId != viewModel.appAuth.authStateFlow.value.id)
                                post.mentionIds
                            else (if (allUsers != null)
                                allUsers.map { x -> x.id }.toMutableSet()
                                else emptySet())
                        else -> emptySet()
                    }
                    val listTypeBundle = bundleOf(USER_LIST_TYPE to userListType, USER_IDS to ids)
                    if (userListType == UserListType.MENTIONS) {
                        viewModel.edit(post)
                        findNavController().navigate(
                            R.id.action_nav_posts_to_mentionsFragment,
                            listTypeBundle
                        )
                    } else
                        findNavController().navigate(
                            R.id.action_nav_posts_to_usersFragment,
                            listTypeBundle
                        )
                }
            },
            userId
        )
        binding.recyclerView.adapter = adapter


        viewModel.dataState.observe(viewLifecycleOwner, { state ->
            binding.progress.isVisible = state.loading
            binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                    .show()
            }
        })

        binding.swiperefresh.setOnRefreshListener {
            viewModel.refreshPosts()
            adapter.refresh()
            viewModel.updateWasSeen()
        }

        binding.fab.setOnClickListener {
            viewModel.edit(emptyPost)
            findNavController().navigate(R.id.action_nav_posts_to_newPostFragment)
        }
        binding.fab.isVisible = viewModel.appAuth.authStateFlow.value.id.toInt() > 0

        binding.fabNewPosts.setOnClickListener {
            viewModel.updateWasSeen()
            binding.fabNewPosts.isVisible = false
            newPostsWasPressed = true
        }

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest(adapter::submitData)
        }

        // show indicator
        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { state ->
                binding.swiperefresh.isRefreshing =
                    state.refresh is LoadState.Loading ||
                            state.prepend is LoadState.Loading ||
                            state.append is LoadState.Loading
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.allUsers.collectLatest { x ->
                getAllUsers(x) }
        }

        binding.swiperefresh.setOnRefreshListener(adapter::refresh)

        adapter.refresh()

        return binding.root
    }

    private fun getAllUsers(usersFeedModel: UsersFeedModel){
        allUsers = usersFeedModel.users
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }
}