package com.myohoon.hometrainingautocounter.view.fragment.main

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.myohoon.hometrainingautocounter.R
import com.myohoon.hometrainingautocounter.databinding.FragmentCountBinding
import com.myohoon.hometrainingautocounter.repository.enums.ExerciseType
import com.myohoon.hometrainingautocounter.repository.enums.GoalsSettingType
import com.myohoon.hometrainingautocounter.utils.ResUtils
import com.myohoon.hometrainingautocounter.utils.TimeUtils
import com.myohoon.hometrainingautocounter.view.adapter.ExerciseLogAdapter
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
        binding.exerciseVM = exerciseVM
        initView()
        initLogs()

        return binding.root
    }

    private fun initLogs() {
        binding.rcvCountLog.adapter = ExerciseLogAdapter(exerciseVM.logs)
    }

    private fun initView() {
        exerciseVM.currentExercise.get()?.let { exercise ->
            //운동 제목
            binding.tvExerciseTitle.text = getString(ResUtils.getResExerciseName(exercise.eId))

            //플랭크의 경우, 하단 우측 사용 안함.
            if (exercise.eId == ExerciseType.PLANK.ordinal){
                viewGone(binding.tvCountBottomRight)
                viewGone(binding.tvGoalsBottomRight)
            }

            //목표
            //목표 설정 안함으로 유입 시
            if (exerciseVM.isSetGoals.get().not()){
                //목표 가림
                viewGone(binding.tvGoalsCenter)
                viewGone(binding.tvGoalsBottomLeft)
                viewGone(binding.tvGoalsBottomRight)

                //하단 내용 가운데 정렬
                viewGravityCenter(binding.tvCountBottomLeft)
                viewGravityCenter(binding.tvCountBottomRight)

                return
            }

            //목표 설정으로 유입 시
            exerciseVM.currentGoals.get()?.let {
                it.forEach {
                    when(it.goalId.split("_").last().toInt()){
                        GoalsSettingType.SETS.ordinal -> {
                            if (!it.isActive) {
                                viewGone(binding.tvGoalsBottomLeft)
                                viewGravityCenter(binding.tvCountBottomLeft)
                            }else{
                                binding.tvGoalsBottomLeft.text = "${it.lastGoalsValue} ${getString(R.string.unit_set)}"
                            }
                        }
                        GoalsSettingType.REPS.ordinal -> {
                            if (exercise.eId == ExerciseType.PLANK.ordinal) return@forEach

                            if (!it.isActive) {
                                viewGone(binding.tvGoalsCenter)
                            }else{
                                binding.tvGoalsCenter.text = "${it.lastGoalsValue} ${getString(R.string.unit_count)}"
                            }
                        }
                        GoalsSettingType.TIME_LIMIT_PER_SET.ordinal -> {
                            if (exercise.eId == ExerciseType.PLANK.ordinal){
                                if (!it.isActive){
                                    viewGone(binding.tvGoalsCenter)
                                }else{
                                    binding.tvGoalsCenter.text = "${TimeUtils.secToFormatTime(it.lastGoalsValue.toInt())}"
                                }
                            }else{
                                if (!it.isActive) {
                                    viewGone(binding.tvGoalsBottomRight)
                                    viewGravityCenter(binding.tvCountBottomRight)
                                }else{
                                    binding.tvGoalsBottomRight.text = "${TimeUtils.secToFormatTime(it.lastGoalsValue.toInt())}"
                                }
                            }
                        }
                        GoalsSettingType.TIME_REST.ordinal -> {

                        }
                    }
                }
            }
        }
    }

    private fun viewGravityCenter(tv: TextView) {
        tv.gravity = Gravity.CENTER
    }

    private fun viewGone(view: View) {
        view.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}