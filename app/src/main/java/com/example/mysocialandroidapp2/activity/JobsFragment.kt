package com.example.mysocialandroidapp2.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mysocialandroidapp2.R
import com.example.mysocialandroidapp2.adapter.JobsAdapter
import com.example.mysocialandroidapp2.adapter.OnJobInteractionListener
import com.example.mysocialandroidapp2.databinding.FragmentJobsBinding
import com.example.mysocialandroidapp2.dto.Job
import com.example.mysocialandroidapp2.viewmodel.JobsViewModel
import com.example.mysocialandroidapp2.viewmodel.emptyJob
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JobsFragment : Fragment() {

    private val viewModel: JobsViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
    )

    private var _binding: FragmentJobsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentJobsBinding.inflate(inflater, container, false)

        val adapter = JobsAdapter(object : OnJobInteractionListener {
            override fun onRemove(job: Job) {
                viewModel.removeById(job.id)
            }
            override fun onEdit(job: Job) {
                viewModel.edit(job)
                findNavController().navigate(R.id.action_nav_jobs_to_newJobFragment)
            }
        })
        binding.jobsList.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.jobs) {
            }
        }

        binding.fab.setOnClickListener {
            viewModel.edit(emptyJob)
            findNavController().navigate(R.id.action_nav_jobs_to_newJobFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}