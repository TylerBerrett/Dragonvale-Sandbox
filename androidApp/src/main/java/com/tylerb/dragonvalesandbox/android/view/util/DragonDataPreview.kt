package com.tylerb.dragonvalesandbox.android.view.util

import com.tylerb.dragonvalesandbox.model.DragonData
import com.tylerb.dragonvalesandbox.model.DragonWeight

object DragonDataPreview {

    val blueFireDragon = DragonData(
        available = "permanent",
        breedKey = "428ee9759e8bc7e2fa5777244a19ce27",
        eggIcon = "Blue_Fire-egg.png",
        elements = listOf( "fire", "cold" ),
        habitats = listOf( "Fire", "Cold" ),
        image = "Blue_Fire.png",
        level = "9",
        name = "Blue Fire",
        rarity = "Rare",
        reqs = listOf( listOf( "fire", "cold" ) ),
        rifty = "yes",
        sell = "500000",
        time = 43200.0,
        type = "opposite",
        weight = DragonWeight(
            breed = 5.0,
            cloneBothSocial = 15.0,
            cloneSingle = 1.0,
            cloneSingleRift = 1.0,
            cloneSingleSocial = 4.0
        )

    )

}