package com.tylerb.dragonvalesandbox.database

import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import com.tylerb.dragonvalesandbox.Dragon
import com.tylerb.dragonvalesandbox.model.DragonData
import com.tylerb.dragonvalesandbox.model.DragonWeight

internal class Database(driver: SqlDriver) {

    private val listOfStringsAdapter = object : ColumnAdapter<List<String>, String> {
        override fun decode(databaseValue: String) =
            if (databaseValue.isEmpty()) {
                listOf()
            } else {
                databaseValue.split(",")
            }

        override fun encode(value: List<String>) = value.joinToString(separator = ",")
    }

    private val listOfListOfStringsAdapter = object : ColumnAdapter<List<List<String>>, String> {
        override fun decode(databaseValue: String) =
            if (databaseValue.isEmpty()) {
                listOf()
            } else {
                databaseValue.split(";").map { it.split(",") }
            }

        override fun encode(value: List<List<String>>) =
            value.joinToString(separator = ";") { it.joinToString(separator = ",") }

    }

    private val db = DragonDatabase(
        driver,
        DragonAdapter = Dragon.Adapter(
            elementsAdapter = listOfStringsAdapter,
            habitatsAdapter = listOfStringsAdapter,
            latentAdapter = listOfStringsAdapter,
            reqsAdapter = listOfListOfStringsAdapter
        )
    )

    private val dbQuery = db.dragonDatabaseQueries

    fun getDragonList(): List<DragonData> {
        return dbQuery.getDragons(::dragonMapper).executeAsList()
    }

    val dragonCount: Int
        get() = dbQuery.countDragons().executeAsOne().toInt()

    fun getDragonList(limit: Int, offset: Int): List<DragonData> {
        return dbQuery.getDragonsPaged(limit.toLong(), offset.toLong(), ::dragonMapper).executeAsList()
    }

    fun addDragonsToDb(dragons: List<DragonData>) {
        dbQuery.transaction {
            dbQuery.removeDragons()
            dragons.forEach {
                dbQuery.insertDragon(
                    available = it.available,
                    breedKey = it.breedKey,
                    breedNote = it.breedNote,
                    eggIcon = it.eggIcon,
                    elements = it.elements,
                    evolved = it.evolved,
                    events = it.events,
                    habitats = it.habitats,
                    image = it.image,
                    latent = it.latent,
                    level = it.level,
                    name = it.name,
                    opposite = it.opposite,
                    isPrimary = it.primary,
                    rarity = it.rarity,
                    reqs = it.reqs,
                    rifty = it.rifty,
                    sell = it.sell,
                    time = it.time,
                    type = it.type,
                    xBreedNote = it.xBreedNote,
                    breed = it.weight?.breed,
                    cloneBoth = it.weight?.cloneBoth,
                    cloneBothRift = it.weight?.cloneBothRift,
                    cloneBothSocial = it.weight?.cloneBothSocial,
                    cloneSingle = it.weight?.cloneSingle,
                    cloneSingleRift = it.weight?.cloneSingleRift,
                    cloneSingleSocial = it.weight?.cloneSingleSocial
                )
            }
        }
    }


    private fun dragonMapper(
        available: String,
        breedKey: String,
        breedNote: String?,
        eggIcon: String,
        elements: List<String>?,
        evolved: String?,
        events: String?,
        habitats: List<String>,
        image: String,
        latent: List<String>?,
        level: String?,
        name: String,
        opposite: String?,
        isPrimary: String?,
        rarity: String,
        reqs: List<List<String>>,
        rifty: String?,
        sell: String,
        time: Double,
        type: String,
        xBreedNote: String?,
        breed: Double?,
        cloneBoth: Double?,
        cloneBothRift: Double?,
        cloneBothSocial: Double?,
        cloneSingle: Double?,
        cloneSingleRift: Double?,
        cloneSingleSocial: Double?
    ): DragonData {
        return DragonData(
            available = available,
            breedKey = breedKey,
            breedNote = breedNote,
            eggIcon = eggIcon,
            elements = elements,
            evolved = evolved,
            events = events,
            habitats = habitats,
            image = image,
            latent = latent,
            level = level,
            name = name,
            opposite = opposite,
            primary = isPrimary,
            rarity = rarity,
            reqs = reqs,
            rifty = rifty,
            sell = sell,
            time = time,
            type = type,
            weight = DragonWeight(
                breed = breed,
                cloneBoth = cloneBoth,
                cloneBothRift = cloneBothRift,
                cloneBothSocial = cloneBothSocial,
                cloneSingle = cloneSingle,
                cloneSingleRift = cloneSingleRift,
                cloneSingleSocial = cloneSingleSocial
            ),
            xBreedNote = xBreedNote
        )
    }



    companion object {
        const val NAME = "dragon.db"
    }

}