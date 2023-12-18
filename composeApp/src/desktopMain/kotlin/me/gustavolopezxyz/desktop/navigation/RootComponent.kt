/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import me.gustavolopezxyz.common.data.CategoryWithParent
import me.gustavolopezxyz.common.data.NewEntryDto
import org.kodein.di.DI

class RootComponent(
    private val di: DI,
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val navigation = StackNavigation<Config>()

    val stack: Value<ChildStack<*, Child>> =
        childStack(
            source = navigation,
            initialConfiguration = Config.Overview, // The initial child component is List
            serializer = Config.serializer(),
            handleBackButton = true, // Automatically pop from the stack on back button presses
            childFactory = ::child,
        )

    private val createTransactionComponent = mutableStateOf<CreateTransactionComponent?>(null)

    @Composable
    fun rememberIsCreateTransactionWindowOpen(): State<Boolean> {
        return derivedStateOf { createTransactionComponent.value != null }
    }

    fun onOpenCreateTransactionWindow(
        description: String? = null,
        details: String? = null,
        category: CategoryWithParent? = null,
        incurredAt: LocalDate? = null,
        entries: Collection<NewEntryDto>? = null,
    ) {
        createTransactionComponent.value =
            CreateTransactionComponent(di, description, details, category, incurredAt, entries)
    }

    fun onCloseCreateTransactionWindow() {
        createTransactionComponent.value = null
    }

    fun onNavigateToDashboard() {
        navigation.popTo(0)
        navigation.replaceCurrent(Config.Overview)
    }

    fun onNavigateToManageAccounts() {
        navigation.popTo(0)
        navigation.replaceCurrent(Config.ManageAccounts)
    }

    fun onNavigateToManageCategories() {
        navigation.popTo(0)
        navigation.replaceCurrent(Config.ManageCategories)
    }

    private fun child(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            is Config.Overview -> Child.Overview(
                OverviewComponent(componentContext = componentContext, di = di, onEditTransaction = {
                    navigation.push(Config.EditTransaction(it))
                }, onDuplicateTransaction = {})
            )

            is Config.ManageAccounts -> Child.ManageAccounts(
                ManageAccountsComponent(
                    componentContext = componentContext,
                    di = di,
                    onShowAccountSummary = { navigation.push(Config.AccountSummary(it)) })
            )

            is Config.AccountSummary -> Child.AccountSummary(
                AccountSummaryComponent(
                    componentContext = componentContext,
                    di = di,
                    accountId = config.accountId,
                    onSelectEntry = {},
                    onNavigateBack = { navigation.pop() })
            )

            is Config.ManageCategories -> Child.ManageCategories(
                ManageCategoriesComponent(componentContext = componentContext, di = di, onShowCategorySummary = { })
            )

            is Config.CategorySummary -> Child.CategorySummary(
                CategoryMonthlySummaryComponent(
                    componentContext = componentContext,
                    di = di,
                    categoryId = config.categoryId,
                    onSelectTransaction = {},
                    onNavigateBack = { navigation.pop() })
            )

            is Config.EditTransaction -> Child.EditTransaction(
                EditTransactionComponent(
                    componentContext = componentContext,
                    di = di,
                    transactionId = config.transactionId,
                    onNavigateBack = { navigation.pop() })
            )
        }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Overview : Config

        @Serializable
        data object ManageAccounts : Config

        @Serializable
        data class AccountSummary(val accountId: Long) : Config

        @Serializable
        data object ManageCategories : Config

        @Serializable
        data class CategorySummary(val categoryId: Long) : Config

        @Serializable
        data class EditTransaction(val transactionId: Long) : Config
    }

    sealed class Child {
        class Overview(val component: OverviewComponent) : Child()
        class ManageAccounts(val component: ManageAccountsComponent) : Child()
        class AccountSummary(val component: AccountSummaryComponent) : Child()
        class ManageCategories(val component: ManageCategoriesComponent) : Child()
        class CategorySummary(val component: CategoryMonthlySummaryComponent) : Child()

        class EditTransaction(val component: EditTransactionComponent) : Child()
    }
}

