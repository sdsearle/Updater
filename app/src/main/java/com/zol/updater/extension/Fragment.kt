/**
 * Created by Chris Renfrow on 7/6/18.
 */

package com.smartrent.common.ui.extension

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

inline fun <reified T : ViewModel> Fragment.getViewModel(owner: ViewModelStoreOwner = this): T {
    return ViewModelProvider(owner).get(T::class.java)
}

inline fun <reified T : ViewModel> Fragment.getViewModel(factory: ViewModelProvider.Factory): T {
    return getViewModel(this, factory)
}

@Suppress("unused")
inline fun <reified T : ViewModel> Fragment.getViewModel(
    owner: ViewModelStoreOwner,
    factory: ViewModelProvider.Factory
): T {
    return ViewModelProvider(owner, factory).get(T::class.java)
}
