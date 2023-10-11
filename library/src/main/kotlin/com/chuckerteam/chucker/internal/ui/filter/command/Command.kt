package com.chuckerteam.chucker.internal.ui.filter.command

import androidx.viewbinding.ViewBinding

public abstract class FilterCommand(public open val commandName: String, currentFilterState: FilterData) {
    protected var previousStateOfFilters: FilterData
    protected var currentStateOfFilters: FilterData

    init {
        previousStateOfFilters = currentFilterState
        currentStateOfFilters = currentFilterState
    }

    internal fun undoFilterCommand() {
        currentStateOfFilters = previousStateOfFilters
    }

    public abstract suspend fun executeCommand()

    public abstract fun renderUI(viewBinding: ViewBinding)
    public fun hasChanged(): Boolean = previousStateOfFilters != currentStateOfFilters
}

public sealed class FilterData
internal class FilterByScheme(val https: Boolean = true, val http: Boolean = true) : FilterData() {
    fun toFilterCommand(): FilterCommand {
        return FilterBySchemeCommand("Scheme", this)
    }
}
internal class FilterByMethod(
    val get: Boolean = true,
    val post: Boolean = true,
    val put: Boolean = true
) : FilterData() {
    fun toFilterCommand(): FilterCommand {
        return FilterByMethodCommand("Method", this)
    }
}

internal class AllFilters(val filterByScheme: FilterByScheme, val filterByMethod: FilterByMethod) :
    FilterData()
