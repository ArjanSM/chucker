package com.chuckerteam.chucker.internal.ui.filter.command

import com.chuckerteam.chucker.internal.data.repository.RepositoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class FilterByMethodAction(
    override val filterActionName: String,
    currentSelectedFilters: FilterData
) : FilterAction(filterActionName, currentSelectedFilters) {

    override suspend fun applyFilterAction() {
        withContext(Dispatchers.IO) {
            val currentFilterByMethod = (currentStateOfFilters as FilterByMethod)
            RepositoryProvider.preferences().applyFilterByMethod(currentFilterByMethod)
        }
    }
}
