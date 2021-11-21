package com.example.mysocialandroidapp2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mysocialandroidapp2.R
import com.example.mysocialandroidapp2.databinding.JobItemBinding
import com.example.mysocialandroidapp2.dto.Job
import java.time.Instant


interface OnJobInteractionListener {
    fun onRemove(job: Job) {}
    fun onEdit(job: Job) {}
}

class JobsAdapter(
    private val onJobInteractionListener: OnJobInteractionListener)
    : ListAdapter<Job, JobsAdapter.JobsViewHolder>(JobsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobsViewHolder {
        var binding =
            JobItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return JobsViewHolder(binding, onJobInteractionListener)
    }

    override fun onBindViewHolder(holder: JobsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
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
        private val onJobInteractionListener: OnJobInteractionListener
    ) : RecyclerView.ViewHolder(binding.root) {

        var jobId: Long = -1

        fun bind(job: Job){
            binding.apply {
                jobname.text = job.name
                position.text = job.position
                start.text = Instant.ofEpochSecond(job.start).toString()
                if (job.finish != null)
                    finish.text = Instant.ofEpochSecond(job.finish).toString()
                else
                    finish.text = ""
                link.text = job.link

                jobId = job.id

//                menu.setGroupVisible(R.id.owned, userId == post.authorId)
                menu.setOnClickListener {
                    PopupMenu(it.context, it).apply {
                        inflate(R.menu.options_job)
                        menu.setGroupVisible(R.id.owned, true)
                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.remove -> {
                                    onJobInteractionListener.onRemove(job)
                                    true
                                }
                                R.id.edit -> {
                                    onJobInteractionListener.onEdit(job)
                                    true
                                }
                                else -> false
                            }
                        }
                    }.show()
                }
            }
        }
    }
}