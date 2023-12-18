/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.navigation

import app.cash.sqldelight.coroutines.mapToList
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import me.gustavolopezxyz.common.data.Category
import me.gustavolopezxyz.common.db.CategoryRepository
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class ManageCategoriesComponent(
    componentContext: ComponentContext,
    override val di: DI,
    val onShowCategorySummary: (categoryId: Long) -> Unit
) : DIAware, ComponentContext by componentContext {
    private val categoryRepository: CategoryRepository by instance()

    fun getCategories() = categoryRepository.allAsFlow().mapToList(Dispatchers.IO)

    fun createCategory(name: String, parentCategoryId: Long?) {
        categoryRepository.create(name, parentCategoryId)
    }

    fun editCategory(modified: Category) {
        categoryRepository.update(modified)
    }
}