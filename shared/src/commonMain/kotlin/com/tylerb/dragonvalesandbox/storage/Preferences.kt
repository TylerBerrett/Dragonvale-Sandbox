package com.tylerb.dragonvalesandbox.storage

import kotlinx.datetime.LocalDateTime

expect class Preferences {
    val dateSettings: DateSettings
    val searchSettings: SearchSettings
}

interface DateSettings {
    fun getLastUpdated(): LocalDateTime?
    fun setLastUpdated(dateTime: LocalDateTime)
}

interface SearchSettings {
    fun getLastSandboxSearch(): Pair<String, String>?
    fun setLastSandboxSearch(d1Name: String, d2Name: String)
}