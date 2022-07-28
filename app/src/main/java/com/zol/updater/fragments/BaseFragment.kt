/**
 * Created by Spencer Searle on 4/1/22.
 */

package com.zol.updater.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import com.zol.updater.events.EventProvider
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject

open class BaseFragment : Fragment() {

    /*
     * Workaround for R8 tripping over BaseFragment_MembersInjector not being in common-ui module and
     * instead being in middle modules
     */
    @Inject
    open lateinit var appContext: Context

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Timber.d("${this::class.simpleName}.onActivityCreated()")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("${this::class.simpleName}.onCreate()")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("${this::class.simpleName}.onCreateView()")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("${this::class.simpleName}.onViewCreated()")
        EventProvider.getEvents().asLiveData(Dispatchers.IO).observe(viewLifecycleOwner) { event ->
            handleEvent(event)
        }
    }

    override fun onStart() {
        super.onStart()
        Timber.d("${this::class.simpleName}.onStart()")
    }

    override fun onResume() {
        super.onResume()
        Timber.d("${this::class.simpleName}.onResume()")
    }

    override fun onPause() {
        super.onPause()
        Timber.d("${this::class.simpleName}.onPause()")
    }

    override fun onStop() {
        super.onStop()
        Timber.d("${this::class.simpleName}.onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("${this::class.simpleName}.onDestroy()")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("${this::class.simpleName}.onDestroyView()")
    }

    override fun onDetach() {
        super.onDetach()
        Timber.d("${this::class.simpleName}.onDetach()")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.d("${this::class.simpleName}.onAttach()")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        Timber.d("${this::class.simpleName}.onCreateOptionsMenu()")
    }

    open fun handleEvent(event: Any) { /* Intentionally blank, override in fragment */
        Timber.d("Handle the event")
        //(activity as MVVMActivity<*, *>).handleEvent(event)
    }
}