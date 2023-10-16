package com.chuckerteam.chucker.internal.ui.filter.command

public abstract class FilterAction(public open val filterActionName: String, currentFilterState: FilterData) {
    private var previousStateOfFilters: FilterData
    protected var currentStateOfFilters: FilterData

    init {
        previousStateOfFilters = currentFilterState
        currentStateOfFilters = currentFilterState
    }

    public abstract suspend fun applyFilterAction()

    public fun hasChanged(): Boolean = previousStateOfFilters != currentStateOfFilters
}

public sealed class FilterData
internal class FilterByScheme(val https: Boolean = true, val http: Boolean = true) : FilterData()
internal class FilterByMethod(
    val get: Boolean = true,
    val post: Boolean = true,
    val put: Boolean = true
) : FilterData()

internal class AllFilters(val filterByScheme: FilterByScheme, val filterByMethod: FilterByMethod) :
    FilterData()
