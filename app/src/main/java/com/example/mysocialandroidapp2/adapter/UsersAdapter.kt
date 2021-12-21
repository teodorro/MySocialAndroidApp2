package com.example.mysocialandroidapp2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mysocialandroidapp2.R
import com.example.mysocialandroidapp2.databinding.UserItemBinding
import com.example.mysocialandroidapp2.dto.User


interface OnUserClickListener{
    fun onCheckUser(user: User)
    fun onUserClicked(user: User)
    fun isCheckboxVisible(user: User) : Boolean
    fun isCheckboxChecked(user: User) : Boolean
}


class UsersAdapter(
    private val userClickListener: OnUserClickListener,
    private val currentUserId: Long,
): ListAdapter<User, UsersAdapter.UsersViewHolder>(UsersDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        var binding =
            UserItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return UsersViewHolder(binding, currentUserId)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, userClickListener)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }



    class UsersDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }



    class UsersViewHolder(
        private val binding: UserItemBinding,
        private val currentUserId: Long,
    ) : RecyclerView.ViewHolder(binding.root) {

        var userId: Long = -1

        fun bind(user: User, clickListener: OnUserClickListener){
            binding.apply {
//                avatar.setImageUri(user.avatar)
                avatar.setImageResource(R.drawable.ic_avatar) // TODO
                username.text = user.name
                userId = user.id
                checked.isVisible = clickListener.isCheckboxVisible(user)
                checked.isChecked = clickListener.isCheckboxChecked(user)
            }

            itemView.setOnClickListener {
                clickListener.onUserClicked(user)
            }

            binding.checked.setOnClickListener {
                clickListener.onCheckUser(user)
            }
        }
    }
}
