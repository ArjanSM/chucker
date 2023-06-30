package com.chuckerteam.chucker.internal.data.model

internal const val FILTERS_PREF = "filters_shared_prefs"
internal const val FILTER_CATEGORY_METHOD_GET = "filter_by_category_method_get"
internal const val FILTER_CATEGORY_METHOD_PUT = "filter_by_category_method_put"
internal const val FILTER_CATEGORY_METHOD_POST = "filter_by_category_method_post"

internal data class FiltersData(val filterByMethodData: FilterByMethodData)

internal data class FilterByMethodData(
    var get: Boolean = true,
    var post: Boolean = true,
    var put: Boolean = true
)
