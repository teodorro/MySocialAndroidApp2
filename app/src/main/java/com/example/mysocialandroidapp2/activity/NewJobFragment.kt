package com.example.mysocialandroidapp2.activity

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mysocialandroidapp2.R
import com.example.mysocialandroidapp2.databinding.FragmentNewJobBinding
import com.example.mysocialandroidapp2.util.AndroidUtils
import com.example.mysocialandroidapp2.viewmodel.JobsViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewJobFragment : Fragment() {

    private val viewModel: JobsViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
    )

    private var fragmentBinding: FragmentNewJobBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_new_post, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                fragmentBinding?.let {
                    viewModel.changeContent(
                        it.editTextName.text.toString(),
                        it.editTextPosition.text.toString(),
                        it.editTextStart.text.toString(),
                        it.editTextFinish.text.toString(),
                        it.editTextLink.text.toString(),
                    )

                    viewModel.save()
                    AndroidUtils.hideKeyboard(requireView())
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentNewJobBinding.inflate(
            inflater,
            container,
            false
        )
        fragmentBinding = binding

        binding.editTextName.setText(viewModel.edited.value?.name)
        binding.editTextPosition.setText(viewModel.edited.value?.position)
        binding.editTextStart.setText(viewModel.edited.value?.start.toString())
        binding.editTextFinish.setText(viewModel.edited.value?.finish?.toString())
        binding.editTextLink.setText(viewModel.edited.value?.link)

        viewModel.jobCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == ImagePicker.RESULT_ERROR) {
            fragmentBinding?.let {
                Snackbar.make(it.root, ImagePicker.getError(data), Snackbar.LENGTH_LONG).show()
            }
            return
        }
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }

}