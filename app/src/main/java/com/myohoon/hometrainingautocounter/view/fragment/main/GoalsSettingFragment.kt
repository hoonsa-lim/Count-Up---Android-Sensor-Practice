package com.myohoon.hometrainingautocounter.view.fragment.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.Observable
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.myohoon.hometrainingautocounter.R
import com.myohoon.hometrainingautocounter.databinding.FragmentGoalsSettingBinding
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
    private lateinit var snackBarCallback: Observable.OnPropertyChangedCallback

    //snackBar
    private var snackBar: Snackbar? = null

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
        context?.let {
            //운동 목록 초기화
            binding.rcvGoalsSetting.layoutManager = GridLayoutManager(it,2)
            goalsAdapter = GoalsSettingAdapter(exerciseVM.currentGoals, exerciseVM)
            binding.rcvGoalsSetting.adapter = goalsAdapter
        }
    }

    private fun initObservableCallback() {
        goCountFragmentCB = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (exerciseVM.goCountFragment.get()){
                    exerciseVM.goCountFragment.set(false)
                    MainActivity.changeFragmentInMain(CountFragment(), isAdd = false)
                }
            }
        }
        goalsCallback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                goalsAdapter.notifyDataSetChanged()
            }
        }
        snackBarCallback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                exerciseVM.snackBarMsg.get()?.let {
                    if (it != 0) {
                        showSnackBar(it)
                        exerciseVM.snackBarMsg.set(0)
                    }
                }
            }
        }
        exerciseVM.snackBarMsg.addOnPropertyChangedCallback(snackBarCallback)
        exerciseVM.currentGoals.addOnPropertyChangedCallback(goalsCallback)
        exerciseVM.goCountFragment.addOnPropertyChangedCallback(goCountFragmentCB)
    }

    private fun showSnackBar(res: Int) {
        if (snackBar == null){
            snackBar = Snackbar
                    .make(binding.constraintLayout, res, Snackbar.LENGTH_SHORT)
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }else if(snackBar!!.isShown)
            snackBar!!.dismiss()

        snackBar!!.show()
    }

    private fun removeObservableCallback() {
        exerciseVM.snackBarMsg.removeOnPropertyChangedCallback(snackBarCallback)
        exerciseVM.currentGoals.removeOnPropertyChangedCallback(goalsCallback)
        exerciseVM.goCountFragment.removeOnPropertyChangedCallback(goCountFragmentCB)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeObservableCallback()
        _binding = null
    }
}