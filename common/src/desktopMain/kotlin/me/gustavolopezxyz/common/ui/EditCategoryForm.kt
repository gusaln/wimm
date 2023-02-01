/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.CategoryWithParent
import me.gustavolopezxyz.common.ui.core.FormTitle

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
    var isParentDropDownExpanded by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(Constants.Size.Field.dp)
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
            expanded = isParentDropDownExpanded,
            onExpandedChange = { isParentDropDownExpanded = it },
            value = parent,
            onSelect = { parent = it },
            categories = categories
        ) {
            Row {
                OutlinedTextField(value = parent?.name ?: "none",
                    onValueChange = {},
                    label = {
                        Text("Subcategory of", modifier = Modifier.clickable(true) {
                            isParentDropDownExpanded = !isParentDropDownExpanded
                        })
                    },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = parent.let {
                        if (it == null) {
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
                        }
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "dropdown icon",
                            modifier = Modifier.clickable(true) {
                                isParentDropDownExpanded = !isParentDropDownExpanded
                            }
                        )
                    })
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Constants.Size.Medium.dp, Alignment.End)
        ) {
            Button(onClick = onEdit) {
                Text("Edit")
            }

            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    }
}