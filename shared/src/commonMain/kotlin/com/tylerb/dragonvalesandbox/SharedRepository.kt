package com.tylerb.dragonvalesandbox

import com.squareup.sqldelight.db.SqlDriver
import com.tylerb.dragonvalesandbox.api.DragonApi
import com.tylerb.dragonvalesandbox.database.Database
import com.tylerb.dragonvalesandbox.model.DragonData
import com.tylerb.dragonvalesandbox.model.DragonParentData
import com.tylerb.dragonvalesandbox.storage.Preferences
import kotlinx.datetime.LocalDateTime
import kotlin.math.roundToInt

class SharedRepository(
    private val preferences: Preferences,
    sqlDriver: SqlDriver
) {
    private val dragonApi = DragonApi()
    private val dragonDatabase = Database(sqlDriver)

    private suspend fun needsUpdate(): Boolean = kotlin.runCatching {
        val lastUpdated = LocalDateTime.parse(dragonApi.checkForUpdates().removeSuffix("Z"))

        val needsUpdate = preferences.dateSettings.getLastUpdated()?.let { lastChecked ->
            lastUpdated > lastChecked
        } ?: true

        if (needsUpdate) {
            preferences.dateSettings.setLastUpdated(lastUpdated)
        }

        needsUpdate
    }.getOrDefault(false)


    suspend fun getDragonList(): List<DragonData> {
        if (needsUpdate()) {
            val dragons = dragonApi.getDragonList()
            dragonDatabase.addDragonsToDb(dragons)
        }

        return dragonDatabase.getDragonList()
    }

    suspend fun trendingParents(): List<String> =
        dragonApi.trendingParents()

    suspend fun parentFinder(spawn: DragonData, beb: Boolean, rift: Boolean): List<DragonParentData> {
        val dragons = dragonDatabase.getDragonList()
        var parents = dragonApi.parentFinder(spawn, beb, rift)
        val pairings = when(parents.size) {
            0 -> "Cannot be bred right now"
            1 -> "1 pairing"
            in 2..99 -> "${parents.size} pairings"
            else -> "${parents.size} pairings, showing the best 100"
        }
        if (parents.size >= 100) {
            parents = parents.subList(0, 99)
        }
        fun getDragonFromName(dragonName: String): DragonData {
            val name = dragonName.replace("_Dragon", "")
            return dragons.find { it.name == name }!!
        }
        return parents.map { parent ->
            val dragonOne = getDragonFromName(parent.dragonOne)
            val dragonTwo = getDragonFromName(parent.dragonTwo)
            val averageDhms = Calc.fmtDhms(parent.averageBreedingTime)
            val maxDhms = Calc.fmtDhms(parent.maxBreedingTime)
            val percent = (parent.percent.roundToInt() / 10.0).toString() + "%"

            DragonParentData(
                pairings = pairings,
                dragonOne = dragonOne,
                dragonTwo = dragonTwo,
                percent = percent,
                offsprings = parent.offspringTotal.toString(),
                averageBreedingTime = averageDhms,
                maxBreedingTime = maxDhms
            )

        }

    }

    fun breedCalc(
        allDragons: List<DragonData>,
        d1: DragonData,
        d2: DragonData,
        beb: Boolean,
        fast: Boolean
    ): List<DragonData>? = Calc.breedCalc(allDragons, d1, d2, beb, fast)

}