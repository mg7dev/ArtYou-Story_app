package com.dw.artyou.models


data class StoryDetailModel(
    val title: String,
    val description: String,
    val objects: List<Object>,
    val owner: Owner,
    val date: String,
    //val location: Location,
    val hidden: String,
    val views: String,
    val created_at: String,
    val id: String,
    val experiences: List<Experience>,
    val creator: Creator,
    val like: String
)


data class Experience(
    val owner: Owner,
    val type: String,
    val visibility: String,
    val content: List<Content>?,
    val contentString: String?,
    val date: String,
    val created_at: String,
    val id: String
)

data class Content(
    val filename: String,
    val caption: String
)




