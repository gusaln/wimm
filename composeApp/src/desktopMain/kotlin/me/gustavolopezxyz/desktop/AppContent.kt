/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import me.gustavolopezxyz.desktop.navigation.RootComponent
import me.gustavolopezxyz.desktop.screens.*

@Composable
fun AppContent(component: RootComponent) {
    Surface {
        Column {
            Spacer(Modifier.height(48.dp))

            Children(
                stack = component.stack,
                modifier = Modifier.fillMaxSize(),
                animation = stackAnimation(fade()),
            ) {
                when (val child = it.instance) {
                    is RootComponent.Child.Overview -> OverviewScreen(child.component)

                    is RootComponent.Child.ManageAccounts -> ManageAccountsScreen(child.component)
                    is RootComponent.Child.AccountSummary -> AccountSummaryScreen(child.component)

                    is RootComponent.Child.ManageCategories -> ManageCategoriesScreen(child.component)
                    is RootComponent.Child.CategorySummary -> CategoryMonthlySummaryScreen(child.component)
                    is RootComponent.Child.EditTransaction -> EditTransactionScreen(child.component)
                }
            }
        }
    }
}
