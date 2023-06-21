package com.chuckerteam.chucker.internal.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chuckerteam.chucker.databinding.ChuckerFilterCategoryBodyLineBinding

internal class FilterCategoryAdapter(
    val filterCategories: List<String>,
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
        holder.setCategoryText(filterCategories[position])
    }

    inner class FilterCategoryViewHolder(
        private val filterCategoryViewBinding: ChuckerFilterCategoryBodyLineBinding
    ) : RecyclerView.ViewHolder(filterCategoryViewBinding.root) {

        init {
            itemView.setOnClickListener {
                onFilterCategoryClickListener?.onFilterCategoryClick(
                    filterCategoryViewBinding.chuckerFilterCategoryLineTextview.text.toString()
                )
            }
        }

        fun setCategoryText(text: String) {
            filterCategoryViewBinding.chuckerFilterCategoryLineTextview.text = text
        }
    }
}

internal interface FilterCategoryItemClickListener {
    fun onFilterCategoryClick(filterCategory: String)
}
