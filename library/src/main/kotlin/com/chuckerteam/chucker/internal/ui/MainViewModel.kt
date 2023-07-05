package com.chuckerteam.chucker.internal.ui

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.chuckerteam.chucker.internal.data.entity.HttpTransaction
import com.chuckerteam.chucker.internal.data.entity.HttpTransactionTuple
import com.chuckerteam.chucker.internal.data.model.FiltersData
import com.chuckerteam.chucker.internal.data.preferences.FiltersPreferenceState
import com.chuckerteam.chucker.internal.data.preferences.PreferencesManager
import com.chuckerteam.chucker.internal.data.repository.RepositoryProvider
import com.chuckerteam.chucker.internal.support.NotificationHelper
import kotlinx.coroutines.launch

internal class MainViewModel : ViewModel() {

    init {
        viewModelScope.launch {
            additionalFilters = PreferencesManager.getFiltersData()
            filtersState = PreferencesManager.filterPreferencesState
        }
    }

    private val currentFilter = MutableLiveData("")
    private val _filteredTransactions: MutableLiveData<List<HttpTransactionTuple>> = MutableLiveData()
    private val filteredTransactions: LiveData<List<HttpTransactionTuple>>
        get() = _filteredTransactions

    private val filterPreferences: LiveData<FiltersData> = PreferencesManager.additionalFiltersPereferences

    private lateinit var originalResults: List<HttpTransactionTuple>
    private var _transactions: LiveData<List<HttpTransactionTuple>> = currentFilter.switchMap { searchQuery ->
        with(RepositoryProvider.transaction()) {
            when {
                searchQuery.isNullOrBlank() -> {
                    getSortedTransactionTuples()
                }
                TextUtils.isDigitsOnly(searchQuery) -> {
                    getFilteredTransactionTuples(searchQuery, "")
                }
                else -> {
                    getFilteredTransactionTuples("", searchQuery)
                }
            }
        }
    }

    val finalFilteredTransactions: MediatorLiveData<List<HttpTransactionTuple>> =
        MediatorLiveData<List<HttpTransactionTuple>>().apply {
            addSource(_transactions) { s ->
                originalResults = mutableListOf<HttpTransactionTuple>().apply {
                    addAll(s)
                }
                applyFilters()
            }
            addSource(filteredTransactions) {
                this.value = originalResults.filter { containsFilteredMethod(it.method) }
            }
            addSource(filterPreferences) {
                additionalFilters = it
                applyFilters()
            }
        }

    lateinit var filtersState: LiveData<FiltersPreferenceState>

    fun applyFilters() {
        _filteredTransactions.value = originalResults.filter { containsFilteredMethod(it.method) }
    }

    fun saveFilters() {
        viewModelScope.launch {
            additionalFilters?.let {
                PreferencesManager.applyFiltersPreference(filtersData = it)
            }
            applyFilters()
        }
    }
    private fun containsFilteredMethod(method: String?): Boolean {
        return (additionalFilters?.filterByMethodData?.get == true && method == "GET") ||
            (additionalFilters?.filterByMethodData?.post == true && method == "POST") ||
            (additionalFilters?.filterByMethodData?.put == true && method == "PUT")
    }

    suspend fun getAllTransactions(): List<HttpTransaction> = RepositoryProvider.transaction().getAllTransactions()

    fun updateItemsFilter(searchQuery: String) {
        currentFilter.value = searchQuery
    }

    fun clearTransactions() {
        viewModelScope.launch {
            RepositoryProvider.transaction().deleteAllTransactions()
        }
        NotificationHelper.clearBuffer()
    }

    private val filterCategory = MutableLiveData("")
    val currentFilterCategory: LiveData<String> = filterCategory
    fun updateFilterCategory(latestFilterCategoryClicked: String) {
        filterCategory.value = latestFilterCategoryClicked
    }

    var additionalFilters: FiltersData? = null
}
