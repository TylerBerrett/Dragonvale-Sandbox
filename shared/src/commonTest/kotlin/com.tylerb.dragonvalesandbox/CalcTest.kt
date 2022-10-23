package com.tylerb.dragonvalesandbox

import com.tylerb.dragonvalesandbox.model.DragonData
import com.tylerb.dragonvalesandbox.model.DragonWeight
import kotlin.test.Test
import kotlin.test.assertTrue

class CalcTest {

    private val fireDragon = DragonData(
        available = "permanent",
        breedKey = "13de03489c211e0e3bf143d6c6d9626e",
        eggIcon = "Fire-egg.png",
        elements = listOf("fire"),
        habitats = listOf("Fire"),
        image = "Fire.png",
        level = "2",
        name = "Fire",
        opposite = "cold",
        primary = "fire",
        rarity = "Primary",
        reqs = emptyList(),
        sell = "50",
        time = 300.0,
        type = "primary",
        weight = DragonWeight(
            cloneSingle = 0.0,
            cloneSingleRift = 0.0,
            cloneSingleSocial = 0.0
        )
    )

    private val airDragon = DragonData(
        available = "permanent",
        breedKey = "52ae1b32eedef31741f4f079a7d4ccf9",
        eggIcon = "Air-egg.png",
        elements = listOf("air"),
        habitats = listOf("Air"),
        image = "Air.png",
        level = "16",
        name = "Air",
        opposite = "earth",
        primary = "air",
        rarity = "Primary",
        reqs = listOf(listOf("Water", "Fire")),
        sell = "375000",
        time = 7200.0,
        type = "primary",
        weight = DragonWeight(
            breed = 100.0,
            cloneSingle = 0.0,
            cloneSingleRift = 0.0,
            cloneSingleSocial = 0.0
        )
    )

    @Test
    fun isDragonBaseElement() {
        assertTrue(isBaseElement(fireDragon.elements!!.first()))

    }

}