package com.dw.artyou.models

data class ObjectDetailModel(
    val availability: String,
    val conservation: String,
    val cover: String,
    val created_at: String,
    val creator: String,
    val date: String,
    val dimensions: Dimensionsx,
    val `entity$`: String,
    val id: String,
    val media_type: String,
    val museology: Museologyx,
    val name: String,
    val owner: Ownerxx,
    val price: Pricex,
    val serie: String,
    val target: String,
    val type: String
)

data class Dimensionsx(
    val depth: Double,
    val height: Double,
    val unit: String,
    val width: Double
)

data class Museologyx(
    val collectionType: String,
    val date: String,
    val inventory: String,
    val material: String,
    val origin: Origin,
    val parts: Int,
    val patrimonyRegister: String,
    val processNumber: List<String>,
    val propertyNumber: List<String>,
    val provenance: String,
    val summaryDescription: String,
    val technique: String,
    val title: String,
    val weight: Weightx
)

data class Origin(
    val country: String
)

data class Weightx(
    val unit: String,
    val value: Int
)

data class Ownerxx(
    val avatar: String,
    val id: String,
    val name: String,
    val type: String
)

data class Pricex(
    val currency: String,
    val value: Int
)