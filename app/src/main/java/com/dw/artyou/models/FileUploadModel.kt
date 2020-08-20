package com.dw.artyou.models

data class FileUploadModel(
    val ok: Boolean,
    val media: Media
)

data class Media(
    val filename: String,
    val owner: Owner,
    val caption: String,
    val size: Int,
    val `when`: String,
    val id: String
)
