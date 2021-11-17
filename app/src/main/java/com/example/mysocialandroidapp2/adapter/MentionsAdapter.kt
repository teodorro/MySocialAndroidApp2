package com.example.mysocialandroidapp2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mysocialandroidapp2.R
import com.example.mysocialandroidapp2.databinding.MentionItemBinding
import com.example.mysocialandroidapp2.dto.User

class MentionsAdapter(): ListAdapter<User, MentionsAdapter.MentionsViewHolder>(MentionsDiffCallback())  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MentionsViewHolder {
        var binding =
            MentionItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return MentionsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MentionsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun getItemViewType(position: Int): Int {
        return position;
    }


    class MentionsDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }


    class MentionsViewHolder(
        private val binding: MentionItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        var userId: Long = -1

        fun bind(user: User){
            binding.apply {
//                avatar.setImageUri(user.avatar)
                avatar.setImageResource(R.drawable.ic_avatar) // TODO
                username.text = user.name
                userId = user.id
            }
        }
    }

}