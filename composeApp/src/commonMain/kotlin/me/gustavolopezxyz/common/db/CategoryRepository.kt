/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import app.cash.sqldelight.coroutines.asFlow
import me.gustavolopezxyz.common.data.Category

class CategoryRepository(private val db: Database) {
    private val categoryQueries: CategoryQueries = db.categoryQueries

    fun getAll() = categoryQueries.selectAllCategories().executeAsList()

    fun getById(ids: Collection<Long>) = categoryQueries.selectCategoryById(ids).executeAsList()

    fun findById(id: Long) = categoryQueries.selectCategoryById(listOf(id)).executeAsOne()

    fun allAsFlow() = categoryQueries.selectAllCategories().asFlow()

    fun countTransactions(id: Long) = categoryQueries.countTransactionsOfCategory(id).executeAsOne()

    fun countChildren(id: Long) = categoryQueries.countChildrenOfCategory(id).executeAsOne()

    fun create(category: Category) {
        return create(category.name, category.parentCategoryId)
    }

    fun create(name: String, parentCategoryId: Long? = null) {
        return categoryQueries.insertCategory(parentCategoryId = parentCategoryId, name = name)
    }

    fun update(modified: Category) = update(
        modified.categoryId, modified.name, modified.parentCategoryId
    )

    private fun update(categoryId: Long, name: String, parentCategoryId: Long? = null) {
        return categoryQueries.updateCategory(name = name, parentCategoryId = parentCategoryId, categoryId = categoryId)
    }

    fun delete(categoryId: Long) {
        categoryQueries.deleteCategory(categoryId)
    }
}