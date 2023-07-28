package com.chuckerteam.chucker.internal.ui.filter.command

import androidx.viewbinding.ViewBinding

internal abstract class FilterCommand(open val commandName: String, currentFilterState: FilterData) {
    protected var previousStateOfFilters: FilterData
    protected var currentStateOfFilters: FilterData

    init {
        previousStateOfFilters = currentFilterState
        currentStateOfFilters = currentFilterState
    }

    internal fun undoFilterCommand() {
        currentStateOfFilters = previousStateOfFilters
    }

    abstract suspend fun executeCommand()

    abstract fun renderUI(viewBinding: ViewBinding)
    fun hasChanged() = previousStateOfFilters != currentStateOfFilters
}

internal sealed class FilterData
internal class FilterByScheme(val https: Boolean = true, val http: Boolean = true) : FilterData()
internal class FilterByMethod(
    val get: Boolean = true,
    val post: Boolean = true,
    val put: Boolean = true
) : FilterData()

internal class AllFilters(val filterByScheme: FilterByScheme, val filterByMethod: FilterByMethod) :
    FilterData()
