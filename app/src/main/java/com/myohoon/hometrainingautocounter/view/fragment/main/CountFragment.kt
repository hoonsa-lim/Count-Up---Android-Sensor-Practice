package com.myohoon.hometrainingautocounter.view.fragment.main

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.service.carrier.CarrierMessagingService
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Adapter
import android.widget.TextView
import androidx.databinding.Observable
import androidx.fragment.app.activityViewModels
import com.myohoon.hometrainingautocounter.R
import com.myohoon.hometrainingautocounter.databinding.FragmentCountBinding
import com.myohoon.hometrainingautocounter.repository.entity.ExerciseEntity
import com.myohoon.hometrainingautocounter.repository.entity.ExerciseLogStart
import com.myohoon.hometrainingautocounter.repository.enums.ExerciseType
import com.myohoon.hometrainingautocounter.repository.enums.GoalsSettingType
import com.myohoon.hometrainingautocounter.utils.AlertUtils
import com.myohoon.hometrainingautocounter.utils.ResUtils
import com.myohoon.hometrainingautocounter.utils.TimeUtils
import com.myohoon.hometrainingautocounter.view.MainActivity
import com.myohoon.hometrainingautocounter.view.adapter.ExerciseLogAdapter
import com.myohoon.hometrainingautocounter.viewmodel.ExerciseViewModel

class CountFragment : Fragment() {
    companion object{
        const val TAG = "CountFragment"
    }

    //view
    private var _binding: FragmentCountBinding? = null
    private val binding get() = _binding!!
    private lateinit var logsAdapter: ExerciseLogAdapter

    //viewModel
    private val exerciseVM by activityViewModels<ExerciseViewModel>()

    //observable callback
    private lateinit var showRestTimerCallback: Observable.OnPropertyChangedCallback
    private lateinit var goCompleteFragmentCallback: Observable.OnPropertyChangedCallback
    private lateinit var logsCallback: Observable.OnPropertyChangedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        //view
        _binding = FragmentCountBinding.inflate(inflater, container, false)
        binding.exerciseVM = exerciseVM

