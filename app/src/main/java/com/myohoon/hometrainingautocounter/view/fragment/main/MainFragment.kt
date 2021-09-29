package com.myohoon.hometrainingautocounter.view.fragment.main

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.myohoon.hometrainingautocounter.R
import com.myohoon.hometrainingautocounter.databinding.FragmentMainBinding
import com.myohoon.hometrainingautocounter.view.MainActivity
import com.myohoon.hometrainingautocounter.view.adapter.ExerciseAdapter
import com.myohoon.hometrainingautocounter.viewmodel.ExerciseViewModel

class MainFragment : Fragment() {
    companion object{
        const val TAG = "MainFragment"
    }

    //view
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    //adapter
    private lateinit var exerciseListAdapter: ExerciseAdapter

    //viewModel
    private val exerciseVM by activityViewModels<ExerciseViewModel>()

    //observable callback
    private lateinit var exerciseListCallback: Observable.OnPropertyChangedCallback
    private lateinit var goGoalsFragmentCallback: Observable.OnPropertyChangedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        initAD()
        initListAdapter()       //운동 목록 초기화
        initObservableCallback()
        return binding.root
    }

    private fun initAD() {
        binding.adView.loadAd(AdRequest.Builder().build())
    }

    private fun initObservableCallback() {
        exerciseListCallback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                exerciseListAdapter.notifyDataSetChanged()
            }
        }

        exerciseVM.exerciseList.addOnPropertyChangedCallback(exerciseListCallback)
    }

    private fun initListAdapter() {
        exerciseListAdapter = ExerciseAdapter(exerciseVM.exerciseList, exerciseVM)
        binding.rcvExercise.adapter = exerciseListAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeObservableCallback()
        _binding = null
    }

    private fun removeObservableCallback() {
        exerciseVM.exerciseList.removeOnPropertyChangedCallback(exerciseListCallback)
    }
}