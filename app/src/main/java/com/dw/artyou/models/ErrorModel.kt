package com.dw.artyou.models

data class ErrorModel(
    val statusCode: Int? = 0,
    val error: Any? = "",
    val message: String? = ""

)