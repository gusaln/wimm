/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    AppTheme(isSystemInDarkTheme(), content)
}

@Composable
actual fun AppTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme {
        content()
    }
}