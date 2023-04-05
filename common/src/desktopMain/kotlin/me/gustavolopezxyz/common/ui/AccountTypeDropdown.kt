/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import me.gustavolopezxyz.common.data.AccountType
import me.gustavolopezxyz.common.ui.theme.dropdownSelected
import me.gustavolopezxyz.common.ui.theme.dropdownUnselected

@Composable
fun AccountTypeDropdown(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    value: AccountType?,
    onSelect: (value: AccountType) -> Unit,
    types: List<AccountType> = AccountType.values().toList(),
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
                types.forEach {
                    val isSelected = it == value
                    val style =
                        if (isSelected) MaterialTheme.typography.dropdownSelected else MaterialTheme.typography.dropdownUnselected

                    DropdownMenuItem(onClick = {
                        onSelect(it)
                        onExpandedChange(false)
                    }) {
                        Text(it.name, style = style)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AccountTypeDropdown(
    label: String,
    value: AccountType?,
    onSelect: (AccountType) -> Unit,
    types: List<AccountType> = AccountType.values().toList(),
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }

    var iconRotation by remember { mutableStateOf(0f) }
    val animatedIconRotation by animateFloatAsState(iconRotation)
    LaunchedEffect(isExpanded) {
        iconRotation = if (isExpanded) 180f else 0f
    }

    AccountTypeDropdown(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it },
        value = value,
        onSelect = onSelect,
        types = types,
        modifier = modifier,
    ) {
        Row {
            OutlinedTextField(
                value = value?.name ?: "",
                onValueChange = {},
                label = {
                    Text(label)
                },
                modifier = Modifier.fillMaxWidth()
                    .onPointerEvent(PointerEventType.Press) {
                        if (!isExpanded) isExpanded = true
                    },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "dropdown icon",
                        modifier = Modifier.rotate(animatedIconRotation)
                    )
                },
                readOnly = true
            )
        }
    }
}