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
import com.example.mysocialandroidapp2.databinding.CardPostBinding
import com.example.mysocialandroidapp2.dto.Post
import com.example.mysocialandroidapp2.enumeration.UserListType
import com.example.mysocialandroidapp2.view.loadCircleCrop

interface OnPostInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShowPicAttachment(post: Post) {}
    fun onShowUsers(post: Post, userListType: UserListType){}
}

class PostsAdapter(
    private val onInteractionListener: OnPostInteractionListener,
    private val userId: Long
) : PagingDataAdapter<Post, PostsAdapter.PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener, userId)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }



    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }



    class PostViewHolder(
        private val binding: CardPostBinding,
        private val onInteractionListener: OnPostInteractionListener,
        private val userId: Long,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.apply {
                author.text = post.author
                published.text = post.published
                content.text = post.content
                avatar.loadCircleCrop("${BuildConfig.BASE_URL}/avatars/${post.authorAvatar}")
                like.isChecked = post.likedByMe
                like.text = "${post.likeOwnerIds.size}"

                menu.visibility = View.VISIBLE

                menu.setOnClickListener {
                    PopupMenu(it.context, it).apply {
                        inflate(R.menu.options_post)
                        var a = userId
                        menu.setGroupVisible(R.id.owned, userId == post.authorId)
                        menu.setGroupVisible(R.id.all_public, true)

                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.remove -> {
                                    onInteractionListener.onRemove(post)
                                    true
                                }
                                R.id.edit -> {
                                    onInteractionListener.onEdit(post)
                                    true
                                }
                                R.id.likes -> {
                                    onInteractionListener.onShowUsers(post, UserListType.LIKES)
                                    true
                                }
                                R.id.mentions -> {
                                    onInteractionListener.onShowUsers(post, UserListType.MENTIONS)
                                    true
                                }
                                else -> false
                            }
                        }
                    }.show()
                }

                like.setOnClickListener {
                    onInteractionListener.onLike(post)
                    like.isChecked = !like.isChecked
                }

                if (post.attachment != null) {
                    val urlAttachment = "http://10.0.2.2:9999/media/${post.attachment.url}"
                    Glide.with(binding.imageViewAttachment)
                        .load(urlAttachment)
                        .placeholder(R.drawable.common_full_open_on_phone)
                        .error(R.drawable.ic_baseline_error_24)
                        .timeout(10_000)
                        .into(binding.imageViewAttachment)
                    imageViewAttachment.setOnClickListener {
                        onInteractionListener.onShowPicAttachment(post)
                    }
                }
            }
        }
    }
}