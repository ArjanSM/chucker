package com.chuckerteam.chucker.internal.ui.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.viewbinding.ViewBinding
import com.chuckerteam.chucker.databinding.ChuckerFilterCategoryMethodBinding
import com.chuckerteam.chucker.databinding.ChuckerFilterCategorySchemeBinding
import com.chuckerteam.chucker.internal.ui.filter.command.FilterByMethod
import com.chuckerteam.chucker.internal.ui.filter.command.FilterByMethodCommand
import com.chuckerteam.chucker.internal.ui.filter.command.FilterByScheme
import com.chuckerteam.chucker.internal.ui.filter.command.FilterBySchemeCommand
import com.chuckerteam.chucker.internal.ui.filter.command.FilterCommand

internal class AdvancedFiltersRecyclerViewAdapter(
    private val viewConfigs: List<FilterCategoryConfig>
) :
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

internal class FilterBySchemeCategory(
    layout: Int,
    filterByScheme: FilterByScheme,
    val advanceFilterCategoryItemClickListener: AdvanceFilterCategoryItemClickListener
) : FilterCategoryConfig(layout), OnCheckedChangeListener {
    private var filterBySchemeSelections = filterByScheme
    private lateinit var viewBinding: ChuckerFilterCategorySchemeBinding
    override fun inflateViewBinding(parent: ViewGroup): ViewBinding {
        return ChuckerFilterCategorySchemeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).also { this.viewBinding = it }
    }

    override fun bindViewState(viewBinding: ViewBinding) {
        val layout = (viewBinding as ChuckerFilterCategorySchemeBinding)
        layout.chipHttp.isChecked = filterBySchemeSelections.http
        layout.chipHttps.isChecked = filterBySchemeSelections.https
        layout.chipHttp.setOnCheckedChangeListener(this)
        layout.chipHttps.setOnCheckedChangeListener(this)
    }

    override fun onCheckedChanged(chip: CompoundButton?, p1: Boolean) {
        when (chip?.id) {
            viewBinding.chipHttp.id -> {
                filterBySchemeSelections = FilterByScheme(p1, filterBySchemeSelections.https)
            }
            viewBinding.chipHttps.id -> {
                filterBySchemeSelections = FilterByScheme(filterBySchemeSelections.http, p1)
            }
        }
        advanceFilterCategoryItemClickListener.onFilterCategoryClick(
            FilterBySchemeCommand("Scheme", filterBySchemeSelections)
        )
    }
}

internal class FilterByVerbCategory(
    layout: Int,
    filterByMethod: FilterByMethod,
    private val advanceFilterCategoryItemClickListener: AdvanceFilterCategoryItemClickListener
) :
    FilterCategoryConfig(layout), OnCheckedChangeListener {
    private var filterByVerbSelections = filterByMethod

    private lateinit var viewBinding: ChuckerFilterCategoryMethodBinding
    override fun inflateViewBinding(parent: ViewGroup): ViewBinding {
        return ChuckerFilterCategoryMethodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).also { viewBinding = it }
    }

    override fun bindViewState(viewBinding: ViewBinding) {
        val layout = (viewBinding as ChuckerFilterCategoryMethodBinding)
        layout.chipGet.isChecked = filterByVerbSelections.get
        layout.chipPost.isChecked = filterByVerbSelections.post
        layout.chipPut.isChecked = filterByVerbSelections.put
        layout.chipPost.setOnCheckedChangeListener(this)
        layout.chipPut.setOnCheckedChangeListener(this)
        layout.chipGet.setOnCheckedChangeListener(this)
    }

    override fun onCheckedChanged(chip: CompoundButton?, p1: Boolean) {
        when (chip?.id) {
            viewBinding.chipGet.id -> {
                filterByVerbSelections = FilterByMethod(p1, filterByVerbSelections.post, filterByVerbSelections.put)
                viewBinding.chipGet.isChecked = p1
            }
            viewBinding.chipPost.id -> {
                filterByVerbSelections = FilterByMethod(filterByVerbSelections.get, p1, filterByVerbSelections.put)
                viewBinding.chipPost.isChecked = p1
            }
            viewBinding.chipPut.id -> {
                filterByVerbSelections = FilterByMethod(filterByVerbSelections.get, filterByVerbSelections.post, p1)
                viewBinding.chipPut.isChecked = p1
            }
        }
        advanceFilterCategoryItemClickListener
            .onFilterCategoryClick(
                FilterByMethodCommand("Method", filterByVerbSelections)
            )
    }
}

internal interface AdvanceFilterCategoryItemClickListener {
    fun onFilterCategoryClick(filterCommand: FilterCommand)
}
