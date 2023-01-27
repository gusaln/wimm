/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.data

data class CategoryWithParent(
    val categoryId: Long,
    val parentCategoryId: Long?,
    val parentCategoryName: String?,
    val name: String,
    val isLocked: Boolean,
) {
    override fun toString(): String = """
  |CategoryWithParent [
  |  categoryId: $categoryId
  |  parentCategoryId: $parentCategoryId
  |  parentCategoryName: $parentCategoryName
  |  name: $name
  |  isLocked: $isLocked
  |]
  """.trimMargin()

    fun toCategory() = Category(
        this.categoryId,
        this.parentCategoryId,
        this.name,
        this.isLocked
    )

    fun fullname() = if (parentCategoryName != null) "$parentCategoryName / $name" else name
}