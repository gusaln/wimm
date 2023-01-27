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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.CategoryWithParent


@Composable
fun CategoriesListCard(category: CategoryWithParent, onSelect: (CategoryWithParent) -> Unit) {
    Row(
        modifier = Modifier.clickable(!category.isLocked) { onSelect(category) }
            .padding(Constants.Size.Small.dp).width(300.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.KeyboardArrowRight, "arrow marker")

        Text(
            category.fullname(),
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Normal)
        )
    }
}

@Composable
fun CategoriesList(categories: List<CategoryWithParent>, onSelect: (CategoryWithParent) -> Unit = {}) {
    val parents = categories.filter { it.parentCategoryId == null }
    val byParent = categories.filter { it.parentCategoryId != null }.groupBy { it.parentCategoryId!! }

    val loLoCaScroll = rememberScrollState()
    Row {
        Column(
            modifier = Modifier.scrollable(loLoCaScroll, Orientation.Vertical),
            verticalArrangement = Arrangement.spacedBy(Constants.Size.Large.dp)
        ) {
            parents.forEach { category ->
                // Container of a category subtree
                Column(modifier = Modifier.fillMaxWidth().width(300.dp)) {
                    Text(
                        category.name,
                        modifier = Modifier.clickable(!category.isLocked) { onSelect(category) }
                            .padding(Constants.Size.Small.dp)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.h6
                    )

                    val scroll = rememberScrollState()
                    Row(
                        modifier = Modifier.wrapContentHeight().scrollable(scroll, Orientation.Horizontal),
                        horizontalArrangement = Arrangement.spacedBy(Constants.Size.Medium.dp)
                    ) {
                        byParent[category.categoryId]?.forEach {
                            CategoriesListCard(it, onSelect)
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
            CategoryWithParent(1, 1, "alpha", "beta", false),
            CategoryWithParent(2, null, null, "single", false),
        )
    )
}