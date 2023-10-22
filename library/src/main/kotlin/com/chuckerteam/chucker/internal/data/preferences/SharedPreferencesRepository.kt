package com.chuckerteam.chucker.internal.data.preferences

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chuckerteam.chucker.internal.data.model.FILTER_CATEGORY_METHOD_GET
import com.chuckerteam.chucker.internal.data.model.FILTER_CATEGORY_METHOD_POST
import com.chuckerteam.chucker.internal.data.model.FILTER_CATEGORY_METHOD_PUT
import com.chuckerteam.chucker.internal.data.model.FILTER_CATEGORY_SCHEME_HTTP
import com.chuckerteam.chucker.internal.data.model.FILTER_CATEGORY_SCHEME_HTTPS
import com.chuckerteam.chucker.internal.ui.filter.command.AllFilters
import com.chuckerteam.chucker.internal.ui.filter.command.FilterByMethod
import com.chuckerteam.chucker.internal.ui.filter.command.FilterByScheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class SharedPreferencesRepository(private val sharedPreferences: SharedPreferences) {
    private val _filterPreferenceState: MutableLiveData<FiltersPreferenceState> =
        MutableLiveData()

    val filterPreferencesState: LiveData<FiltersPreferenceState>
        get() = _filterPreferenceState

    private val _filterData: MutableLiveData<AllFilters> = MutableLiveData()
    val filterData: LiveData<AllFilters>
        get() = _filterData

    suspend fun applyFilterByMethod(filterByMethod: FilterByMethod) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().apply {
                putBoolean(FILTER_CATEGORY_METHOD_GET, filterByMethod.get)
                putBoolean(FILTER_CATEGORY_METHOD_POST, filterByMethod.post)
                putBoolean(FILTER_CATEGORY_METHOD_PUT, filterByMethod.put)
                this.apply()
            }
            val updatedFilters = getFilterData()
            _filterData.postValue(updatedFilters)
        }
    }
    suspend fun applyFilterByScheme(filterByScheme: FilterByScheme) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().apply {
                putBoolean(FILTER_CATEGORY_SCHEME_HTTP, filterByScheme.http)
                putBoolean(FILTER_CATEGORY_SCHEME_HTTPS, filterByScheme.https)
                this.apply()
            }
            val updatedFilters = getFilterData()
            _filterData.postValue(updatedFilters)
        }
    }

    suspend fun getFilterData(): AllFilters {
        return withContext(Dispatchers.IO) {
            val filterByScheme = getFilterByScheme()
            val filterByMethod = getFilterByMethod()
            val allFilters = AllFilters(filterByScheme, filterByMethod).also {
                checkFilterPreferenceState(it)
            }
            _filterData.postValue(allFilters)
            allFilters
        }
    }

    private fun getFilterByMethod(): FilterByMethod {
        val get = sharedPreferences.getBoolean(FILTER_CATEGORY_METHOD_GET, true)
        val put = sharedPreferences.getBoolean(FILTER_CATEGORY_METHOD_PUT, true)
        val post = sharedPreferences.getBoolean(FILTER_CATEGORY_METHOD_POST, true)
        return FilterByMethod(get, post, put)
    }

    private fun getFilterByScheme(): FilterByScheme {
        val https = sharedPreferences.getBoolean(FILTER_CATEGORY_SCHEME_HTTPS, true)
        val http = sharedPreferences.getBoolean(FILTER_CATEGORY_SCHEME_HTTP, true)
        return FilterByScheme(https = https, http = http)
    }

    private fun checkFilterPreferenceState(allFilters: AllFilters) {
        if (haveFiltersByMethodChanged(allFilters.filterByMethod) ||
            haveFiltersBySchemeChanged(allFilters.filterByScheme)
        ) {
            _filterPreferenceState.postValue(FiltersPreferenceState.CHANGED)
        } else {
            _filterPreferenceState.postValue(FiltersPreferenceState.DEFAULT)
        }
    }

    private fun haveFiltersByMethodChanged(filtersByMethod: FilterByMethod): Boolean {
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
