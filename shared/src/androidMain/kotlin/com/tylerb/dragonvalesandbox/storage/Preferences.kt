package com.tylerb.dragonvalesandbox.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

private val DATE_KEY = stringPreferencesKey("date_key")
private val SANDBOX_SEARCH_KEY = stringPreferencesKey("sandbox_search_key")

actual class Preferences(private val context: Context) {

    actual val dateSettings: DateSettings
        get() = object : DateSettings {
            override fun getLastUpdated(): LocalDateTime? = runBlocking {
                context.dataStore.data.first()[DATE_KEY]?.let { stringDate ->
                    LocalDateTime.parse(stringDate)
                }
            }

            override fun setLastUpdated(dateTime: LocalDateTime) {
                runBlocking {
                    context.dataStore.edit { settings ->
                        settings[DATE_KEY] = dateTime.toString()
                    }
                }
            }
        }

    actual val searchSettings: SearchSettings
        get() = object : SearchSettings {
            override fun getLastSandboxSearch(): Pair<String, String>? = runBlocking {
                context.dataStore.data.first()[SANDBOX_SEARCH_KEY]?.let { names ->
                    val (d1, d2) = names.split(", ")
                    Pair(d1, d2)
                }
            }

            override fun setLastSandboxSearch(d1Name: String, d2Name: String) {
                runBlocking {
                    context.dataStore.edit { settings ->
                        settings[SANDBOX_SEARCH_KEY] = "$d1Name, $d2Name"
                    }
                }
            }
        }

}