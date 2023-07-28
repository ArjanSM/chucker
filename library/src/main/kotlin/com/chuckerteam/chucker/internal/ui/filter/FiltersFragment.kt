package com.chuckerteam.chucker.internal.ui.filter

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.activityViewModels
import com.chuckerteam.chucker.R
import com.chuckerteam.chucker.databinding.ChuckerFragmentFiltersBinding
import com.chuckerteam.chucker.internal.ui.MainViewModel
import com.chuckerteam.chucker.internal.ui.filter.command.FilterCommand
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

internal class FiltersFragment : BottomSheetDialogFragment() {

    private lateinit var filtersBinding: ChuckerFragmentFiltersBinding
    private val viewModel: MainViewModel by activityViewModels()
    private val filterCommands: MutableSet<FilterCommand> = mutableSetOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        filtersBinding = ChuckerFragmentFiltersBinding.inflate(inflater, container, false)
        return filtersBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        filtersBinding.filterCancel.setOnClickListener {
            this.dismiss()
            filterCommands.filter { it.hasChanged() }.forEach { it.undoFilterCommand() }
            filterCommands.clear()
        }
        filtersBinding.filtersAccept.setOnClickListener {
            this.dismiss()
            filterCommands.filter { it.hasChanged() }.forEach { viewModel.updateFilter(it) }
        }

        dialog?.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as Dialog
            bottomSheetDialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)?.let {
                BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        viewModel.lastClickedFilter.observe(this.viewLifecycleOwner) { filterCommand ->
            filterCommands.add(filterCommand)
        }
    }
}
