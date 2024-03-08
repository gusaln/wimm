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
import me.gustavolopezxyz.common.ext.datetime.currentTimeZone
import me.gustavolopezxyz.common.ext.toCurrency
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.desktop.navigation.ManageExchangeRatesComponent
import me.gustavolopezxyz.desktop.ui.common.AppDropdown
import org.json.JSONArray
import org.json.JSONObject

internal const val RAW_JSON = """
    []"""

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

@Composable
fun ExchangeRateParser(
    modifier: Modifier = Modifier,
    onImportRequest: (List<ManageExchangeRatesComponent.UnsavedExchangeRate>) -> Unit,
    onClose: () -> Unit
) {
    val rawData = remember { JSONArray(RAW_JSON) }

    val keys by derivedStateOf {
        val size = rawData.length()

        val keySet = mutableSetOf<String>()
        for (i in 0..size) {
            val obj = rawData.optJSONObject(0)
            keySet += obj.keySet()
        }

        keySet.toSet()
    }


    val mappers = remember {
        mutableStateMapOf(
            ExchangeRateAttribute.BaseCurrency to Mapper(),
            ExchangeRateAttribute.CounterCurrency to Mapper(),
            ExchangeRateAttribute.Rate to Mapper(),
            ExchangeRateAttribute.EffectiveSince to Mapper(),
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
        modifier.padding(AppDimensions.Default.padding.large).fillMaxSize(),
    ) {
        Text("Import exchange rates from JSON", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.fillMaxWidth().height(24.dp))

        Column {
            Text("Mappers", style = MaterialTheme.typography.headlineMedium)

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
        }
        Spacer(Modifier.fillMaxWidth().height(24.dp))

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
                Divider(Modifier.fillMaxWidth().height(1.dp), color = Color.Gray)
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
                        Divider(Modifier.fillMaxWidth().height(1.dp), color = Color.Gray)
                    }
                }
            }
        }

        Spacer(Modifier.fillMaxWidth().height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.small, Alignment.End)) {
            Button(onClick = { onImportRequest(getUnsavedRates()) }) {
                Text("Import")
            }

            Button(onClick = { onClose() }) {
                Text("Cancelar")
            }
        }
    }
}
