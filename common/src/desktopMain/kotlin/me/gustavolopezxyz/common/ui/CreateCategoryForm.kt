/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.gustavolopezxyz.common.data.CategoryWithParent
import me.gustavolopezxyz.common.ui.common.AppButton
import me.gustavolopezxyz.common.ui.common.AppTextButton
import me.gustavolopezxyz.common.ui.common.FormTitle
import me.gustavolopezxyz.common.ui.theme.AppDimensions

@Preview
@Composable
fun CreateCategoryForm(
    categories: List<CategoryWithParent>,
    onCreate: (name: String, parentCategoryId: Long?) -> Unit,
    onCancel: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var parent by remember { mutableStateOf<CategoryWithParent?>(null) }

    fun handleCreate() {
        name = name.trimEnd()

        if (name.isNotEmpty()) onCreate(name.trimEnd(), parent?.categoryId)
    }

    fun handleCancel() {
        name = ""
        parent = null

        onCancel()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.fieldSpacing)
    ) {
        FormTitle("Create a Category")

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = name,
            onValueChange = { s -> name = s.trimStart().lowercase().filter { it != '/' } },
            label = { Text("Name") },
            placeholder = { Text("Must be unique and can't contain a '/'") },
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
            AppButton(onClick = ::handleCreate, "Create")

            AppTextButton(onClick = ::handleCancel, "Cancel")
        }
    }
}