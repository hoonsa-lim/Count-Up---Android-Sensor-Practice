package com.myohoon.hometrainingautocounter.repository.model

data class MyAlert(
    var title: String? = null,
    var desc: String? = null,
    var img: Int? = null,
    var isOneButton: Boolean = false,
    var btnPositiveText: String? = null,
    var btnNegativeText: String? = null,
    var positiveEvent: ()-> Unit? = {},
    var negativeEvent: ()-> Unit? = {},
)
