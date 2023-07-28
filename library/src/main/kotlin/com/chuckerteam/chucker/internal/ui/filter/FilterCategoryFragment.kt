package com.chuckerteam.chucker.internal.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chuckerteam.chucker.databinding.ChuckerFragmentFilterCategoryBinding
import com.chuckerteam.chucker.internal.ui.MainViewModel
import com.chuckerteam.chucker.internal.ui.filter.command.FilterByMethodCommand
import com.chuckerteam.chucker.internal.ui.filter.command.FilterBySchemeCommand
import com.chuckerteam.chucker.internal.ui.filter.command.FilterCommand

internal class FilterCategoryFragment : Fragment(), FilterCategoryItemClickListener {
    private lateinit var filterCategoryViewBinding: ChuckerFragmentFilterCategoryBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var filterCategoryAdapter: FilterCategoryAdapter
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        filterCategoryViewBinding = ChuckerFragmentFilterCategoryBinding.inflate(
            inflater,
            container,
            false
        )
        return filterCategoryViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = filterCategoryViewBinding.filterCategoryRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this.activity)

        viewModel.filterData.observe(viewLifecycleOwner) {
            filterCategoryAdapter = FilterCategoryAdapter(
                listOf(
                    FilterByMethodCommand("Method", it.filterByMethod),
                    FilterBySchemeCommand("Scheme", it.filterByScheme)
                ),
                this
            )
            recyclerView.adapter = filterCategoryAdapter
        }
    }

    override fun onFilterCategoryClick(filterCommand: FilterCommand) {
        viewModel.updateFilterCategory(filterCommand)
        viewModel.updateLastClickedFilter(filterCommand)
    }
}
