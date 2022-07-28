/**
 * Created by Spencer Searle on 4/27/21.
 */

package com.zol.updater.activities

import android.content.Context
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.zol.updater.events.EventProvider
import com.zol.updater.viewmodels.BaseViewModel
import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.asLiveData
import com.zol.updater.BR
import timber.log.Timber
import javax.inject.Inject

abstract class MVVMActivity<B : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity() {

    @Inject
    open lateinit var appContext: Context

    @Suppress("MemberVisibilityCanBePrivate")
    var isTransactionSafe = true
    var isTransactionPending = false

    protected abstract val layoutID: Int
    protected lateinit var binding: B
    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.d("${this::class.java.simpleName}.onCreate()")

        binding = DataBindingUtil.setContentView(this, layoutID)

        binding.lifecycleOwner = this

        EventProvider.getEvents().asLiveData(Dispatchers.IO).observe(this) { e ->
            handleEvent(e)
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        if (::viewModel.isInitialized) {
            binding.setVariable(BR.vm, viewModel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("${this::class.java.simpleName}.onDestroy()")
    }

    override fun onStart() {
        super.onStart()
        Timber.d("${this::class.java.simpleName}.onStart()")
    }

    override fun onStop() {
        super.onStop()
        Timber.d("${this::class.java.simpleName}.onStop()")

    }

    override fun onResume() {
        super.onResume()

        Timber.d("${this::class.java.simpleName}.onResume()")
    }

    override fun onPostResume() {
        super.onPostResume()
        isTransactionSafe = true
    }

    override fun onPause() {
        super.onPause()

        Timber.d("${this::class.java.simpleName}.onPause()")

        isTransactionSafe = false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Timber.d("${this::class.java.simpleName}.onCreateOptionsMenu()")

        return super.onCreateOptionsMenu(menu)
    }

    open fun handleEvent(event: Any) { /* Intentionally blank, override in activity */
        Timber.d("Handle the event ${event}")
    }
}