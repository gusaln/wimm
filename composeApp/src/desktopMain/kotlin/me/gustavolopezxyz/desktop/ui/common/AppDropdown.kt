/*
 * Copyright (c) 2024. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
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


@Composable
internal fun <T> AppDropdown(
    value: T?,
    onSelect: (value: T?) -> Unit,
    items: List<T>,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    itemText: @Composable ((T) -> Unit),
    anchor: @Composable ((T?) -> Unit),
) {
    Box(modifier = Modifier.wrapContentWidth().then(modifier)) {
//        Row {
        anchor(value)

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.widthIn(300.dp, 450.dp)
        ) {
            items.forEach {
                DropdownMenuItem(
                    text = { itemText(it) },
                    onClick = {
                        onSelect(it)
                        onExpandedChange(false)
                    })
            }
        }
//        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun <T> AppDropdown(
    value: T?,
    onSelect: (value: T?) -> Unit,
    items: List<T>,
    modifier: Modifier = Modifier,
    anchorLabel: String = "",
    anchorValue: ((T) -> String) = { it.toString() },
    isError: Boolean = false,
    itemText: @Composable ((T) -> Unit),
) {
    var isExpanded by remember { mutableStateOf(false) }

    var iconRotation by remember { mutableStateOf(0f) }
    val animatedIconRotation by animateFloatAsState(iconRotation)
    LaunchedEffect(isExpanded) {
        iconRotation = if (isExpanded) 180f else 0f
    }

    AppDropdown(
        value,
        onSelect,
        items,
        isExpanded,
        onExpandedChange = { isExpanded = it },
        modifier = modifier,
        itemText = itemText
    ) {
        TextField(
            value = if (it != null) anchorValue(it) else "",
            onValueChange = {},
            modifier = modifier
                .fillMaxWidth()
                .onPointerEvent(PointerEventType.Press) {
                    if (!isExpanded) isExpanded = true
                },
            readOnly = true,
            label = { Text(anchorLabel) },
            isError = isError,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "dropdown icon",
                    modifier = Modifier.rotate(animatedIconRotation)
                )
            },
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun <T> AppOutlinedDropdown(
    value: T?,
    onSelect: (value: T?) -> Unit,
    items: List<T>,
    modifier: Modifier = Modifier,
    anchorLabel: String = "",
    anchorValue: ((T) -> String) = { it.toString() },
    isError: Boolean = false,
    itemText: @Composable ((T) -> Unit),
) {
    var isExpanded by remember { mutableStateOf(false) }

    var iconRotation by remember { mutableStateOf(0f) }
    val animatedIconRotation by animateFloatAsState(iconRotation)
    LaunchedEffect(isExpanded) {
        iconRotation = if (isExpanded) 180f else 0f
    }

    AppDropdown(
        value,
        onSelect,
        items,
        isExpanded,
        onExpandedChange = { isExpanded = it },
        modifier = modifier,
        itemText = itemText
    ) {
        OutlinedTextField(
            value = if (it != null) anchorValue(it) else "",
            onValueChange = {},
            modifier = modifier
                .fillMaxWidth()
                .onPointerEvent(PointerEventType.Press) {
                    if (!isExpanded) isExpanded = true
                },
            readOnly = true,
            label = { Text(anchorLabel) },
            isError = isError,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "dropdown icon",
                    modifier = Modifier.rotate(animatedIconRotation)
                )
            },
        )
    }
}