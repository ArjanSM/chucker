package com.chuckerteam.chucker.internal.ui.filter

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
import com.chuckerteam.chucker.internal.ui.MainViewModel

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
            viewGroup.removeAllViews()
            when (it.commandName) {
                resources.getString(R.string.chucker_method) -> {
                    val chuckerFilterCategoryMethodBinding =
                        ChuckerFilterCategoryMethodBinding
                            .inflate(inflator, viewGroup, false)
                    viewGroup.addView(chuckerFilterCategoryMethodBinding.root)
                    it.renderUI(chuckerFilterCategoryMethodBinding)
                }
                resources.getString(R.string.chucker_scheme) -> {
                    val chuckerFilterCategorySchemeBinding =
                        ChuckerFilterCategorySchemeBinding
                            .inflate(inflator, viewGroup, false)
                    viewGroup.addView(chuckerFilterCategorySchemeBinding.root)
                    it.renderUI(chuckerFilterCategorySchemeBinding)
                }
            }
        }
    }
}
