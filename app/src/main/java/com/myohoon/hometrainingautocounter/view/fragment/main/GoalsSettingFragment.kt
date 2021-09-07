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
import com.myohoon.hometrainingautocounter.repository.AppDB
import com.myohoon.hometrainingautocounter.utils.ResUtils
import com.myohoon.hometrainingautocounter.view.MainActivity
import com.myohoon.hometrainingautocounter.view.adapter.GoalsSettingAdapter
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

    //adapter
    private lateinit var goalsAdapter: GoalsSettingAdapter

    //observable callback
    private lateinit var goCountFragmentCB: Observable.OnPropertyChangedCallback
    private lateinit var goalsCallback: Observable.OnPropertyChangedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        //view
        _binding = FragmentGoalsSettingBinding.inflate(inflater, container, false)
        binding.fg = this

        exerciseVM.currentExercise.get()?.let {
            //운동 제목 세팅
            binding.tvExerciseName.text = getString(ResUtils.getResExerciseName(it.eId))

            //목표 목록 설정
            initListAdapter()
        }

        initObservableCallback()
        return binding.root
    }

    fun btnStartExerciseClicked(view:View) {
        exerciseVM.btnStartExerciseClicked(view.id == R.id.btnStart)
    }

    private fun initListAdapter() {
        //운동 목록 초기화
        goalsAdapter = GoalsSettingAdapter(exerciseVM.currentGoals, exerciseVM)
        binding.rcvGoalsSetting.adapter = goalsAdapter

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
        goalsCallback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                goalsAdapter.notifyDataSetChanged()
            }
        }
        exerciseVM.currentGoals.addOnPropertyChangedCallback(goalsCallback)
        exerciseVM.goCountFragment.addOnPropertyChangedCallback(goCountFragmentCB)
    }

    private fun removeObservableCallback() {
        exerciseVM.currentGoals.removeOnPropertyChangedCallback(goalsCallback)
        exerciseVM.goCountFragment.removeOnPropertyChangedCallback(goCountFragmentCB)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeObservableCallback()
        _binding = null
    }
}