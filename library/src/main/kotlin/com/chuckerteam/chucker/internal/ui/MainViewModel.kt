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
import com.chuckerteam.chucker.internal.data.preferences.FiltersPreferenceState
import com.chuckerteam.chucker.internal.data.preferences.PreferencesManager
import com.chuckerteam.chucker.internal.data.repository.RepositoryProvider
import com.chuckerteam.chucker.internal.support.NotificationHelper
import com.chuckerteam.chucker.internal.ui.filter.command.AllFilters
import com.chuckerteam.chucker.internal.ui.filter.command.FilterAction
import kotlinx.coroutines.launch

internal class MainViewModel : ViewModel() {

    init {
        viewModelScope.launch {
            filtersState = PreferencesManager.filterPreferencesState
            filterData = PreferencesManager.filterData
            PreferencesManager.getFilterData()
        }
    }

    private val currentFilter = MutableLiveData("")
    private val _filteredTransactions: MutableLiveData<List<HttpTransactionTuple>> = MutableLiveData()
    private val filteredTransactions: LiveData<List<HttpTransactionTuple>>
        get() = _filteredTransactions

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
            addSource(filterData) {
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
        return (filterData.value?.filterByMethod?.get == true && method == "GET") ||
            (filterData.value?.filterByMethod?.post == true && method == "POST") ||
            (filterData.value?.filterByMethod?.put == true && method == "PUT")
    }

    private fun containsFilteredScheme(scheme: String?): Boolean {
        return (filterData.value?.filterByScheme?.https == true && scheme == "https") ||
            (filterData.value?.filterByScheme?.http == true && scheme == "http")
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

    fun updateFilter(filterAction: FilterAction) {
        viewModelScope.launch {
            filterAction.applyFilterAction()
        }
    }

    lateinit var filterData: LiveData<AllFilters>
}
