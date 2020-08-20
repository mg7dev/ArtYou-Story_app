package com.dw.artyou.models

data class UserSegmentModel(
    val `data`: List<Data>
)

data class Data(
    val id: String,
    val name: String
)