package com.example.mysocialandroidapp2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mysocialandroidapp2.R
import com.example.mysocialandroidapp2.databinding.UserCheckableItemBinding
import com.example.mysocialandroidapp2.dto.Post
import com.example.mysocialandroidapp2.dto.User



class CheckableUsersAdapter(
    private val onUserClickListener: OnUserClickListener,
    private val currentUserId: Long,
    ): ListAdapter<User, CheckableUsersAdapter.CheckableUsersViewHolder>(CheckableUsersDiffCallback())  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckableUsersViewHolder {
        var binding =
            UserCheckableItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CheckableUsersViewHolder(binding, currentUserId)
    }

    override fun onBindViewHolder(holder: CheckableUsersViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onUserClickListener)
    }

    override fun getItemViewType(position: Int): Int {
        return position;
    }


    class CheckableUsersDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }


    class CheckableUsersViewHolder(
        private val binding: UserCheckableItemBinding,
        private val currentUserId: Long,
    ) : RecyclerView.ViewHolder(binding.root) {

        var userId: Long = -1

        fun bind(user: User,
                 onUserClickListener: OnUserClickListener,){
            binding.apply {
//                avatar.setImageUri(user.avatar)
                avatar.setImageResource(R.drawable.ic_avatar) // TODO
                username.text = user.name
                userId = user.id
                checked.isChecked = onUserClickListener.isCheckboxChecked(user)//post?.mentionIds?.contains(userId) ?: false
                checked.isVisible = onUserClickListener.isCheckboxVisible(user)//post?.authorId == currentUserId
                userItemLayout.isVisible = onUserClickListener.isCheckboxChecked(user)
            }

            binding.checked.setOnClickListener {
                onUserClickListener.onCheckUser(user)
            }

            itemView.setOnClickListener {
                onUserClickListener.onUserClicked(user)
            }
        }
    }

}