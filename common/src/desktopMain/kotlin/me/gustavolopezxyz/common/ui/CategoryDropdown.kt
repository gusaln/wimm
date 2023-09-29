/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import me.gustavolopezxyz.common.data.CategoryWithParent
import me.gustavolopezxyz.common.ui.theme.dropdownSelected
import me.gustavolopezxyz.common.ui.theme.dropdownUnselected

@Composable
fun CategoryDropdown(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    value: CategoryWithParent?,
    onSelect: (value: CategoryWithParent) -> Unit,
    categories: List<CategoryWithParent> = emptyList(),
    modifier: Modifier = Modifier,
    anchor: @Composable (() -> Unit),
) {
    Box {
        Row(modifier = modifier) {
            anchor()

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
                modifier = Modifier.widthIn(200.dp, 450.dp)
            ) {
                categories.forEach {
                    val isSelected = it.categoryId == value?.categoryId
                    val style =
                        if (isSelected) MaterialTheme.typography.dropdownSelected else MaterialTheme.typography.dropdownUnselected

                    DropdownMenuItem(text = {
                        Text(it.fullname(), style = style)
                    }, onClick = {
                        onSelect(it)
                        onExpandedChange(false)
                    })
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CategoryDropdown(
    label: String,
    value: CategoryWithParent?,
    onSelect: (value: CategoryWithParent) -> Unit,
    categories: List<CategoryWithParent> = emptyList(),
    modifier: Modifier = Modifier,
    leadingIcon: @Composable() (() -> Unit)? = null,
    trailingIcon: @Composable() (() -> Unit)? = null,
) {
    var isExpanded by remember { mutableStateOf(false) }

    val trailingIconComponent by derivedStateOf {
        trailingIcon ?: {
            var iconRotation by remember { mutableStateOf(0f) }
            val animatedIconRotation by animateFloatAsState(iconRotation)
            LaunchedEffect(isExpanded) {
                iconRotation = if (isExpanded) 180f else 0f
            }

            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "dropdown icon",
                modifier = Modifier.rotate(animatedIconRotation)
            )
        }
    }

    CategoryDropdown(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it },
        value = value,
        onSelect = onSelect,
        categories = categories,
        modifier = modifier,
    ) {
        Row {
            OutlinedTextField(value = value?.fullname() ?: "", onValueChange = {}, label = {
                Text(label)
            }, modifier = Modifier.fillMaxWidth().onPointerEvent(PointerEventType.Press) {
                if (!isExpanded) isExpanded = true
            }, leadingIcon = leadingIcon, trailingIcon = trailingIconComponent, readOnly = true
            )
        }
    }
}