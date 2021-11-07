package com.example.mysocialandroidapp2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mysocialandroidapp2.R
import com.example.mysocialandroidapp2.databinding.UserItemBinding
import com.example.mysocialandroidapp2.dto.User


interface OnUserClickListener{
    fun onUserClicked(user: User)
}


class UsersAdapter(
    private val userClickListener: OnUserClickListener
): ListAdapter<User, UsersAdapter.UsersViewHolder>(UsersDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        var binding =
            UserItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return UsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(item, userClickListener)
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
    ) : RecyclerView.ViewHolder(binding.root) {

        var userId: Long = -1

        fun bind(user: User, clickListener: OnUserClickListener){
            binding.apply {

//                avatar.setImageUri(user.avatar)
                avatar.setImageResource(R.drawable.ic_avatar) // TODO
                username.text = user.name
                userId = user.id
            }

            itemView.setOnClickListener {
                clickListener.onUserClicked(user)
            }
        }
    }
}
