package com.tylerb.dragonvalesandbox.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DragonParent(
    @SerialName("d1")
    val dragonOne: String,
    @SerialName("d2")
    val dragonTwo: String,
    @SerialName("n")
    val offspringTotal: Int,
    @SerialName("pct")
    val percent: Double,
    @SerialName("tq")
    val averageBreedingTime: Double,
    @SerialName("tz")
    val maxBreedingTime: Double
)