/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.gustavolopezxyz.common.data.CategoryWithParent
import me.gustavolopezxyz.common.ui.theme.AppDimensions


@Composable
fun CategoriesListCard(
    category: CategoryWithParent, onSelect: (CategoryWithParent) -> Unit, onDelete: (CategoryWithParent) -> Unit
) {
    Row(
        modifier = Modifier.clickable(!category.isLocked) { onSelect(category) }
            .padding(AppDimensions.Default.padding.small)
            .width(300.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row {
            Icon(Icons.Default.KeyboardArrowRight, "arrow marker")

            Text(
                category.fullname(),
                modifier = Modifier.weight(1f),
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Normal)
            )
        }

        IconButton(onClick = { onDelete(category) }) {
            Icon(Icons.Default.Delete, "delete category")
        }
    }
}

@Composable
fun CategoriesList(
    categories: List<CategoryWithParent>,
    onSelect: (CategoryWithParent) -> Unit = {},
    onDelete: (CategoryWithParent) -> Unit = {}
) {
    val parents = categories.filter { it.parentCategoryId == null }
    val byParent = categories.filter { it.parentCategoryId != null }.groupBy { it.parentCategoryId!! }

    val loLoCaScroll = rememberScrollState()
    Row {
        Column(
            modifier = Modifier.scrollable(loLoCaScroll, Orientation.Vertical),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.large)
        ) {
            val iconSize = Modifier.size(20.dp)

            parents.forEach { parentCategory ->
                // Container of a category subtree
                Column(modifier = Modifier.fillMaxWidth().width(300.dp)) {
                    Row(
                        modifier = Modifier.padding(AppDimensions.Default.padding.small).width(300.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(parentCategory.name,
                            modifier = Modifier.clickable(!parentCategory.isLocked) { onSelect(parentCategory) }
                                .padding(AppDimensions.Default.padding.small),
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.h6
                        )

                        IconButton(onClick = { onDelete(parentCategory) }) {
                            Icon(Icons.Default.Delete, "delete category", iconSize)
                        }
                    }

                    val scroll = rememberScrollState()
                    Row(
                        modifier = Modifier.wrapContentHeight().scrollable(scroll, Orientation.Horizontal),
                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium)
                    ) {
                        byParent[parentCategory.categoryId]?.forEach {
                            CategoriesListCard(it, onSelect, onDelete)
                        }
                    }
                }
            }
        }

        VerticalScrollbar(rememberScrollbarAdapter(loLoCaScroll))
    }
}


@Preview
@Composable
fun CategoriesListPreview() {
    CategoriesList(
        listOf(
            CategoryWithParent(1, null, null, "alpha", true),
            CategoryWithParent(10, 1, "alpha", "beta", false),
            CategoryWithParent(11, null, null, "single", false),
        )
    )
}