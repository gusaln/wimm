/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDateTime
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.ext.currentTz


fun Int.minMax(min: Int, max: Int) = when {
    this < min -> min
    this > max -> max
    else -> this
}


@Composable
fun OutlinedDateTextField(
    date: LocalDate,
    onValueChange: (date: LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
) {

    var dateString by remember { mutableStateOf(date.toString().replace("-", "/")) }

    fun handleChange(raw: String) {
        val parts = raw.filter { c -> c.isDigit() || c == '/' }.split('/')

        val year = parts[0].take(4).padStart(1, '0').toInt()
        val month = parts[1].take(2).padStart(1, '0').toInt().minMax(1, 12)
        val dayOfMonth = parts[2].take(2).padStart(1, '0').toInt().minMax(
            min = 1,
            max = when (month) {
                2 -> 27
                4, 6, 9, 11 -> 30
                else -> 31
            }
        )

        onValueChange(LocalDate(year, month, dayOfMonth))

        dateString = "${year.toString().padStart(4, '0')}/${month.toString().padStart(2, '0')}/${
            dayOfMonth.toString().padStart(2, '0')
        }"
    }

    OutlinedTextField(
        dateString, ::handleChange, modifier = modifier, label = label, placeholder = placeholder, singleLine = true
    )
}

@Composable
fun RegularLayout(menu: @Composable() (() -> Unit)? = null, content: @Composable (() -> Unit)) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(Constants.Size.Large.dp),
        horizontalArrangement = Arrangement.spacedBy(Constants.Size.Large.dp)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            menu?.invoke()
        }

        Box(modifier = Modifier.weight(5f)) {
            content()
        }
    }
}

@Preview
@Composable
fun OutlinedDateTextFieldPreview() {
    val date = Clock.System.now().toLocalDateTime(currentTz()).date

    OutlinedDateTextField(date, onValueChange = {})
}