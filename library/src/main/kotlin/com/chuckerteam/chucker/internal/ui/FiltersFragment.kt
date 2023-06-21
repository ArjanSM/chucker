package com.chuckerteam.chucker.internal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chuckerteam.chucker.databinding.ChuckerFragmentFiltersBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

internal class FiltersFragment : BottomSheetDialogFragment() {

    private lateinit var filtersBinding: ChuckerFragmentFiltersBinding

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
        }
        filtersBinding.filtersAccept.setOnClickListener {
            this.dismiss()
        }
    }
}
