/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.ext.toMoney
import me.gustavolopezxyz.common.ui.theme.dropdownSelected
import me.gustavolopezxyz.common.ui.theme.dropdownUnselected
import me.gustavolopezxyz.desktop.ui.common.AppOutlinedDropdown

@Composable
fun AccountDropdown(
    label: String,
    value: Account?,
    onSelect: (value: Account) -> Unit,
    accounts: List<Account> = emptyList(),
    modifier: Modifier = Modifier,
) {
    AppOutlinedDropdown(
        value,
        onSelect = { onSelect(it!!) },
        items = accounts,
        anchorValue = { it.name },
        anchorLabel = label,
        modifier = modifier,
    ) {
        val isSelected = remember { it.accountId == value?.accountId }

        Text(
            buildAnnotatedString {
                append(it.name)
                append(' ')

                withStyle(
                    SpanStyle(
                        color = Color.Gray, fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )
                ) {
                    append("[${it.type.name}; ${it.balance.toMoney(it.currency)}]")
                }
            }, style = if (isSelected) MaterialTheme.typography.dropdownSelected
            else MaterialTheme.typography.dropdownUnselected
        )
    }
}