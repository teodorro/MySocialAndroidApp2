package com.example.mysocialandroidapp2.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mysocialandroidapp2.databinding.FragmentJobsBinding
import com.example.mysocialandroidapp2.viewmodel.JobsViewModel
import com.example.mysocialandroidapp2.viewmodel.PostsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JobsFragment : Fragment() {

    private val viewModel: JobsViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
    )

    private lateinit var jobsViewModel: JobsViewModel
    private var _binding: FragmentJobsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        jobsViewModel =
//            ViewModelProvider(this).get(JobsViewModel::class.java)

        _binding = FragmentJobsBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}