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
import com.chuckerteam.chucker.internal.data.model.FilterByMethodData
import com.chuckerteam.chucker.internal.data.model.FiltersData
import com.chuckerteam.chucker.internal.data.repository.RepositoryProvider
import com.chuckerteam.chucker.internal.support.NotificationHelper
import kotlinx.coroutines.launch

internal class MainViewModel : ViewModel() {

    private val currentFilter = MutableLiveData("")
    private val _filteredTransactions: MutableLiveData<List<HttpTransactionTuple>> = MutableLiveData()
    private val filteredTransactions: LiveData<List<HttpTransactionTuple>>
        get() = _filteredTransactions
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

    val transactions: LiveData<List<HttpTransactionTuple>>
        get() = _transactions

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
        }

    fun applyFilters() {
        _filteredTransactions.value = originalResults.filter { containsFilteredMethod(it.method) }
    }
    private fun containsFilteredMethod(method: String?): Boolean {
        return (additionalFilters.filterByMethodData.get && method == "GET") ||
            (additionalFilters.filterByMethodData.post && method == "POST") ||
            (additionalFilters.filterByMethodData.post && method == "PUT")
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

    val additionalFilters = FiltersData(
        FilterByMethodData()
    )
}
