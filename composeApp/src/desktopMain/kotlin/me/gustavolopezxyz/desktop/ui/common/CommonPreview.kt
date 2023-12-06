/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.ui.common

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.toLocalDateTime
import me.gustavolopezxyz.common.ext.datetime.currentTimeZone
import me.gustavolopezxyz.common.ui.theme.AppTheme

@Preview
@Composable
fun AppChipPreview() {
    AppTheme(true) {
        Card(modifier = Modifier.fillMaxSize()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppChip {
                    Text("This is a test")
                }

                AppChip(MaterialTheme.colorScheme.primary) {
                    Text("This is a test")
                }

                AppChip(MaterialTheme.colorScheme.secondary) {
                    Text("This is a test")
                }

                AppChip(MaterialTheme.colorScheme.tertiary) {
                    Text("This is a test")
                }

                AppChip(MaterialTheme.colorScheme.surface) {
                    Text("This is a test")
                }

                AppChip(Color.Red) {
                    Text("This is a test")
                }
            }
        }
    }
}

@Preview
@Composable
fun OutlinedDateTextFieldPreview() {
    val date = Clock.System.now().toLocalDateTime(currentTimeZone()).date

    OutlinedDateTextField(date, onValueChange = {})
}