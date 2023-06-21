package com.chuckerteam.chucker.internal.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.chuckerteam.chucker.R
import com.chuckerteam.chucker.databinding.ChuckerFragmentFilterCategoryContentBinding

internal class FilterCategoryContentFragment : Fragment() {
    private lateinit var viewBinding: ChuckerFragmentFilterCategoryContentBinding
    private val viewModel: MainViewModel by viewModels({ requireParentFragment() })

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
            Log.i("FilterCategoryFragment2", "CurrentFilterCategory CHANGED!!!")
            val viewGroup = viewBinding.fragmentCategoryViewContainer
            when (it) {
                resources.getString(R.string.chucker_method) -> {
                    viewGroup.removeAllViews()
                    val childView = inflator.inflate(R.layout.chucker_filter_category_method, viewGroup, false)
                    viewGroup.addView(childView)
                }
                resources.getString(R.string.chucker_scheme) -> {
                    viewGroup.removeAllViews()
                    val childView = inflator.inflate(R.layout.chucker_filter_category_scheme, viewGroup, false)
                    viewGroup.addView(childView)
                }
            }
        }
    }
}
