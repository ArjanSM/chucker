package com.chuckerteam.chucker.internal.ui.filter.command

import com.chuckerteam.chucker.internal.data.preferences.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class FilterBySchemeAction(
    override val filterActionName: String,
    currentSelectedFilters: FilterData
) : FilterAction(filterActionName, currentSelectedFilters) {

    override suspend fun applyFilterAction() {
        withContext(Dispatchers.IO) {
            PreferencesManager.applyFilterByScheme(currentStateOfFilters as FilterByScheme)
        }
    }
}
