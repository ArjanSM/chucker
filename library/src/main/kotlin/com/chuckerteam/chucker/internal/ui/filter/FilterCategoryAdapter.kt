package com.chuckerteam.chucker.internal.ui.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chuckerteam.chucker.databinding.ChuckerFilterCategoryBodyLineBinding
import com.chuckerteam.chucker.internal.ui.filter.command.FilterCommand

internal class FilterCategoryAdapter(
    private val filterCategories: List<FilterCommand>,
    private val onFilterCategoryClickListener: FilterCategoryItemClickListener?
) : RecyclerView.Adapter<FilterCategoryAdapter.FilterCategoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterCategoryViewHolder {
        val viewBinding = ChuckerFilterCategoryBodyLineBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FilterCategoryViewHolder(viewBinding)
    }

    override fun getItemCount() = filterCategories.size

    override fun onBindViewHolder(holder: FilterCategoryViewHolder, position: Int) {
        holder.setFilterCommand(filterCategories[position])
    }

    inner class FilterCategoryViewHolder(
        private val filterCategoryViewBinding: ChuckerFilterCategoryBodyLineBinding
    ) : RecyclerView.ViewHolder(filterCategoryViewBinding.root) {

        private lateinit var filterCommand: FilterCommand

        init {
            itemView.setOnClickListener {
                onFilterCategoryClickListener?.onFilterCategoryClick(filterCommand)
            }
        }

        fun setFilterCommand(filterCommand: FilterCommand) {
            this.filterCommand = filterCommand
            setCategoryText(filterCommand.commandName)
        }

        private fun setCategoryText(text: String) {
            filterCategoryViewBinding.chuckerFilterCategoryLineTextview.text = text
        }
    }
}

internal interface FilterCategoryItemClickListener {
    fun onFilterCategoryClick(filterCommand: FilterCommand)
}
