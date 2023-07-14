package com.chuckerteam.chucker.internal.data.preferences

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chuckerteam.chucker.internal.data.model.FILTERS_PREF
import com.chuckerteam.chucker.internal.data.model.FILTER_CATEGORY_METHOD_GET
import com.chuckerteam.chucker.internal.data.model.FILTER_CATEGORY_METHOD_POST
import com.chuckerteam.chucker.internal.data.model.FILTER_CATEGORY_METHOD_PUT
import com.chuckerteam.chucker.internal.data.model.FILTER_CATEGORY_SCHEME_HTTP
import com.chuckerteam.chucker.internal.data.model.FILTER_CATEGORY_SCHEME_HTTPS
import com.chuckerteam.chucker.internal.data.model.FilterByMethodData
import com.chuckerteam.chucker.internal.data.model.FilterByScheme
import com.chuckerteam.chucker.internal.data.model.FiltersData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal object PreferencesManager {
    private var sharedPreferences: SharedPreferences? = null
    private val _filterByMethod: MutableLiveData<FiltersData> = MutableLiveData<FiltersData>()
    val additionalFiltersPreferences: LiveData<FiltersData>
        get() = _filterByMethod

    private val _filterPreferenceState: MutableLiveData<FiltersPreferenceState> =
        MutableLiveData()

    val filterPreferencesState: LiveData<FiltersPreferenceState>
        get() = _filterPreferenceState
    suspend fun initialize(applicationContext: Context) {
        withContext(Dispatchers.IO) {
            if (sharedPreferences == null) {
                sharedPreferences = applicationContext.getSharedPreferences(FILTERS_PREF, MODE_PRIVATE)
            }
        }
    }

    private fun preferences(): SharedPreferences {
        return checkNotNull(sharedPreferences) {
            "You cannot access sharedPreferences if you don't initialize it"
        }
    }

    suspend fun applyFiltersPreference(filtersData: FiltersData) {
        return withContext(Dispatchers.IO) {
            preferences().edit().apply {
                putBoolean(FILTER_CATEGORY_METHOD_GET, filtersData.filterByMethodData.get)
                putBoolean(FILTER_CATEGORY_METHOD_PUT, filtersData.filterByMethodData.put)
                putBoolean(FILTER_CATEGORY_METHOD_POST, filtersData.filterByMethodData.post)
                // filters by scheme
                putBoolean(FILTER_CATEGORY_SCHEME_HTTPS, filtersData.filterByScheme.https)
                putBoolean(FILTER_CATEGORY_SCHEME_HTTP, filtersData.filterByScheme.http)
                this.apply()
            }
            val updatedFilters = getFiltersData()
            _filterByMethod.postValue(updatedFilters)
        }
    }

    suspend fun getFiltersData(): FiltersData {
        return withContext(Dispatchers.IO) {
            val filtersByMethod = getFiltersByMethod()
            val filterByScheme = getFiltersByScheme()
            FiltersData(filtersByMethod, filterByScheme).also {
                checkFilterPreferenceState(it)
            }
        }
    }

    private fun getFiltersByMethod(): FilterByMethodData {
        val get = preferences().getBoolean(FILTER_CATEGORY_METHOD_GET, true)
        val put = preferences().getBoolean(FILTER_CATEGORY_METHOD_PUT, true)
        val post = preferences().getBoolean(FILTER_CATEGORY_METHOD_POST, true)
        return FilterByMethodData(get, post, put)
    }

    private fun getFiltersByScheme(): FilterByScheme {
        val https = preferences().getBoolean(FILTER_CATEGORY_SCHEME_HTTPS, true)
        val http = preferences().getBoolean(FILTER_CATEGORY_SCHEME_HTTP, true)
        return FilterByScheme(https = https, http = http)
    }

    private fun checkFilterPreferenceState(filtersData: FiltersData) {
        if (haveFiltersByMethodChanged(filtersData.filterByMethodData) ||
            haveFiltersBySchemeChanged(filtersData.filterByScheme)
        ) {
            _filterPreferenceState.postValue(FiltersPreferenceState.CHANGED)
        } else {
            _filterPreferenceState.postValue(FiltersPreferenceState.DEFAULT)
        }
    }

    private fun haveFiltersByMethodChanged(filtersByMethod: FilterByMethodData): Boolean {
        return (!filtersByMethod.get || !filtersByMethod.put || !filtersByMethod.post)
    }
    private fun haveFiltersBySchemeChanged(filtersByScheme: FilterByScheme): Boolean {
        return (!filtersByScheme.https || !filtersByScheme.http)
    }
}

public enum class FiltersPreferenceState {
    DEFAULT,
    CHANGED
}
