/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDateTime
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.Currency
import me.gustavolopezxyz.common.ext.currentTz


fun Int.minMax(min: Int, max: Int) = when {
    this < min -> min
    this > max -> max
    else -> this
}

@Composable
fun ScreenTitle(title: String) {
    Text(title, style = MaterialTheme.typography.h5)
}

@Composable
fun FormTitle(title: String) {
    Text(title, style = MaterialTheme.typography.h5)
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
            min = 1, max = when (month) {
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
fun OutlinedDoubleField(
    value: Double,
    onValueChange: (Double) -> Unit,
    decimals: Int = 2,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
) {
    val isNegative by remember { mutableStateOf(value < 0) }

    OutlinedTextField(
        value = "%.2f".format(value),
        onValueChange = { raw ->
            val sign = if (raw.contains('-')) "-" else ""
            val minorUnit = raw.filter { c -> c.isDigit() }.padStart(decimals + 2, '0')
            val before = minorUnit.substring(0, minorUnit.length - decimals).padEnd(1, '0')
            val after = minorUnit.substring(minorUnit.length - decimals).padEnd(decimals, '0')

            onValueChange("$sign$before.$after".toDouble())
        },
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        singleLine = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = if (isNegative) Color.Red else Color.Unspecified
        )
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

@Composable
fun MoneyText(
    amount: Double,
    currency: Currency,
    modifier: Modifier = Modifier,
    positiveColor: Color = Color.Unspecified,
    negativeColor: Color = Color.Red,
    commonStyle: TextStyle = TextStyle.Default,
    valueStyle: TextStyle = TextStyle.Default,
    currencyStyle: TextStyle = TextStyle(
        color = Color.Gray,
        fontSize = MaterialTheme.typography.caption.fontSize,
        baselineShift = BaselineShift.Subscript
    )
) {
    val amountColor = if (amount < 0) {
        negativeColor
    } else {
        positiveColor
    }

    Text(buildAnnotatedString {
        withStyle(
            valueStyle.copy(color = amountColor).toSpanStyle()
        ) { append("%.2f".format(amount)) }

        withStyle(
            currencyStyle.toSpanStyle()
        ) {
            append(
                currency.toString()
            )
        }
    }, modifier = modifier, style = commonStyle)
}

@Composable
fun EntrySummaryText(
    description: String,
    accountName: String,
    modifier: Modifier = Modifier,
    descriptionStyle: TextStyle = MaterialTheme.typography.body1,
    accountStyle: TextStyle = TextStyle(
        color = Color.Gray, fontSize = MaterialTheme.typography.caption.fontSize
    )
) {
    Text(
        buildAnnotatedString {
            withStyle(
                descriptionStyle.toSpanStyle()
            ) {
                append(description)
                append(' ')
            }

            withStyle(
                accountStyle.toSpanStyle()
            ) {
                append("($accountName)")
            }
        }, modifier = modifier, overflow = TextOverflow.Ellipsis
    )
}

@Preview
@Composable
fun OutlinedDateTextFieldPreview() {
    val date = Clock.System.now().toLocalDateTime(currentTz()).date

    OutlinedDateTextField(date, onValueChange = {})
}