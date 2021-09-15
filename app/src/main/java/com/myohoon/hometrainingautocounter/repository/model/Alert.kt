package com.myohoon.hometrainingautocounter.repository.model

import com.myohoon.hometrainingautocounter.R

data class Alert(
    var title: Int,
    var desc: Int,
    var img: Int? = null,
    var isOneButton: Boolean = false,
    var btnPositiveText: Int? = R.string.ok,
    var btnNegativeText: Int? = R.string.no,
    var positiveEvent: (()-> Unit)? = null,
    var negativeEvent: (()-> Unit)? = null,
)
