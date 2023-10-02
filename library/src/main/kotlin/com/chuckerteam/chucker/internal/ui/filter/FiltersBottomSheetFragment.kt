package com.chuckerteam.chucker.internal.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chuckerteam.chucker.R
import com.chuckerteam.chucker.databinding.ChuckerFragmentBottomSheetBinding
import com.chuckerteam.chucker.internal.ui.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

public class FiltersBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var filterCategoryViewBinding: ChuckerFragmentBottomSheetBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var filterCategoryAdapter: AdvancedFiltersRecyclerViewAdapter
    private val viewModel: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        filterCategoryViewBinding = ChuckerFragmentBottomSheetBinding.inflate(inflater)
        return filterCategoryViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = filterCategoryViewBinding.chuckerFilterRecyclerview
        recyclerView.layoutManager = LinearLayoutManager(this.activity)
        viewModel.filterData.observe(viewLifecycleOwner) {
            filterCategoryAdapter = AdvancedFiltersRecyclerViewAdapter(
                listOf(
                    FilterBySchemeCategory(R.layout.chucker_filter_category_scheme, it.filterByScheme),
                    FilterByVerbCategory(R.layout.chucker_filter_category_method, it.filterByMethod)
                )
            )
            recyclerView.adapter = filterCategoryAdapter
            filterCategoryAdapter.notifyItemRangeChanged(0, 2)
        }
    }
}
