package com.myohoon.hometrainingautocounter.view.fragment.main

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.myohoon.hometrainingautocounter.R
import com.myohoon.hometrainingautocounter.databinding.FragmentMainBinding
import com.myohoon.hometrainingautocounter.view.adapter.ExerciseAdapter
import com.myohoon.hometrainingautocounter.viewmodel.ExerciseViewModel

class MainFragment : Fragment() {
    companion object{
        const val TAG = "MainFragment"
    }

    //view
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    //viewModel
    private val exerciseVM by activityViewModels<ExerciseViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        //운동 목록 초기화
        binding.rcvExercise.adapter = ExerciseAdapter(exerciseVM.exerciseList)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}