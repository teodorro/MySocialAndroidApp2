package com.example.mysocialandroidapp2.adapter

import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mysocialandroidapp2.BuildConfig
import com.example.mysocialandroidapp2.R
import com.example.mysocialandroidapp2.databinding.CardPostBinding
import com.example.mysocialandroidapp2.dto.Post
import com.example.mysocialandroidapp2.view.loadCircleCrop

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            avatar.loadCircleCrop("${BuildConfig.BASE_URL}/avatars/${post.authorAvatar}")
            like.isChecked = post.likedByMe
            like.text = "${post.likes}"
            menu.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    // TODO if we don't have other options just remove dots
                    menu.setGroupVisible(R.id.owned, post.ownedByMe)

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