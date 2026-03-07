package com.dailyshayari.data

import kotlinx.serialization.Serializable

@Serializable
data class GoodMorningData(
    val weekday_blessings: Map<String, Blessing>,
    val messages: List<String>
)

@Serializable
data class Blessing(
    val god: String,
    val text: String
)
