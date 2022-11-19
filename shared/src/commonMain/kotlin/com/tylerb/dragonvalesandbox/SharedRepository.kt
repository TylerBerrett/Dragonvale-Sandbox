package com.tylerb.dragonvalesandbox

import com.squareup.sqldelight.db.SqlDriver
import com.tylerb.dragonvalesandbox.api.DragonApi
import com.tylerb.dragonvalesandbox.database.Database
import com.tylerb.dragonvalesandbox.model.DragonData
import com.tylerb.dragonvalesandbox.model.DragonImage
import com.tylerb.dragonvalesandbox.storage.Preferences
import kotlinx.datetime.LocalDateTime

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

    suspend fun imageTest(): DragonImage {
        val r = dragonApi.getImageData(dragonDatabase.getDragonList().filter { it.name == "Fire" })
        return r.first().values.first()
    }


    suspend fun getDragonList(): List<DragonData> {
        if (needsUpdate()) {
            val dragons = dragonApi.getDragonList()
            dragonDatabase.addDragonsToDb(dragons)
        }

        return dragonDatabase.getDragonList()
    }


    fun breedCalc(
        allDragons: List<DragonData>,
        d1: DragonData,
        d2: DragonData,
        beb: Boolean,
        fast: Boolean
    ): List<DragonData>? = Calc.breedCalc(allDragons, d1, d2, beb, fast)

}