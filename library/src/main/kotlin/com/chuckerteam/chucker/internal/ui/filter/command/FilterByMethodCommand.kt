package com.chuckerteam.chucker.internal.ui.filter.command

import androidx.viewbinding.ViewBinding
import com.chuckerteam.chucker.databinding.ChuckerFilterCategoryMethodBinding
import com.chuckerteam.chucker.internal.data.preferences.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class FilterByMethodCommand(
    override val commandName: String,
    currentSelectedFilters: FilterData
) : FilterCommand(commandName, currentSelectedFilters) {

    override fun renderUI(viewBinding: ViewBinding) {
        val binding = viewBinding as ChuckerFilterCategoryMethodBinding
        val filterByMethodSettings = currentStateOfFilters as FilterByMethod
        binding.chuckerFilterCategoryMethodGet.isChecked = filterByMethodSettings.get
        binding.chuckerFilterCategoryMethodPost.isChecked = filterByMethodSettings.post
        binding.chuckerFilterCategoryMethodPut.isChecked = filterByMethodSettings.put
        setupFilterByMethodClickListeners(binding)
    }

    private fun setupFilterByMethodClickListeners(filterByMethodViewBinding: ChuckerFilterCategoryMethodBinding) {
        filterByMethodViewBinding
            .chuckerFilterCategoryMethodGet
            .setOnCheckedChangeListener { _, isChecked ->
                val filterByMethodSettings = currentStateOfFilters as FilterByMethod
                currentStateOfFilters = FilterByMethod(
                    get = isChecked,
                    post = filterByMethodSettings.post,
                    put = filterByMethodSettings.put
                )
            }
        filterByMethodViewBinding
            .chuckerFilterCategoryMethodPost
            .setOnCheckedChangeListener { _, isChecked ->
                val filterByMethodSettings = currentStateOfFilters as FilterByMethod
                previousStateOfFilters = currentStateOfFilters
                currentStateOfFilters = FilterByMethod(
                    get = filterByMethodSettings.get,
                    post = isChecked,
                    put = filterByMethodSettings.put
                )
            }
        filterByMethodViewBinding
            .chuckerFilterCategoryMethodPut
            .setOnCheckedChangeListener { compoundButton, isChecked ->
                val filterByMethodSettings = currentStateOfFilters as FilterByMethod
                previousStateOfFilters = currentStateOfFilters
                currentStateOfFilters = FilterByMethod(
                    get = filterByMethodSettings.get,
                    post = filterByMethodSettings.post,
                    put = isChecked
                )
            }
    }

    override suspend fun executeCommand() {
        withContext(Dispatchers.IO) {
            val currentFilterByMethod = (currentStateOfFilters as FilterByMethod)
            PreferencesManager.applyFilterByMethod(currentFilterByMethod)
        }
    }
}
