package com.example.mysocialandroidapp2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mysocialandroidapp2.R
import com.example.mysocialandroidapp2.databinding.JobItemBinding
import com.example.mysocialandroidapp2.databinding.UserItemBinding
import com.example.mysocialandroidapp2.dto.Job

class JobsAdapter()
    : ListAdapter<Job, JobsAdapter.JobsViewHolder>(JobsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobsAdapter.JobsViewHolder {
        var binding =
            UserItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return JobsAdapter.JobsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobsAdapter.JobsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, userClickListener)
    }

    override fun getItemViewType(position: Int): Int {
        return position;
    }


    class JobsDiffCallback : DiffUtil.ItemCallback<Job>() {
        override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
            return oldItem == newItem
        }
    }



    class JobsViewHolder(
        private val binding: JobItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        var userId: Long = -1

        fun bind(job: Job, clickListener: OnUserClickListener){
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