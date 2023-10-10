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
        binding.chipGet.isChecked = filterByMethodSettings.get
        binding.chipPost.isChecked = filterByMethodSettings.post
        binding.chipPut.isChecked = filterByMethodSettings.put
        setupFilterByMethodClickListeners(binding)
    }

    private fun setupFilterByMethodClickListeners(filterByMethodViewBinding: ChuckerFilterCategoryMethodBinding) {
        filterByMethodViewBinding
            .chipGet
            .setOnCheckedChangeListener { _, isChecked ->
                val filterByMethodSettings = currentStateOfFilters as FilterByMethod
                currentStateOfFilters = FilterByMethod(
                    get = isChecked,
                    post = filterByMethodSettings.post,
                    put = filterByMethodSettings.put
                )
            }
        filterByMethodViewBinding
            .chipPost
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
            .chipPut
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
