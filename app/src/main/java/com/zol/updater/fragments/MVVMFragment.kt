/**
 * Created by Spencer Searle on 4/1/22.
 */

package com.zol.updater.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.zol.updater.viewmodels.BaseViewModel
import javax.inject.Inject
import com.zol.updater.BR

abstract class MVVMFragment<B : ViewDataBinding, VM : BaseViewModel> : BaseFragment() {

    /*
     * Workaround for R8 tripping over MVVMActivity_MembersInjector not being in common-ui module and
     * instead being in middle modules
     */
    @Suppress("LateinitVarOverridesLateinitVar")
    @Inject
    override lateinit var appContext: Context

    protected abstract val layoutID: Int
    protected lateinit var binding: B
    protected lateinit var viewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, savedInstanceState)

        binding = DataBindingUtil.inflate(inflater, layoutID, container, false)

        if (this::viewModel.isInitialized){
            binding.lifecycleOwner = this

            binding.setVariable(BR.vm, viewModel)

            return binding.root
        }

        return null
    }
}