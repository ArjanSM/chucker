package com.chuckerteam.chucker.internal.ui.filter.command

import androidx.viewbinding.ViewBinding
import com.chuckerteam.chucker.databinding.ChuckerFilterCategorySchemeBinding
import com.chuckerteam.chucker.internal.data.preferences.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class FilterBySchemeCommand(
    override val commandName: String,
    currentSelectedFilters: FilterData
) : FilterCommand(commandName, currentSelectedFilters) {

    override suspend fun executeCommand() {
        withContext(Dispatchers.IO) {
            PreferencesManager.applyFilterByScheme(currentStateOfFilters as FilterByScheme)
        }
    }

    override fun renderUI(viewBinding: ViewBinding) {
        val binding = viewBinding as ChuckerFilterCategorySchemeBinding
        val filterBySchemeSettings = currentStateOfFilters as FilterByScheme
        binding.chuckerFilterCategoryHttp.isChecked = filterBySchemeSettings.http
        binding.chuckerFilterCategoryHttps.isChecked = filterBySchemeSettings.https
        setupFilterBySchemeClickListeners(binding)
    }

    private fun setupFilterBySchemeClickListeners(viewBinding: ChuckerFilterCategorySchemeBinding) {
        viewBinding.chuckerFilterCategoryHttp.setOnCheckedChangeListener { _, isChecked ->
            val filterBySchemeSettings = currentStateOfFilters as FilterByScheme
            previousStateOfFilters = currentStateOfFilters
            currentStateOfFilters = FilterByScheme(
                http = isChecked,
                https = filterBySchemeSettings.https
            )
        }

        viewBinding.chuckerFilterCategoryHttps.setOnCheckedChangeListener { _, isChecked ->
            val filterBySchemeSettings = currentStateOfFilters as FilterByScheme
            previousStateOfFilters = currentStateOfFilters
            currentStateOfFilters = FilterByScheme(
                http = filterBySchemeSettings.http,
                https = isChecked
            )
        }
    }
}
