package com.chuckerteam.chucker.internal.data.preferences

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.chuckerteam.chucker.internal.data.model.FilterByMethodData
import com.chuckerteam.chucker.internal.data.model.FiltersData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal object PreferencesManager {
    private var sharedPreferences: SharedPreferences? = null
    suspend fun initialize(applicationContext: Context) {
        withContext(Dispatchers.IO) {
            if (sharedPreferences == null) {
                sharedPreferences = applicationContext.getSharedPreferences("filters", MODE_PRIVATE)
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
                putBoolean("filter_by_category_method_get", filtersData.filterByMethodData.get)
                putBoolean("filter_by_category_method_put", filtersData.filterByMethodData.put)
                putBoolean("filter_by_category_method_post", filtersData.filterByMethodData.post)
                this.apply()
            }
            getFiltersData()
        }
    }

    suspend fun getFiltersData(): FiltersData {
        return withContext(Dispatchers.IO) {
            val get = preferences().getBoolean("filter_by_category_method_get", true)
            val put = preferences().getBoolean("filter_by_category_method_put", true)
            val post = preferences().getBoolean("filter_by_category_method_post", true)
            FiltersData(FilterByMethodData(get, post, put))
        }
    }
}
