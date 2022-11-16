package com.tylerb.dragonvalesandbox.storage

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import platform.Foundation.NSUserDefaults

private const val DATE_KEY = "date_key"
private const val SANDBOX_SEARCH_KEY = "sandbox_search_key"

actual class Preferences {

    actual val dateSettings: DateSettings
        get() = object : DateSettings {
            override fun getLastUpdated(): LocalDateTime? {
                return NSUserDefaults.standardUserDefaults.stringForKey(DATE_KEY)?.let { stringDate ->
                    LocalDateTime.parse(stringDate)
                }

            }

            override fun setLastUpdated(dateTime: LocalDateTime) {
                NSUserDefaults.standardUserDefaults.setObject(dateTime.toString(), DATE_KEY)
            }
        }

    actual val searchSettings: SearchSettings
        get() = object : SearchSettings {
            override fun getLastSandboxSearch(): Pair<String, String>? {
                return NSUserDefaults.standardUserDefaults.stringForKey(SANDBOX_SEARCH_KEY)?.let { names ->
                    val (d1, d2) = names.split(", ")
                    Pair(d1, d2)
                }
            }


            override fun setLastSandboxSearch(d1Name: String, d2Name: String) {
                NSUserDefaults.standardUserDefaults.setObject("$d1Name, $d2Name", SANDBOX_SEARCH_KEY)
            }
        }

}