package com.example.mysocialandroidapp2.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.mysocialandroidapp2.R
import com.example.mysocialandroidapp2.adapter.EventsAdapter
import com.example.mysocialandroidapp2.adapter.OnEventInteractionListener
import com.example.mysocialandroidapp2.adapter.OnPostInteractionListener
import com.example.mysocialandroidapp2.adapter.PostsAdapter
import com.example.mysocialandroidapp2.databinding.FragmentEventsBinding
import com.example.mysocialandroidapp2.databinding.FragmentPostsBinding
import com.example.mysocialandroidapp2.dto.Event
import com.example.mysocialandroidapp2.dto.Post
import com.example.mysocialandroidapp2.enumeration.UserListType
import com.example.mysocialandroidapp2.viewmodel.EventsViewModel
import com.example.mysocialandroidapp2.viewmodel.PostsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class EventsFragment : Fragment() {

    private var newPostsWasPressed: Boolean = false

    private val viewModel: EventsViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
    )

    private var _binding: FragmentEventsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentEventsBinding.inflate(inflater, container, false)

        viewModel.clearLocalTable()

        val adapter = EventsAdapter(object : OnEventInteractionListener {
            override fun onEdit(event: Event) {
                viewModel.edit(event)
            }

            override fun onLike(event: Event) {
                viewModel.likeById(event.id)
            }

            override fun onRemove(event: Event) {
                viewModel.removeById(event.id)
            }

            override fun onShowPicAttachment(event: Event) {
                viewModel.selectedPost.value = event
                //findNavController().navigate(R.id.action_feedFragment_to_picFragment)
            }

            override fun onShowUsers(event: Event, userListType: UserListType) {
                val ids = when(userListType){
                    UserListType.LIKES -> event.likeOwnerIds
                    UserListType.PARTICIPANTS -> event.participantsIds
                    UserListType.SPEAKERS -> event.speakerIds
                    else -> emptySet()
                }
                val listTypeBundle = bundleOf(USER_LIST_TYPE to userListType, POST_IDS to ids)
                findNavController().navigate(R.id.action_nav_posts_to_usersFragment, listTypeBundle)
            }
        })
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
            findNavController().navigate(R.id.action_nav_posts_to_newPostFragment)
        }

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

        binding.swiperefresh.setOnRefreshListener(adapter::refresh)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}