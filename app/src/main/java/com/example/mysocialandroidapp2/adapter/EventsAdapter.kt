package com.example.mysocialandroidapp2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mysocialandroidapp2.BuildConfig
import com.example.mysocialandroidapp2.R
import com.example.mysocialandroidapp2.databinding.EventItemBinding
import com.example.mysocialandroidapp2.dto.Event
import com.example.mysocialandroidapp2.enumeration.UserListType
import com.example.mysocialandroidapp2.view.loadCircleCrop


interface OnEventInteractionListener {
    fun onLike(event: Event) {}
    fun onParticipate(event: Event) {}
    fun onEdit(event: Event) {}
    fun onRemove(event: Event) {}
    fun onShowPicAttachment(event: Event) {}
    fun onShowUsers(event: Event, userListType: UserListType){}
}



class EventsAdapter (
    private val onInteractionListener: OnEventInteractionListener,
) : PagingDataAdapter<Event, EventsAdapter.EventViewHolder>(EventDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = EventItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }



    class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }



    class EventViewHolder(
        private val binding: EventItemBinding,
        private val onInteractionListener: OnEventInteractionListener,
    ) : RecyclerView.ViewHolder(binding.root) {
        private val _iAmIn = "I'm in"
        private val _takePart = "Take part"

        fun bind(event: Event) {
            binding.apply {
                author.text = event.author
                published.text = event.published
                content.text = event.content
                avatar.loadCircleCrop("${BuildConfig.BASE_URL}/avatars/${event.authorAvatar}")
                like.isChecked = event.likedByMe
                like.text = "${event.likeOwnerIds.size}"
                participate.isChecked = event.participatedByMe
                participate.text = if (event.participatedByMe) _iAmIn else _takePart

                menu.visibility = View.VISIBLE

                menu.setOnClickListener {
                    PopupMenu(it.context, it).apply {
                        inflate(R.menu.options_event)
                        menu.setGroupVisible(R.id.owned, true)

                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.remove -> {
                                    onInteractionListener.onRemove(event)
                                    true
                                }
                                R.id.edit -> {
                                    onInteractionListener.onEdit(event)
                                    true
                                }
                                R.id.likes -> {
                                    onInteractionListener.onShowUsers(event, UserListType.LIKES)
                                    true
                                }
                                R.id.participants -> {
                                    onInteractionListener.onShowUsers(event, UserListType.PARTICIPANTS)
                                    true
                                }
                                R.id.speakers -> {
                                    onInteractionListener.onShowUsers(event, UserListType.SPEAKERS)
                                    true
                                }
                                else -> false
                            }
                        }
                    }.show()
                }

                like.setOnClickListener {
                    onInteractionListener.onLike(event)
                    like.isChecked = !like.isChecked
                }

                participate.setOnClickListener {
                    onInteractionListener.onParticipate(event)
                    participate.isChecked = !participate.isChecked
                    participate.text = if (participate.isChecked) _iAmIn else _takePart
                }

                if (event.attachment != null) {
                    val urlAttachment = "http://10.0.2.2:9999/media/${event.attachment.url}"
                    Glide.with(binding.imageViewAttachment)
                        .load(urlAttachment)
                        .placeholder(R.drawable.common_full_open_on_phone)
                        .error(R.drawable.ic_baseline_error_24)
                        .timeout(10_000)
                        .into(binding.imageViewAttachment)
                    imageViewAttachment.setOnClickListener {
                        onInteractionListener.onShowPicAttachment(event)
                    }
                }
            }
        }
    }
}