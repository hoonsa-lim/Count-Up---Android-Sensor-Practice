package com.myohoon.hometrainingautocounter.view.fragment.ranking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.myohoon.hometrainingautocounter.R

class RankingFragment : Fragment() {
    companion object{
        const val TAG = "RankingFragment"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ranking, container, false)
    }
}