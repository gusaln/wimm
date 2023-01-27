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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import me.gustavolopezxyz.common.data.Account

@Composable
fun AccountDropdown(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    value: Account?,
    onClick: (value: Account) -> Unit,
    accounts: List<Account> = listOf(),
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
                accounts.forEach {
                    val isSelected = it.id == value?.id
                    val style = if (isSelected) {
                        MaterialTheme.typography.body1.copy(
                            fontWeight = FontWeight.Bold, color = MaterialTheme.colors.secondary
                        )
                    } else {
                        MaterialTheme.typography.body1.copy(
                            fontWeight = FontWeight.Normal, color = MaterialTheme.colors.onSurface
                        )
                    }

                    DropdownMenuItem(onClick = {
                        onClick(it)
                        onExpandedChange(false)
                    }) {
                        Text(buildAnnotatedString {
                            append(it.name)
                            append(' ')

                            withStyle(
                                SpanStyle(color = Color.Gray, fontSize = MaterialTheme.typography.caption.fontSize)
                            ) {
                                append("[${it.type.name}; ${it.balance_currency}]")
                            }
                        }, style = style)
                    }
                }
            }
        }
    }
}