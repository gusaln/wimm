/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable

/**
 * NavController
 *
 * [taken from](https://github.com/itheamc/navigation-for-compose-for-desktop)
 */
class NavController(
    private val startDestination: NavStackEntry,
    private var backStackScreens: MutableSet<NavStackEntry> = mutableSetOf()
) {
    constructor(startDestination: Screen) : this(startDestination.route)

    constructor(startDestination: String) : this(NavStackEntry(startDestination))

    // Variable to store the state of the current screen
    var currentScreen: MutableState<NavStackEntry> = mutableStateOf(startDestination)

    fun navigate(route: Route) = navigate(route.route, route.arguments)

    // Function to handle the navigation between the screen
    fun navigate(route: String, arguments: Map<String, String>? = null) {
        val entry = NavStackEntry(route, arguments)
        if (entry.toString() == currentScreen.value.toString()) {
            return
        }

        if (backStackScreens.contains(currentScreen.value) && currentScreen.value != startDestination) {
            backStackScreens.remove(currentScreen.value)
        }

        if (entry.toString() == startDestination.toString()) {
            backStackScreens = mutableSetOf()
        } else {
            backStackScreens.add(currentScreen.value)
        }

        currentScreen.value = entry
    }

    // Function to handle the back
    fun navigateBack() {
        if (backStackScreens.isNotEmpty()) {
            currentScreen.value = backStackScreens.last()
            backStackScreens.remove(currentScreen.value)
        }
    }

    fun getArgument(name: String): String? = currentScreen.value.arguments?.get(name)

    data class NavStackEntry(val route: String, val arguments: Map<String, String>? = null) {
        override fun toString(): String {
            var s = route

            arguments?.forEach {
                s = s.replace("{${it.key}}", it.value)
            }

            return s
        }
    }
}

data class Route(val route: String, val arguments: Map<String, String>? = null)

/**
 * Composable to remember the state of the NavController
 *
 * [taken from](https://github.com/itheamc/navigation-for-compose-for-desktop)
 *
 * @see NavController
 */
@Composable
fun rememberNavController(navController: NavController): MutableState<NavController> = rememberSaveable {
    mutableStateOf(navController)
}