package com.dw.artyou.models

data class UserInfoModel(
    val nick: String,
    var email: String,
    var name: String,
    val active: Boolean,
    val `when`: String,
    val confirmed: Boolean,
    val social: List<Any>,
    val telephone: ArrayList<Telephone>,
    val address: List<Any>,
    val first_name: String,
    val last_name: String,
    val segment: Segment,
    val country: String,
    val killbill_account_id: String,
    var avatar: String,
    val language: String,
    val failedLoginCount: Int,
    var currency: String,
    val smtp: Smtp,
    val subscribe: Subscribe,
    val id: String
)

data class Segment(
    val id: String,
    val name: String
)

data class Smtp(
    val service: String,
    val reply: Any
)

data class Subscribe(
    val MARKETING: Boolean,
    val TRANSACIONAL: Boolean
)

data class Telephone(
    val id: String,
    val confirmed: Boolean,
    val country_code: String,
    val number: String
)