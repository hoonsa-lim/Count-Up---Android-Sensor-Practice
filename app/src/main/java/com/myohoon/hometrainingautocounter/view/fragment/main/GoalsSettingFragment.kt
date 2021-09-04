package com.myohoon.hometrainingautocounter.view.fragment.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.fragment.app.activityViewModels
import com.myohoon.hometrainingautocounter.R
import com.myohoon.hometrainingautocounter.databinding.FragmentGoalsSettingBinding
import com.myohoon.hometrainingautocounter.databinding.FragmentMainBinding
import com.myohoon.hometrainingautocounter.view.MainActivity
import com.myohoon.hometrainingautocounter.view.adapter.ExerciseAdapter
import com.myohoon.hometrainingautocounter.viewmodel.ExerciseViewModel

class GoalsSettingFragment : Fragment() {
    companion object{
        const val TAG = "GoalsSettingFragment"
    }

    //view
    private var _binding: FragmentGoalsSettingBinding? = null
    private val binding get() = _binding!!

    //viewModel
    private val exerciseVM by activityViewModels<ExerciseViewModel>()

    //observable callback
    private lateinit var goCountFragmentCB: Observable.OnPropertyChangedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        //view
        _binding = FragmentGoalsSettingBinding.inflate(inflater, container, false)
        binding.exerciseVM = exerciseVM

        initObservableCallback()
        return binding.root
    }

    private fun initObservableCallback() {
        goCountFragmentCB = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (exerciseVM.goCountFragment.get()){
                    exerciseVM.goCountFragment.set(false)
                    MainActivity.addFragmentInMain(CountFragment(), CountFragment.TAG)
                }
            }
        }

        exerciseVM.goCountFragment.addOnPropertyChangedCallback(goCountFragmentCB)
    }

    private fun removeObservableCallback() {
        exerciseVM.goCountFragment.removeOnPropertyChangedCallback(goCountFragmentCB)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeObservableCallback()
        _binding = null
    }
}