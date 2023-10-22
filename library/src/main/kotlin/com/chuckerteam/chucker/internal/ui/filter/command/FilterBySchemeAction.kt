package com.chuckerteam.chucker.internal.ui.filter.command

import com.chuckerteam.chucker.internal.data.repository.RepositoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class FilterBySchemeAction(
    override val filterActionName: String,
    currentSelectedFilters: FilterData
) : FilterAction(filterActionName, currentSelectedFilters) {

    override suspend fun applyFilterAction() {
        withContext(Dispatchers.IO) {
            RepositoryProvider.preferences().applyFilterByScheme(currentStateOfFilters as FilterByScheme)
        }
    }
}
