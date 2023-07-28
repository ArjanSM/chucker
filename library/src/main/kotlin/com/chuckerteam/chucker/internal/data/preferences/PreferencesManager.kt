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
import com.chuckerteam.chucker.internal.data.model.FiltersByMethodData
import com.chuckerteam.chucker.internal.data.model.FiltersByScheme
import com.chuckerteam.chucker.internal.data.model.FiltersData
import com.chuckerteam.chucker.internal.ui.filter.command.AllFilters
import com.chuckerteam.chucker.internal.ui.filter.command.FilterByMethod
import com.chuckerteam.chucker.internal.ui.filter.command.FilterByScheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Suppress("TooManyFunctions")
internal object PreferencesManager {
    private var sharedPreferences: SharedPreferences? = null
    private val additionalFilters: MutableLiveData<FiltersData> = MutableLiveData<FiltersData>()
    val additionalFiltersPreferences: LiveData<FiltersData>
        get() = additionalFilters

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

    suspend fun getFiltersData(): FiltersData {
        return withContext(Dispatchers.IO) {
            val filtersByMethod = getFiltersByMethod()
            val filterByScheme = getFiltersByScheme()
            FiltersData(filtersByMethod, filterByScheme).also {
                checkFilterPreferenceState(it)
            }
        }
    }
    suspend fun applyFilterByMethod(filterByMethod: FilterByMethod) {
        withContext(Dispatchers.IO) {
            preferences().edit().apply {
                putBoolean(FILTER_CATEGORY_METHOD_GET, filterByMethod.get)
                putBoolean(FILTER_CATEGORY_METHOD_POST, filterByMethod.post)
                putBoolean(FILTER_CATEGORY_METHOD_PUT, filterByMethod.put)
                this.apply()
            }
            val updatedFilters = getFiltersData()
            additionalFilters.postValue(updatedFilters)
        }
    }
    suspend fun applyFilterByScheme(filterByScheme: FilterByScheme) {
        withContext(Dispatchers.IO) {
            preferences().edit().apply {
                putBoolean(FILTER_CATEGORY_SCHEME_HTTP, filterByScheme.http)
                putBoolean(FILTER_CATEGORY_SCHEME_HTTPS, filterByScheme.https)
                this.apply()
            }
            val updatedFilters = getFiltersData()
            additionalFilters.postValue(updatedFilters)
        }
    }

    suspend fun getFilterData(): AllFilters {
        return withContext(Dispatchers.IO) {
            val filterByScheme = getFilterByScheme()
            val filterByMethod = getFilterByMethod()
            AllFilters(filterByScheme, filterByMethod)
        }
    }

    private fun getFiltersByMethod(): FiltersByMethodData {
        val get = preferences().getBoolean(FILTER_CATEGORY_METHOD_GET, true)
        val put = preferences().getBoolean(FILTER_CATEGORY_METHOD_PUT, true)
        val post = preferences().getBoolean(FILTER_CATEGORY_METHOD_POST, true)
        return FiltersByMethodData(get, post, put)
    }

    private fun getFilterByMethod(): FilterByMethod {
        val get = preferences().getBoolean(FILTER_CATEGORY_METHOD_GET, true)
        val put = preferences().getBoolean(FILTER_CATEGORY_METHOD_PUT, true)
        val post = preferences().getBoolean(FILTER_CATEGORY_METHOD_POST, true)
        return FilterByMethod(get, post, put)
    }

    private fun getFiltersByScheme(): FiltersByScheme {
        val https = preferences().getBoolean(FILTER_CATEGORY_SCHEME_HTTPS, true)
        val http = preferences().getBoolean(FILTER_CATEGORY_SCHEME_HTTP, true)
        return FiltersByScheme(https = https, http = http)
    }
    private fun getFilterByScheme(): FilterByScheme {
        val https = preferences().getBoolean(FILTER_CATEGORY_SCHEME_HTTPS, true)
        val http = preferences().getBoolean(FILTER_CATEGORY_SCHEME_HTTP, true)
        return FilterByScheme(https = https, http = http)
    }

    private fun checkFilterPreferenceState(filtersData: FiltersData) {
        if (haveFiltersByMethodChanged(filtersData.filtersByMethodData) ||
            haveFiltersBySchemeChanged(filtersData.filtersByScheme)
        ) {
            _filterPreferenceState.postValue(FiltersPreferenceState.CHANGED)
        } else {
            _filterPreferenceState.postValue(FiltersPreferenceState.DEFAULT)
        }
    }

    private fun haveFiltersByMethodChanged(filtersByMethod: FiltersByMethodData): Boolean {
        return (!filtersByMethod.get || !filtersByMethod.put || !filtersByMethod.post)
    }
    private fun haveFiltersBySchemeChanged(filtersByScheme: FiltersByScheme): Boolean {
        return (!filtersByScheme.https || !filtersByScheme.http)
    }
}

public enum class FiltersPreferenceState {
    DEFAULT,
    CHANGED
}
