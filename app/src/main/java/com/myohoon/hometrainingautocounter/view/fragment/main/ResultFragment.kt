package com.myohoon.hometrainingautocounter.view.fragment.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.myohoon.hometrainingautocounter.databinding.FragmentResultBinding
import com.myohoon.hometrainingautocounter.viewmodel.ExerciseViewModel

class ResultFragment : Fragment() {
    companion object{
        const val TAG = "ResultFragment"
    }

    //view
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    //viewModel
    private val exerciseVM by activityViewModels<ExerciseViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        //view
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        binding.exerciseVM = exerciseVM
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}