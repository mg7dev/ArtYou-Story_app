package com.dw.artyou.models

data class ExperienceModel(
    val experiences: List<Experiences>,
    val ok: Boolean
)

data class Experiences(
    val object_id: String,
    val owner: Owner,
    val type: String,
    val visibility: String,
    val content: String,
    val active: Boolean,
    val date: String,
    val created_at: String,
    val id: String

)
