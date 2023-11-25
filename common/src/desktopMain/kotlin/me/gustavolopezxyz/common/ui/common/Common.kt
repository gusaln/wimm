/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import java.math.RoundingMode
import java.text.DecimalFormat


@Composable
fun CardTitle(
    modifier: Modifier = Modifier.heightIn(24.dp, 64.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable RowScope.() -> Unit
) {
    ProvideTextStyle(MaterialTheme.typography.titleLarge) {
        Row(modifier, horizontalArrangement, verticalAlignment) {
            content()
        }
    }
}

@Composable
fun AppCard(
    modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit
) {
    Card(modifier) {
        Box(Modifier.padding(AppDimensions.Default.cardPadding)) {
            content()
        }
    }
}

@Composable
fun AppChip(
    color: Color = MaterialTheme.colorScheme.surface,
    modifier: Modifier = Modifier,
    text: @Composable RowScope.() -> Unit
) {
    Surface(
        color = color, contentColor = contentColorFor(color), shape = MaterialTheme.shapes.small
    ) {
        Row(modifier.padding(12.dp, 0.dp)) {
            text()
        }
    }
}

@Composable
fun AppDivider(modifier: Modifier = Modifier) {
    Divider(color = MaterialTheme.colorScheme.background, thickness = 1.dp, modifier = modifier)
}

@Composable
fun AppCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked, onCheckedChange, enabled = enabled)

        Text(label)
    }
}

val PercentageFormatter = DecimalFormat("#0.0 %")
val NumberFormatter = DecimalFormat("#,##0.00").apply {
    roundingMode = RoundingMode.CEILING
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedDateTextField(
    date: LocalDate,
    onValueChange: (date: LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
) {
    val datePickerState = rememberDatePickerState(date.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds())
    var isPickerOpen by remember { mutableStateOf(false) }

    if (isPickerOpen) {
        DatePickerDialog(
            onDismissRequest = { isPickerOpen = false },
            confirmButton = {
                TextButton(onClick = {
                    val dt = when (datePickerState.selectedDateMillis) {
                        null -> date
                        else -> Instant.fromEpochMilliseconds(datePickerState.selectedDateMillis!!)
                            .toLocalDateTime(TimeZone.UTC)
                            .date
                    }

                    onValueChange(dt)
                    isPickerOpen = false
                }) {
                    Text("Confirm")
                }

            }, dismissButton = {
                TextButton(onClick = { isPickerOpen = false }) {
                    Text("Dismiss")
                }
            }) {
            DatePicker(datePickerState, showModeToggle = false)
        }
    }

    var dateString by remember { mutableStateOf(date.toString().replace("-", "/")) }
    LaunchedEffect(date) {
        dateString = date.toString().replace("-", "/")
    }

    OutlinedTextField(
        dateString,
        onValueChange = { raw ->
            val parts = raw.filter { c -> c.isDigit() || c == '/' }.split('/')

            val year = parts[0].take(4).padStart(1, '0').toInt()
            val month = parts[1].take(2).padStart(1, '0').toInt().coerceIn(1, 12)
            val dayOfMonth = parts[2].take(2).padStart(1, '0').toInt().coerceIn(
                minimumValue = 1,
                maximumValue = when (month) {
                    2 -> 27
                    4, 6, 9, 11 -> 30
                    else -> 31
                }
            )

            onValueChange(LocalDate(year, month, dayOfMonth))
        },
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        leadingIcon = {
            if (!readOnly)
                IconButton(onClick = { isPickerOpen = true }, enabled = !isPickerOpen) {
                    Icon(Icons.Default.EditCalendar, "pick date")
                }
        },
        singleLine = true,
        readOnly = readOnly || isPickerOpen
    )
}

@Composable
fun OutlinedDoubleField(
    value: Double,
    onValueChange: (Double) -> Unit,
    decimals: Int = 2,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
) {
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
        readOnly = readOnly,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal)
    )
}
