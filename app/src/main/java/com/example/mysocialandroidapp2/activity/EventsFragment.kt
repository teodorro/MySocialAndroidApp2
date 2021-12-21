package com.example.mysocialandroidapp2.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.mysocialandroidapp2.R
import com.example.mysocialandroidapp2.adapter.EventsAdapter
import com.example.mysocialandroidapp2.adapter.OnEventInteractionListener
import com.example.mysocialandroidapp2.databinding.FragmentEventsBinding
import com.example.mysocialandroidapp2.dto.Event
import com.example.mysocialandroidapp2.enumeration.UserListType
import com.example.mysocialandroidapp2.viewmodel.EventsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class EventsFragment : Fragment() {

    private var newPostsWasPressed: Boolean = false

    private val viewModel: EventsViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
    )

    private var binding: FragmentEventsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_events)

        var _binding = FragmentEventsBinding.inflate(inflater, container, false)

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
                val listTypeBundle = bundleOf(USER_LIST_TYPE to userListType, USER_IDS to ids)
                if (userListType == UserListType.SPEAKERS) {
                    viewModel.edit(event)
                    findNavController().navigate(
                        R.id.action_nav_events_to_speakersFragment,
                        listTypeBundle
                    )
                } else
                    findNavController().navigate(R.id.action_nav_events_to_usersFragment, listTypeBundle)
            }

            override fun onParticipate(event: Event) {
                viewModel.participateById(event.id)
            }
        })
        _binding.recyclerView.adapter = adapter

        viewModel.dataState.observe(viewLifecycleOwner, { state ->
            _binding.progress.isVisible = state.loading
            _binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(_binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                    .show()
            }
        })

        _binding.swiperefresh.setOnRefreshListener {
            viewModel.refreshPosts()
            adapter.refresh()
            viewModel.updateWasSeen()
        }

        _binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_nav_events_to_newEventFragment)
        }

        _binding.fabNewPosts.setOnClickListener {
            viewModel.updateWasSeen()
            _binding.fabNewPosts.isVisible = false
            newPostsWasPressed = true
        }

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest(adapter::submitData)
        }

        // show indicator
        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { state ->
                _binding.swiperefresh.isRefreshing =
                    state.refresh is LoadState.Loading ||
                            state.prepend is LoadState.Loading ||
                            state.append is LoadState.Loading
            }
        }

        _binding.swiperefresh.setOnRefreshListener(adapter::refresh)

        return _binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}