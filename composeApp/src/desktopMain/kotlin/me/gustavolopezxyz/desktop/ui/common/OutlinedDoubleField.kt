/*
 * Copyright (c) 2024. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.ui.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import org.scijava.parsington.eval.DefaultEvaluator

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
    var useCalculator by remember { mutableStateOf(false) }

    if (useCalculator) {
        var formula by remember { mutableStateOf("") }

        OutlinedTextField(
            value = formula,
            onValueChange = { formula = it },
            modifier = modifier.onKeyEvent {
                if (it.key == Key.Enter) {
                    when (val result = DecimalEvaluator().evaluate(formula)) {
                        is Number -> {
                            onValueChange(result.toDouble())
                        }

                        else -> {
                            onValueChange(0.0)
                        }
                    }
                    useCalculator = false
                } else if (it.key == Key.Escape) {
                    useCalculator = false
                }

                false
            },
            label = label,
            placeholder = placeholder,
            singleLine = true,
            readOnly = readOnly,
            trailingIcon = {
                IconButton(onClick = { useCalculator = false }) {
                    Icon(Icons.Default.Cancel, "cancel")
                }
            },
//            supportingText = {
//                Text("Type your formula and press Enter")
//            }
//            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        )
    } else {
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
            trailingIcon = {
                IconButton(onClick = { useCalculator = true }) {
                    Icon(Icons.Default.Calculate, "calculator")
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
    }
}

class DecimalEvaluator : DefaultEvaluator() {
    override fun add(a: Any?, b: Any?): Double? {
        if (a is Number && b is Number) return a.toDouble() + b.toDouble()

        return null
    }

    override fun sub(a: Any?, b: Any?): Double? {
        if (a is Number && b is Number) return a.toDouble() - b.toDouble()

        return null
    }

    override fun mul(a: Any?, b: Any?): Double? {
        if (a is Number && b is Number) return a.toDouble() * b.toDouble()

        return null
    }

    override fun div(a: Any?, b: Any?): Double? {
        if (a is Number && b is Number) return a.toDouble() / b.toDouble()

        return null
    }
}