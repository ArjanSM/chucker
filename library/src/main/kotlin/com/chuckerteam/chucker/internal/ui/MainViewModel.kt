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
import com.chuckerteam.chucker.internal.ui.filter.command.AllFilters
import com.chuckerteam.chucker.internal.ui.filter.command.FilterCommand
import kotlinx.coroutines.launch

internal class MainViewModel : ViewModel() {

    init {
        viewModelScope.launch {
            additionalFilters = PreferencesManager.getFiltersData()
            filtersState = PreferencesManager.filterPreferencesState
            _filterData.value = PreferencesManager.getFilterData()
        }
    }

    private val currentFilter = MutableLiveData("")
    private val _filteredTransactions: MutableLiveData<List<HttpTransactionTuple>> = MutableLiveData()
    private val filteredTransactions: LiveData<List<HttpTransactionTuple>>
        get() = _filteredTransactions

    private val filterPreferences: LiveData<FiltersData> = PreferencesManager.additionalFiltersPreferences

    private var searchFilteredTransactions: List<HttpTransactionTuple> = emptyList()
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
            addSource(_transactions) { searchFilteredTransactions ->
                this@MainViewModel.searchFilteredTransactions = mutableListOf<HttpTransactionTuple>().apply {
                    addAll(searchFilteredTransactions)
                }
                applyFilters()
            }
            addSource(filteredTransactions) {
                this.value = searchFilteredTransactions.filter {
                    containsFilteredMethod(it.method) && containsFilteredScheme(it.scheme)
                }
            }
            addSource(filterPreferences) {
                additionalFilters = it
                applyFilters()
            }
        }

    lateinit var filtersState: LiveData<FiltersPreferenceState>
    private fun applyFilters() {
        _filteredTransactions.value = searchFilteredTransactions.filter {
            containsFilteredMethod(it.method) && containsFilteredScheme(it.scheme)
        }
    }
    private fun containsFilteredMethod(method: String?): Boolean {
        return (additionalFilters?.filtersByMethodData?.get == true && method == "GET") ||
            (additionalFilters?.filtersByMethodData?.post == true && method == "POST") ||
            (additionalFilters?.filtersByMethodData?.put == true && method == "PUT")
    }

    private fun containsFilteredScheme(scheme: String?): Boolean {
        return (additionalFilters?.filtersByScheme?.https == true && scheme == "https") ||
            (additionalFilters?.filtersByScheme?.http == true && scheme == "http")
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

    private val filterCategory = MutableLiveData<FilterCommand>()
    val currentFilterCategory: LiveData<FilterCommand>
        get() = filterCategory
    fun updateFilterCategory(latestFilterCommand: FilterCommand) {
        filterCategory.value = latestFilterCommand
    }

    var additionalFilters: FiltersData? = null

    private val _lastClickedFilter = MutableLiveData<FilterCommand>()
    val lastClickedFilter: LiveData<FilterCommand>
        get() = _lastClickedFilter
    fun updateLastClickedFilter(filterCommand: FilterCommand) {
        _lastClickedFilter.value = filterCommand
    }

    fun updateFilter(filterCommand: FilterCommand) {
        viewModelScope.launch {
            filterCommand.executeCommand()
        }
    }

    private val _filterData: MutableLiveData<AllFilters> = MutableLiveData()
    val filterData: LiveData<AllFilters>
        get() = _filterData
}
