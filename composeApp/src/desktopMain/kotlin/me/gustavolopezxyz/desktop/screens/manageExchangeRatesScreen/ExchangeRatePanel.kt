/*
 * Copyright (c) 2024. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.screens.manageExchangeRatesScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RequestPage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.russhwolf.settings.Settings
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import me.gustavolopezxyz.common.logging.rememberLogger
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.common.ui.theme.dropdownSelected
import me.gustavolopezxyz.common.ui.theme.dropdownUnselected
import me.gustavolopezxyz.desktop.navigation.ManageExchangeRatesComponent
import me.gustavolopezxyz.desktop.ui.common.AppOutlinedDropdown
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.kodein.di.compose.localDI
import org.kodein.di.instance

private sealed class State {
    data object NoData : State()
    data object Loading : State()
    data object Success : State()
    data object Error : State()
}

const val lastUrlSetting = "exchangeRates:lastUsedUrl"

@Composable
fun ExchangeRatePanel(
    modifier: Modifier = Modifier,
    onImportRequest: (List<ManageExchangeRatesComponent.UnsavedExchangeRate>) -> Unit,
    onClose: () -> Unit
) {
    val di = localDI()
    val httpClient by di.instance<HttpClient>()
    val settings by di.instance<Settings>()
    val logger = rememberLogger("ExchangeRatePanel")


    var state by remember { mutableStateOf<State>(State.NoData) }
    var error by remember { mutableStateOf("") }
    var rawObject by remember { mutableStateOf(JSONObject()) }
    var rawObjectKey by remember { mutableStateOf("") }
    var rawArray by remember { mutableStateOf(JSONArray()) }
    val isRawObject by derivedStateOf {
        rawArray.isEmpty
    }

    var url by remember { mutableStateOf(settings.getString(lastUrlSetting, "")) }

    val scope = rememberCoroutineScope()

    fun getData() {
        scope.launch {
            state = State.Loading

            val response = httpClient.get(url) {
                accept(ContentType.Application.Json)
            }
            logger.info("data received from $url")
//            logger.info("data was ${response.bodyAsText()}")

            if (!response.status.isSuccess()) {
                state = State.Error
                error = "Request was unsuccessful"
                return@launch
            }

            val body: String
            try {
                body = response.bodyAsText()
            } catch (e: Exception) {
                state = State.Error
                error = e.message ?: "Unknown error"
                return@launch
            }

            try {
                rawObject = JSONObject()
                rawArray = JSONArray(body)
                state = State.Success
                settings.putString(lastUrlSetting, url)
                return@launch
            } catch (e: JSONException) {
                error = e.message ?: "Invalid JSON returned"
            }

            try {
                rawArray = JSONArray()
                rawObject = JSONObject(body)
                state = State.Success
                settings.putString(lastUrlSetting, url)
                return@launch
            } catch (e: JSONException) {
                error = e.message ?: "Invalid JSON returned"
            }

            state = State.Error
        }
    }

    Column(
        modifier.padding(AppDimensions.Default.padding.large).fillMaxSize(),
    ) {
        Text("Import exchange rates from JSON", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.fillMaxWidth().height(24.dp))

        OutlinedTextField(url, onValueChange = { url = it }, modifier = Modifier.fillMaxWidth(), trailingIcon = {
            IconButton(onClick = { getData() }) {
                Icon(Icons.Default.RequestPage, "request page")
            }
        })
        Spacer(Modifier.fillMaxWidth().height(24.dp))


        when (state) {
            State.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }

            State.NoData -> {
                Text(
                    "No data",
                    style = MaterialTheme.typography.headlineSmall,
                )
            }

            State.Error -> {
                Text(
                    "Error", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.error
                )
                Text(error)
            }

            State.Success -> {
                if (!isRawObject) {
                    ExchangeRateParser(
                        rawArray, onImportRequest, { state = State.NoData }, modifier = Modifier.fillMaxSize()
                    )
                } else if (rawObjectKey.isBlank()) {
                    AppOutlinedDropdown(
                        value = rawObjectKey,
                        onSelect = { rawObjectKey = it ?: "" },
                        items = rawObject.keySet().toList(),
                        anchorLabel = "Select a key for the ",
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val isSelected = remember { it == rawObjectKey }

                        Text(
                            it, style = if (isSelected) MaterialTheme.typography.dropdownSelected
                            else MaterialTheme.typography.dropdownUnselected
                        )
                    }
                } else {
                    ExchangeRateParser(
                        rawObject.getJSONArray(rawObjectKey),
                        onImportRequest,
                        { state = State.NoData },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        if (state != State.Success) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.small, Alignment.End)
            ) {
                Button(onClick = { onClose() }) {
                    Text("Cancelar")
                }
            }
        }
    }
}