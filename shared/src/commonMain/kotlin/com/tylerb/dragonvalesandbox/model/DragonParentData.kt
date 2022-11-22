package com.tylerb.dragonvalesandbox.model

data class DragonParentData(
    val pairings: String,
    val dragonOne: DragonData,
    val dragonTwo: DragonData,
    val percent: String,
    val offsprings: String,
    val averageBreedingTime: String,
    val maxBreedingTime: String,
)