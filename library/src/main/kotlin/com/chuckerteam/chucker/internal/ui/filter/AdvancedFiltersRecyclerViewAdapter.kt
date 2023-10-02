package com.chuckerteam.chucker.internal.ui.filter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.viewbinding.ViewBinding
import com.chuckerteam.chucker.databinding.ChuckerFilterCategoryMethodBinding
import com.chuckerteam.chucker.databinding.ChuckerFilterCategorySchemeBinding
import com.chuckerteam.chucker.internal.ui.filter.command.FilterByMethod
import com.chuckerteam.chucker.internal.ui.filter.command.FilterByScheme

internal class AdvancedFiltersRecyclerViewAdapter(private val viewConfigs: List<FilterCategoryConfig>) :
    Adapter<AdvancedFiltersRecyclerViewAdapter.ViewHolder>() {
    override fun getItemViewType(position: Int): Int {
        return viewConfigs[position].layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvancedFiltersRecyclerViewAdapter.ViewHolder {
        val viewBinding = getViewBinding(parent, viewType)
        return ViewHolder(viewBinding)
    }

    override fun getItemCount() = viewConfigs.size

    override fun onBindViewHolder(holder: AdvancedFiltersRecyclerViewAdapter.ViewHolder, position: Int) {
        val viewConfig = viewConfigs[position]
        holder.populateUI(viewConfig)
    }

    inner class ViewHolder(private val viewBinding: ViewBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun populateUI(viewConfig: FilterCategoryConfig) {
            viewConfig.bindViewState(viewBinding)
        }
    }
    private fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
        val matchingConfig = viewConfigs.first { it.layout == viewType }
        return matchingConfig.inflateViewBinding(parent)
    }
}

internal abstract class FilterCategoryConfig(val layout: Int) {
    abstract fun inflateViewBinding(parent: ViewGroup): ViewBinding
    abstract fun bindViewState(viewBinding: ViewBinding)
}

internal class FilterBySchemeCategory(layout: Int, private val filterByScheme: FilterByScheme) :
    FilterCategoryConfig(layout) {
    override fun inflateViewBinding(parent: ViewGroup): ViewBinding {
        return ChuckerFilterCategorySchemeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindViewState(viewBinding: ViewBinding) {
        val layout = (viewBinding as ChuckerFilterCategorySchemeBinding)
        layout.chuckerFilterCategoryHttp.visibility =
            if (filterByScheme.http) View.VISIBLE else View.INVISIBLE
        layout.chuckerFilterCategoryHttps.visibility =
            if (filterByScheme.https) View.VISIBLE else View.INVISIBLE
    }
}

internal class FilterByVerbCategory(layout: Int, private val filterByMethod: FilterByMethod) :
    FilterCategoryConfig(layout) {
    override fun inflateViewBinding(parent: ViewGroup): ViewBinding {
        return ChuckerFilterCategoryMethodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindViewState(viewBinding: ViewBinding) {
        val layout = (viewBinding as ChuckerFilterCategoryMethodBinding)
        layout.chuckerFilterCategoryMethodGet.visibility =
            if (filterByMethod.get) View.VISIBLE else View.INVISIBLE
        layout.chuckerFilterCategoryMethodPost.visibility =
            if (filterByMethod.post) View.VISIBLE else View.INVISIBLE
        layout.chuckerFilterCategoryMethodPut.visibility =
            if (filterByMethod.put) View.VISIBLE else View.INVISIBLE
    }
}
