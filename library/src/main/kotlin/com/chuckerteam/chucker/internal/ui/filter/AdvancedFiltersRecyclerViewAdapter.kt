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
import com.chuckerteam.chucker.internal.ui.filter.command.FilterAction
import com.chuckerteam.chucker.internal.ui.filter.command.FilterByMethod
import com.chuckerteam.chucker.internal.ui.filter.command.FilterByMethodAction
import com.chuckerteam.chucker.internal.ui.filter.command.FilterByScheme
import com.chuckerteam.chucker.internal.ui.filter.command.FilterBySchemeAction

internal class AdvancedFiltersRecyclerViewAdapter(
    private val viewConfigs: List<FilterViewConfig>
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

        fun populateUI(viewConfig: FilterViewConfig) {
            viewConfig.bindViewState(viewBinding)
        }
    }
    private fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
        val matchingConfig = viewConfigs.first { it.layout == viewType }
        return matchingConfig.inflateViewBinding(parent)
    }
}

internal abstract class FilterViewConfig(val layout: Int) {
    abstract fun inflateViewBinding(parent: ViewGroup): ViewBinding
    abstract fun bindViewState(viewBinding: ViewBinding)
}

internal class FilterBySchemeView(
    layout: Int,
    filterByScheme: FilterByScheme,
    private val advanceFilterCategoryItemClickListener: AdvanceFilterCategoryItemClickListener
) : FilterViewConfig(layout), OnCheckedChangeListener {
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
                filterBySchemeSelections = FilterByScheme(http = p1, https = filterBySchemeSelections.https)
            }
            viewBinding.chipHttps.id -> {
                filterBySchemeSelections = FilterByScheme(http = filterBySchemeSelections.http, https = p1)
            }
        }
        advanceFilterCategoryItemClickListener.onFilterCategoryClick(
            FilterBySchemeAction("Scheme", filterBySchemeSelections)
        )
    }
}

internal class FilterByVerbView(
    layout: Int,
    filterByMethod: FilterByMethod,
    private val advanceFilterCategoryItemClickListener: AdvanceFilterCategoryItemClickListener
) :
    FilterViewConfig(layout), OnCheckedChangeListener {
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
                FilterByMethodAction("Method", filterByVerbSelections)
            )
    }
}

internal interface AdvanceFilterCategoryItemClickListener {
    fun onFilterCategoryClick(filterCommand: FilterAction)
}
