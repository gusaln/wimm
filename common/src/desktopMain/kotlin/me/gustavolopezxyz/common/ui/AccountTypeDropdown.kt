/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import me.gustavolopezxyz.common.data.AccountType
import me.gustavolopezxyz.common.ui.theme.dropdownSelected
import me.gustavolopezxyz.common.ui.theme.dropdownUnselected

@Composable
fun AccountTypeDropdown(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    value: AccountType?,
    onClick: (value: AccountType) -> Unit,
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
                        onClick(it)
                        onExpandedChange(false)
                    }) {
                        Text(buildAnnotatedString {
                            append(it.name)
                            append(" ")

                            withStyle(
                                SpanStyle(
                                    color = Color.Gray, fontSize = MaterialTheme.typography.caption.fontSize
                                )
                            ) {
                                append(
                                    if (it.isDebit()) "(debit)" else "(credit)"
                                )
                            }
                        }, style = style)
                    }
                }
            }
        }
    }
}