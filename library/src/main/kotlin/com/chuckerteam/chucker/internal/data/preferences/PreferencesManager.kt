package com.chuckerteam.chucker.internal.data.preferences

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.chuckerteam.chucker.internal.data.model.FILTERS_PREF
import com.chuckerteam.chucker.internal.data.model.FILTER_CATEGORY_METHOD_GET
import com.chuckerteam.chucker.internal.data.model.FILTER_CATEGORY_METHOD_POST
import com.chuckerteam.chucker.internal.data.model.FILTER_CATEGORY_METHOD_PUT
import com.chuckerteam.chucker.internal.data.model.FilterByMethodData
import com.chuckerteam.chucker.internal.data.model.FiltersData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal object PreferencesManager {
    private var sharedPreferences: SharedPreferences? = null
    suspend fun initialize(applicationContext: Context) {
        withContext(Dispatchers.IO) {
            if (sharedPreferences == null) {
                sharedPreferences = applicationContext.getSharedPreferences(FILTERS_PREF, MODE_PRIVATE)
            }
        }
    }

    fun preferences(): SharedPreferences {
        return checkNotNull(sharedPreferences) {
            "You cannot access sharedPreferences if you don't initialize it"
        }
    }

    suspend fun applyFiltersPreference(filtersData: FiltersData): FiltersData {
        return withContext(Dispatchers.IO) {
            preferences().edit().apply {
                putBoolean(FILTER_CATEGORY_METHOD_GET, filtersData.filterByMethodData.get)
                putBoolean(FILTER_CATEGORY_METHOD_PUT, filtersData.filterByMethodData.put)
                putBoolean(FILTER_CATEGORY_METHOD_POST, filtersData.filterByMethodData.post)
                this.apply()
            }
            getFiltersData()
        }
    }

    suspend fun getFiltersData(): FiltersData {
        return withContext(Dispatchers.IO) {
            val get = preferences().getBoolean(FILTER_CATEGORY_METHOD_GET, true)
            val put = preferences().getBoolean(FILTER_CATEGORY_METHOD_PUT, true)
            val post = preferences().getBoolean(FILTER_CATEGORY_METHOD_POST, true)
            FiltersData(FilterByMethodData(get, post, put))
        }
    }
}