        exerciseVM.currentExercise.get()?.let { exercise ->
            initView(exercise)
            initLogs(exercise)
            initSensor()
            initObservableCallback()
        }
        Log.d(TAG, "onCreateView: fragment id == ${this.hashCode()}")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //화면 꺼짐 방지
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun initObservableCallback() {
        showRestTimerCallback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (exerciseVM.showRestTimerAlert.get()){
                    exerciseVM.showRestTimerAlert.set(false)

                    val time =
                        if (exerciseVM.isActiveCurrentGoal(GoalsSettingType.TIME_REST.ordinal))
                            exerciseVM.getCurrentGoalValue(GoalsSettingType.TIME_REST.ordinal).toInt()
                        else null

                    AlertUtils.instance().showRestTimer(requireContext(), time)
                }
            }
        }
        goCompleteFragmentCallback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (exerciseVM.goCompleteFragment.get()){
                    exerciseVM.goCompleteFragment.set(false)
                    parentFragmentManager.beginTransaction().detach(this@CountFragment).commit()
//                    MainActivity.changeFragmentInMain(ResultFragment(), isAdd = false)
                }
            }
        }
        logsCallback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                logsAdapter.notifyDataSetChanged()
            }
        }

        exerciseVM.logs.addOnPropertyChangedCallback(logsCallback)
        exerciseVM.goCompleteFragment.addOnPropertyChangedCallback(goCompleteFragmentCallback)
        exerciseVM.showRestTimerAlert.addOnPropertyChangedCallback(showRestTimerCallback)
    }

    private fun removeObservableCallback(){
        exerciseVM.logs.addOnPropertyChangedCallback(logsCallback)
        exerciseVM.goCompleteFragment.removeOnPropertyChangedCallback(goCompleteFragmentCallback)
        exerciseVM.showRestTimerAlert.removeOnPropertyChangedCallback(showRestTimerCallback)
    }

    private fun initSensor() {
        requireContext()?.let {
            (it.getSystemService(Context.SENSOR_SERVICE) as SensorManager)?.let {
                exerciseVM.initSensor(it)
            }
        }
    }

    private fun initLogs(exercise: ExerciseEntity) {
        exerciseVM.createCountFragment.onNext(exercise)
        logsAdapter = ExerciseLogAdapter(exerciseVM.logs)
        binding.rcvCountLog.adapter = logsAdapter
    }

    private fun initView(exercise: ExerciseEntity) {
        //운동 제목
        binding.tvExerciseTitle.text = getString(ResUtils.getResExerciseName(exercise.eId))

        //플랭크의 경우, 하단 우측 사용 안함.
        if (exercise.eId == ExerciseType.PLANK.ordinal){
            viewGone(binding.tvCountBottomRight)
            viewGone(binding.tvGoalsBottomRight)
            viewGone(binding.tvTitleBottomRight)
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

            //set
            binding.tvTitleBottomLeft.text = getString(R.string.sets)

            //center reps
            if (exercise.eId == ExerciseType.PLANK.ordinal) return
            else binding.tvTitleCenter.text = getString(R.string.reps)

            //time limit
            if (exercise.eId == ExerciseType.PLANK.ordinal){
                binding.tvTitleCenter.text = getString(R.string.time_limit)
            }else{
                binding.tvTitleBottomRight.text = getString(R.string.time_limit)
            }

        }else{
            //목표 설정으로 유입 시
            exerciseVM.currentGoals.get()?.let {
                it.forEach {
                    when(it.goalId.split("_").last().toInt()){
                        GoalsSettingType.SETS.ordinal -> {
                            if (!it.isActive) {
                                viewGone(binding.tvGoalsBottomLeft)
                                viewGravityCenter(binding.tvCountBottomLeft)
                            }else{
                                binding.tvTitleBottomLeft.text = getString(R.string.sets)
                                binding.tvGoalsBottomLeft.text = "/ ${it.lastGoalsValue}"
                            }
                        }
                        GoalsSettingType.REPS.ordinal -> {
                            if (exercise.eId == ExerciseType.PLANK.ordinal) return@forEach

                            if (!it.isActive) {
                                viewGone(binding.tvGoalsCenter)
                            }else{
                                binding.tvTitleCenter.text = getString(R.string.reps)
                                binding.tvGoalsCenter.text = "/ ${it.lastGoalsValue}"
                            }
                        }
                        GoalsSettingType.TIME_LIMIT_PER_SET.ordinal -> {
                            if (exercise.eId == ExerciseType.PLANK.ordinal){
                                if (!it.isActive){
                                    viewGone(binding.tvGoalsCenter)
                                }else{
                                    binding.tvTitleCenter.text = getString(R.string.time_limit)
                                    binding.tvGoalsCenter.text = "/ ${TimeUtils.secToFormatTime(it.lastGoalsValue.toInt())}"
                                }
                            }else{
                                if (!it.isActive) {
                                    viewGone(binding.tvGoalsBottomRight)
                                    viewGravityCenter(binding.tvCountBottomRight)
                                }else{
                                    binding.tvTitleBottomRight.text = getString(R.string.time_limit)
                                    binding.tvGoalsBottomRight.text = "/ ${TimeUtils.secToFormatTime(it.lastGoalsValue.toInt())}"
                                }
                            }
                        }
                        GoalsSettingType.TIME_REST.ordinal -> {

                        }
                    }
                }
            }

            //title 설정
            exerciseVM.currentGoals.get()?.let {
                it.forEach {
                    when(it.goalId.split("_").last().toInt()){
                        GoalsSettingType.SETS.ordinal -> {
                            binding.tvTitleBottomLeft.text = getString(R.string.sets)
                        }
                        GoalsSettingType.REPS.ordinal -> {
                            if (exercise.eId == ExerciseType.PLANK.ordinal) return@forEach
                            binding.tvTitleCenter.text = getString(R.string.reps)
                        }
                        GoalsSettingType.TIME_LIMIT_PER_SET.ordinal -> {
                            if (exercise.eId == ExerciseType.PLANK.ordinal){
                                binding.tvTitleCenter.text = getString(R.string.time_limit)
                            }else{
                                binding.tvTitleBottomRight.text = getString(R.string.time_limit)
                            }
                        }
                        GoalsSettingType.TIME_REST.ordinal -> {

                        }
                    }
                }
            }
        }

        //event
        binding.btnRest.setOnClickListener { exerciseVM.btnRestButtonClick.onNext(Unit) }
        binding.btnFinish.setOnClickListener { requireActivity().onBackPressed() }
    }

    private fun viewGravityCenter(tv: TextView) {
        tv.gravity = Gravity.CENTER
    }

    private fun viewGone(view: View) {
        view.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeObservableCallback()
        _binding = null

        //화면 꺼짐 방지 해제
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}