/**
 * Created by Spencer Searle on 4/1/22.
 */

package com.zol.updater.fragments

import android.os.Bundle
import com.smartrent.common.ui.extension.getViewModel
import com.zol.updater.R
import com.zol.updater.databinding.FragmentUpdateBinding
import com.zol.updater.viewmodels.UpdateViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class UpdateFragment() :
    MVVMFragment<FragmentUpdateBinding, UpdateViewModel>() {
    @Inject
    lateinit var updateViewModel: UpdateViewModel

    override val layoutID: Int = R.layout.fragment_update

        companion object{
            const val tag = "HelloFragment"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("ZOL Fragment Created")
        viewModel = updateViewModel
    }
}