package com.dw.artyou.models

data class ObjectCollectModel(
    val active: String,
    val created_at: String,
    val creator: Any,
    val dimensions: Any,
    val media_type: Any,
    val name: String,
    val owner: Owner,
    val type: String,
    val cover:String,
    val id: String
)
