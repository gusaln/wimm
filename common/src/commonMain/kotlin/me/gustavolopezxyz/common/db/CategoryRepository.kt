/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import com.squareup.sqldelight.runtime.coroutines.asFlow
import me.gustavolopezxyz.common.data.Category
import me.gustavolopezxyz.common.data.Database
import me.gustavolopezxyz.db.CategoryQueries
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CategoryRepository : KoinComponent {
    private val db: Database by inject()
    private val categoryQueries: CategoryQueries = db.categoryQueries

    fun getAll() = categoryQueries.selectAllCategories().executeAsList()

    fun getById(ids: Collection<Long>) = categoryQueries.selectCategoryById(ids).executeAsList()

    fun findById(id: Long) = categoryQueries.selectCategoryById(listOf(id)).executeAsOne()

    fun allAsFlow() = categoryQueries.selectAllCategories().asFlow()

    fun create(category: Category) {
        return create(category.name, category.parentCategoryId)
    }

    fun create(name: String, parentCategoryId: Long? = null) {
        return categoryQueries.insertCategory(parentCategoryId = parentCategoryId, name = name)
    }

    fun update(original: Category, modified: Category) = update(
        original.categoryId, modified.name, modified.parentCategoryId
    )

    private fun update(categoryId: Long, name: String, parentCategoryId: Long? = null) {
        return categoryQueries.updateCategory(name = name, parentCategoryId = parentCategoryId, categoryId = categoryId)
    }

}