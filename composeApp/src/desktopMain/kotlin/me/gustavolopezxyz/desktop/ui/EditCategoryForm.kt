/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.gustavolopezxyz.common.data.CategoryWithParent
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.desktop.ui.common.AppButton
import me.gustavolopezxyz.desktop.ui.common.AppTextButton
import me.gustavolopezxyz.desktop.ui.common.FormTitle

@Preview
@Composable
fun EditCategoryForm(
    categories: List<CategoryWithParent>,
    value: CategoryWithParent,
    onValueChange: (CategoryWithParent) -> Unit,
    onEdit: () -> Unit,
    onCancel: () -> Unit = {}
) {
    var parent by remember { mutableStateOf(categories.find { it.categoryId == value.parentCategoryId }) }

    Column(
        verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium)
    ) {
        FormTitle("Edit Account ${value.name}")

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value.name,
            onValueChange = { onValueChange(value.copy(name = it)) },
            label = { Text("Name") },
            placeholder = { Text("Awesome savings account") },
            singleLine = true
        )

        CategoryDropdown(
            label = "Subcategory of",
            value = parent,
            onSelect = { parent = it },
            categories = categories,
            leadingIcon =
            if (parent == null) {
                null
            } else {
                {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "clear icon",
                        modifier = Modifier.clickable(true) {
                            parent = null
                        }
                    )
                }
            },
        )
        Spacer(modifier = Modifier.fillMaxWidth())

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium, Alignment.End)
        ) {
            AppButton(onClick = onEdit, "Edit")

            AppTextButton(onClick = onCancel, "Cancel")
        }
    }
}