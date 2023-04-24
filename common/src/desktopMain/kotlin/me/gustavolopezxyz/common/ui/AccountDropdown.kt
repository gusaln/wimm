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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.ext.toMoney
import me.gustavolopezxyz.common.ui.theme.dropdownSelected
import me.gustavolopezxyz.common.ui.theme.dropdownUnselected

@Composable
fun AccountDropdown(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    value: Account?,
    onSelect: (value: Account) -> Unit,
    accounts: List<Account> = emptyList(),
    modifier: Modifier = Modifier,
    anchor: @Composable (() -> Unit),
) {
    val onSelectAccount by rememberUpdatedState(onSelect)

    Box(modifier = modifier) {
        Row {
            anchor()

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
                modifier = Modifier.widthIn(300.dp, 450.dp)
            ) {
                accounts.forEach {
                    val isSelected = it.accountId == value?.accountId
                    val style =
                        if (isSelected) MaterialTheme.typography.dropdownSelected else MaterialTheme.typography.dropdownUnselected

                    DropdownMenuItem(onClick = {
                        onSelectAccount(it)
                        onExpandedChange(false)
                    }) {
                        Text(buildAnnotatedString {
                            append(it.name)
                            append(' ')

                            withStyle(
                                SpanStyle(color = Color.Gray, fontSize = MaterialTheme.typography.caption.fontSize)
                            ) {
                                append("[${it.type.name}; ${it.balance.toMoney(it.currency)}]")
                            }
                        }, style = style)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AccountDropdown(
    label: String,
    value: Account?,
    onSelect: (value: Account) -> Unit,
    accounts: List<Account> = emptyList(),
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }

    var iconRotation by remember { mutableStateOf(0f) }
    val animatedIconRotation by animateFloatAsState(iconRotation)
    LaunchedEffect(isExpanded) {
        iconRotation = if (isExpanded) 180f else 0f
    }

    AccountDropdown(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it },
        value = value,
        onSelect = onSelect,
        accounts = accounts,
        modifier = modifier,
    ) {
        Row {
            OutlinedTextField(
                value = if (value != null) "${value.name} (${value.balance.toMoney(value.currency)})" else "",
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