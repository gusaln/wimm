/*
 * Copyright (c) 2024. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.screens.manageExchangeRatesScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import me.gustavolopezxyz.common.ext.camelToSnakeCase
import me.gustavolopezxyz.common.ext.datetime.currentTimeZone
import me.gustavolopezxyz.common.ext.toCurrency
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.desktop.navigation.ManageExchangeRatesComponent
import me.gustavolopezxyz.desktop.ui.common.AppDropdown
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

enum class ExchangeRateAttribute {
    BaseCurrency,
    CounterCurrency,
    Rate,
    EffectiveSince;

    override fun toString(): String {
        return when (this) {
            BaseCurrency -> "baseCurrency"
            CounterCurrency -> "counterCurrency"
            Rate -> "rate"
            EffectiveSince -> "effectiveSince"
        }
    }
}

data class Mapper(
    val value: String? = null,
    val isConstant: Boolean = true,
) {
    fun map(raw: JSONObject): String {
        if (isConstant) return value.orEmpty()

        return raw.optString(value).orEmpty()
    }
}

fun tryToGetMap(keys: Set<String>, attribute: ExchangeRateAttribute): Mapper {
    if (keys.contains(attribute.name.lowercase(Locale.getDefault()))) return Mapper(
        attribute.name.lowercase(Locale.getDefault()),
        false
    )

    if (keys.contains(attribute.name.camelToSnakeCase())) return Mapper(attribute.name.camelToSnakeCase(), false)

    if (attribute == ExchangeRateAttribute.Rate && keys.contains("value")) return Mapper("value", false)

    return Mapper()
}

@Composable
fun ExchangeRateParser(
    rawData: JSONArray,
    onImportRequest: (List<ManageExchangeRatesComponent.UnsavedExchangeRate>) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val keys by derivedStateOf {
        val size = rawData.length()

        val keySet = mutableSetOf<String>()
        for (i in 0..size) {
            rawData.optJSONObject(0).apply {
                if (this != null) keySet += this.keySet()
            }
        }

        keySet.toSet()
    }

    val mappers = remember {
        mutableStateMapOf(
            ExchangeRateAttribute.BaseCurrency to tryToGetMap(keys, ExchangeRateAttribute.BaseCurrency),
            ExchangeRateAttribute.CounterCurrency to tryToGetMap(keys, ExchangeRateAttribute.CounterCurrency),
            ExchangeRateAttribute.Rate to tryToGetMap(keys, ExchangeRateAttribute.Rate),
            ExchangeRateAttribute.EffectiveSince to tryToGetMap(keys, ExchangeRateAttribute.EffectiveSince),
        )
    }

    fun getUnsavedRates(): List<ManageExchangeRatesComponent.UnsavedExchangeRate> {
        val list = (0 until rawData.length()).map {
            val datum = rawData.getJSONObject(it)

            ManageExchangeRatesComponent.UnsavedExchangeRate(
                mappers.getValue(ExchangeRateAttribute.BaseCurrency).map(datum).toCurrency(),
                mappers.getValue(ExchangeRateAttribute.CounterCurrency).map(datum).toCurrency(),
                mappers.getValue(ExchangeRateAttribute.EffectiveSince).map(datum).toInstant()
                    .toLocalDateTime(currentTimeZone()),
                mappers.getValue(ExchangeRateAttribute.Rate).map(datum).toDouble(),
            )
        }

        return list
    }

    Column(
        modifier.padding(AppDimensions.Default.padding.large),
    ) {
        Column {
            Text("Select the fields", style = MaterialTheme.typography.headlineSmall)
            mappers.forEach { (attribute, mapper) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(attribute.toString(), modifier = Modifier.weight(1f))

                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            mapper.isConstant,
                            { mappers[attribute] = mapper.copy(isConstant = it) },
                        )

                        Text(if (mapper.isConstant) "Constant value" else "Use attribute")
                    }

                    if (mapper.isConstant) {
                        TextField(
                            value = mapper.value.toString(),
                            onValueChange = {
                                mappers[attribute] = mapper.copy(value = it)
                            },
                            modifier = Modifier.weight(1f),
                            label = { Text("Set the raw value") },
                        )
                    } else {
                        AppDropdown(
                            mapper.value,
                            onSelect = {
                                mappers[attribute] = mapper.copy(value = it)
                            },
                            items = keys.toList(),
                            modifier = Modifier.weight(1f),
                            anchorLabel = "Choose an attribute",
                        ) {
                            Text("'$it'")
                        }
                    }
                }
            }

            Spacer(Modifier.fillMaxWidth().height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.small, Alignment.End)
            ) {
                Button(onClick = { onImportRequest(getUnsavedRates()) }) {
                    Text("Import")
                }

                Button(onClick = { onClear() }) {
                    Text("Cancelar")
                }
            }
        }
        Spacer(Modifier.fillMaxWidth().height(24.dp))

        RawEntriesTable(keys, rawData, Modifier.fillMaxSize())
    }
}

@Composable
private fun RawEntriesTable(keys: Set<String>, rawData: JSONArray, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text("Raw data", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.fillMaxWidth().height(AppDimensions.Default.spacing.medium))

        Surface(
            tonalElevation = 24.dp,
        ) {
            Column {
                Row(
                    Modifier.padding(8.dp, 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("", modifier = Modifier.requiredWidth(24.dp))

                    keys.forEach {
                        Text(it, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                    }
                }
                HorizontalDivider(Modifier.fillMaxWidth().height(1.dp), color = Color.Gray)
                LazyColumn(
                    Modifier.wrapContentHeight().fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (rawData.isEmpty) {
                        item {
                            Text("No content")
                        }
                    }

                    items(rawData.length()) {
                        val datum = remember { rawData.getJSONObject(it) }
                        Row(
                            Modifier.padding(8.dp, 0.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                "$it",
                                modifier = Modifier.requiredWidth(24.dp),
                                style = MaterialTheme.typography.bodySmall
                            )

                            keys.forEach {
                                Text(
                                    datum.getString(it),
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        HorizontalDivider(Modifier.fillMaxWidth().height(1.dp), color = Color.Gray)
                    }
                }
            }
        }
    }
}
