package com.dw.artyou.models

data class StoryesListModel(
    val storyes: List<Storye>
)

data class Storye(
    val title: String,
    val description: String,
    val objects: List<Object>,
    val owner: Owner,
    val date: String,
    val location: Location,
    val hidden: Boolean,
    val views: Int,
    val created_at: String,
    val id: String,
    val creator: Creator2,
    val like: Like
)

data class Creator(
    val cover: String,
    val id: String,
    val name: String,
    val segment: String
)

data class Like(
    val count: Int,
    val liked: Boolean
)

data class Location(
    val address: String,
    val country: Country,
    val geometry: List<Double>
)

data class Country(
    val long_name: String,
    val short_name: String
)

data class Object(
    val `object`: ObjectX,
    val text: String
)

data class ObjectX(
    val `entity$`: String,
    val name: String,
    val type: String,
    val owner: Owner,
    val cover: String,
    val id: String

)

data class Owner(
    val id: String,
    val type: String
)

data class Creator2(
    val active: Boolean,
    val address: List<Any>,
    val avatar: String,
    val confirmed: Boolean,
    val country: String,
    val currency: String,
    val email: String,
    val failedLoginCount: Int,
    val first_name: String,
    val id: String,
    val killbill_account_id: String,
    val language: String,
    val last_name: String,
    val name: String,
    val nick: String,
    val segment: String,
    val smtp: Smtp,
    val social: List<Any>,
    val subscribe: Subscribe,
    val telephone: List<Telephone>,
    val `when`: String
)


