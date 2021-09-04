package com.myohoon.hometrainingautocounter.view.fragment.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.myohoon.hometrainingautocounter.R
import com.myohoon.hometrainingautocounter.databinding.FragmentCountBinding
import com.myohoon.hometrainingautocounter.databinding.FragmentGoalsSettingBinding
import com.myohoon.hometrainingautocounter.databinding.FragmentMainBinding
import com.myohoon.hometrainingautocounter.view.adapter.ExerciseAdapter
import com.myohoon.hometrainingautocounter.viewmodel.ExerciseViewModel

class CountFragment : Fragment() {
    companion object{
        const val TAG = "CountFragment"
    }

    //view
    private var _binding: FragmentCountBinding? = null
    private val binding get() = _binding!!

    //viewModel
    private val exerciseVM by activityViewModels<ExerciseViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        //view
        _binding = FragmentCountBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}