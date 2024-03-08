/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import kotlinx.coroutines.launch
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.desktop.navigation.ManageExchangeRatesComponent
import me.gustavolopezxyz.desktop.screens.manageExchangeRatesScreen.ExchangeRateParser
import me.gustavolopezxyz.desktop.services.SnackbarService
import me.gustavolopezxyz.desktop.ui.ExchangeRatesList
import me.gustavolopezxyz.desktop.ui.common.ContainerLayout
import org.kodein.di.compose.localDI
import org.kodein.di.instance

@Composable
fun ManageExchangeRatesScreen(component: ManageExchangeRatesComponent) {
    val scope = rememberCoroutineScope()
    val exchangeRates by component.collectExchangeRatesAsState()

    val di = localDI()
    val snackbar by di.instance<SnackbarService>()

    val page by component.page.subscribeAsState()

    var isImporting by remember { mutableStateOf(false) }
    if (isImporting) {
        Window(
            onCloseRequest = { isImporting = false },
            title = "Import from JSON",
        ) {
            Card(modifier = Modifier.fillMaxSize()) {
                ExchangeRateParser(
                    onImportRequest = {
                        component.insertMany(it)

                        scope.launch {
                            snackbar.showSnackbar("${it.size} exchange rates imported")
                        }
                    },
                    onClose = { isImporting = false })
            }
        }
    }

    ContainerLayout {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = { isImporting = true }) {
                    Icon(Icons.Default.UploadFile, "")
                    Text("Import values")
                }
            }

            Spacer(Modifier.fillMaxWidth().height(AppDimensions.Default.spacing.large))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.large)
            ) {
                ExchangeRatesList(
                    exchangeRates,
                    isFirstPage = page == 1,
                    isLastPage = exchangeRates.size < component.pageSize.value,
                    isLoading = false,
                    onPrevPage = { component.onNextPage() },
                    onNextPage = { component.onPrevPage() },
                    modifier = Modifier.weight(4f).fillMaxHeight()
                )

                Column(Modifier.weight(6f).fillMaxHeight()) {
                }
            }
        }

    }
}

