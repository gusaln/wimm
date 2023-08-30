/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import me.gustavolopezxyz.common.data.CategoryWithParent
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.common.ui.theme.AppTheme


const val rootCategory = "Root"

@Composable
fun CategoriesGroupedList(
    categories: List<CategoryWithParent>,
    onSelect: (CategoryWithParent) -> Unit = {},
    onEdit: ((CategoryWithParent) -> Unit)? = null,
) {
    val scroll = rememberScrollState()
    val byType by derivedStateOf { categories.groupBy { it.parentCategoryName ?: rootCategory } }

    Column(
        modifier = Modifier.verticalScroll(scroll).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.large)
    ) {
        byType.forEach { (parentName, categories) ->
            Text(parentName)

            CategoriesList(categories.sortedBy { it.fullname() }, onSelect, onEdit)

            Spacer(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun CategoriesList(
    categories: List<CategoryWithParent>,
    onSelect: (CategoryWithParent) -> Unit = {},
    onEdit: ((CategoryWithParent) -> Unit)? = null,
) {
    categories.chunked(5).forEach { categoriesChunk ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = AppDimensions.Default.listSpaceBetween),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium)
        ) {
            categoriesChunk.forEach { category ->
                CategoriesListCard(category, onSelect, onEdit)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CategoriesListCard(
    category: CategoryWithParent,
    onSelect: (CategoryWithParent) -> Unit,
    onEdit: ((CategoryWithParent) -> Unit)? = null,
) {
    Card(
        modifier = Modifier.widthIn(200.dp, 350.dp).clickable { onSelect(category) }.pointerHoverIcon(
            PointerIcon.Hand
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.small)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(category.name)

                if (onEdit != null) {
                    IconButton(onClick = { onEdit(category) }, modifier = Modifier.size(16.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "edit category",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun CategoriesGroupedListPreview() {
    AppTheme {
        CategoriesGroupedList(
            listOf(
                CategoryWithParent(1, null, null, "Savings", false),
                CategoryWithParent(2, 1, "Savings", "Stuff", false),
            ),
            onEdit = {}
        )
    }
}