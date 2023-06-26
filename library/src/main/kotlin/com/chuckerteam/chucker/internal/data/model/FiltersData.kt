package com.chuckerteam.chucker.internal.data.model

internal data class FiltersData(val filterByMethodData: FilterByMethodData)

internal data class FilterByMethodData(
    var get: Boolean = true,
    var post: Boolean = true,
    var put: Boolean = true
)
