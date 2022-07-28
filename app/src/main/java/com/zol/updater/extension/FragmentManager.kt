/**
 * Created by Chris Renfrow on 3/20/18.
 */

@file:Suppress("unused")

package com.smartrent.common.ui.extension

import androidx.annotation.AnimRes
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.NavHostFragment

inline fun FragmentManager.inTransaction(
    addToBackstack: Boolean,
    tag: String?,
    func: FragmentTransaction.() -> Unit
) {
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.func()
    if (addToBackstack) fragmentTransaction.addToBackStack(tag)
    fragmentTransaction.commit()
}

inline fun <reified T : Fragment> FragmentManager.find(tag: String?): T? =
    findFragmentByTag(tag) as? T

inline fun <reified T : Fragment> FragmentManager.findOrNewFragment(
    tag: String?,
    func: () -> T
): T =
    findFragmentByTag(tag) as? T ?: func()

inline fun <reified T : NavHostFragment> FragmentManager.replace(
    @IdRes viewID: Int,
    addToBackstack: Boolean,
    tag: String?,
    func: () -> T
) {
    replace(viewID, addToBackstack, tag, func())
}

inline fun <reified T : NavHostFragment> FragmentManager.replace(
    @IdRes viewID: Int,
    addToBackstack: Boolean,
    tag: String?,
    navHost: T
) = inTransaction(
    addToBackstack,
    tag
) {

    replace(viewID, navHost)
    setPrimaryNavigationFragment(navHost)
}

inline fun <reified T : Fragment> FragmentManager.replace(
    @IdRes viewID: Int,
    addToBackstack: Boolean,
    tag: String?,
    @AnimRes enterAnim: Int?,
    @AnimRes exitAnim: Int?,
    @AnimRes popEnterAnim: Int?,
    @AnimRes popExitAnim: Int?,
    func: () -> T
) = inTransaction(
    addToBackstack,
    tag
) {
    if (enterAnim != null && exitAnim != null) {
        if (popEnterAnim != null && popExitAnim != null) {
            setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
        } else {
            setCustomAnimations(enterAnim, exitAnim)
        }
    }

    replace(viewID, func(), tag)
}