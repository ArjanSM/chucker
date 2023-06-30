package com.chuckerteam.chucker.internal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.chuckerteam.chucker.R
import com.chuckerteam.chucker.databinding.ChuckerFilterCategoryMethodBinding
import com.chuckerteam.chucker.databinding.ChuckerFilterCategorySchemeBinding
import com.chuckerteam.chucker.databinding.ChuckerFragmentFilterCategoryContentBinding

internal class FilterCategoryContentFragment : Fragment() {
    private lateinit var viewBinding: ChuckerFragmentFilterCategoryContentBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = ChuckerFragmentFilterCategoryContentBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val inflator = LayoutInflater.from(context)
        viewModel.currentFilterCategory.observe(viewLifecycleOwner) {
            val viewGroup = viewBinding.fragmentCategoryViewContainer
            when (it) {
                resources.getString(R.string.chucker_method) -> {
                    viewGroup.removeAllViews()
                    val chuckerFilterCategoryMethodBinding =
                        ChuckerFilterCategoryMethodBinding
                            .inflate(inflator, viewGroup, false)
                    viewGroup.addView(chuckerFilterCategoryMethodBinding.root)
                    setupFiltersByMethodUI(chuckerFilterCategoryMethodBinding)
                }
                resources.getString(R.string.chucker_scheme) -> {
                    viewGroup.removeAllViews()
                    val chuckerFilterCategorySchemeBinding =
                        ChuckerFilterCategorySchemeBinding
                            .inflate(inflator, viewGroup, false)
                    viewGroup.addView(chuckerFilterCategorySchemeBinding.root)
                }
            }
        }
    }

    private fun setupFiltersByMethodUI(filterByMethodViewBinding: ChuckerFilterCategoryMethodBinding) {
        filterByMethodViewBinding.chuckerFilterCategoryMethodGet.isChecked =
            viewModel.additionalFilters?.filterByMethodData?.get ?: true
        filterByMethodViewBinding.chuckerFilterCategoryMethodPost.isChecked =
            viewModel.additionalFilters?.filterByMethodData?.post ?: true
        filterByMethodViewBinding.chuckerFilterCategoryMethodPut.isChecked =
            viewModel.additionalFilters?.filterByMethodData?.put ?: true
        setupFilterByMethodClickListerners(filterByMethodViewBinding)
    }

    private fun setupFilterByMethodClickListerners(filterByMethodViewBinding: ChuckerFilterCategoryMethodBinding) {
        filterByMethodViewBinding
            .chuckerFilterCategoryMethodGet
            .setOnCheckedChangeListener { compoundButton, isChecked ->
                viewModel.additionalFilters?.filterByMethodData?.get = isChecked
                viewModel.saveFilters()
            }
        filterByMethodViewBinding
            .chuckerFilterCategoryMethodPost
            .setOnCheckedChangeListener { compoundButton, isChecked ->
                viewModel.additionalFilters?.filterByMethodData?.post = isChecked
                viewModel.saveFilters()
            }
        filterByMethodViewBinding
            .chuckerFilterCategoryMethodPut
            .setOnCheckedChangeListener { compoundButton, isChecked ->
                viewModel.additionalFilters?.filterByMethodData?.put = isChecked
                viewModel.saveFilters()
            }
    }
}
