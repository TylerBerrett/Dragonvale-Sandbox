package com.tylerb.dragonvalesandbox

import com.tylerb.dragonvalesandbox.api.DragonApi
import com.tylerb.dragonvalesandbox.model.DragonData
import com.tylerb.dragonvalesandbox.storage.Preferences
import kotlinx.datetime.LocalDateTime

class SharedRepository(
    private val preferences: Preferences
) {
    private val dragonApi = DragonApi()

    private suspend fun needsUpdate(): Boolean {
        val lastUpdated = LocalDateTime.parse(dragonApi.checkForUpdates().removeSuffix("Z"))

        val needsUpdate = preferences.dateSettings.getLastUpdated()?.let { lastChecked ->
            lastUpdated > lastChecked
        } ?: true

        if (needsUpdate) {
            preferences.dateSettings.setLastUpdated(lastUpdated)
        }

        return needsUpdate
    }


    suspend fun getDragonList(): List<DragonData> =
        dragonApi.getDragonList()

    fun breedCalc(
        allDragons: List<DragonData>,
        d1: DragonData,
        d2: DragonData,
        beb: Boolean,
        fast: Boolean
    ): List<DragonData>? = Calc.breedCalc(allDragons, d1, d2, beb, fast)

}