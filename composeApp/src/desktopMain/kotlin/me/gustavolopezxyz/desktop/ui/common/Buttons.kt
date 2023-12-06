/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.ui.common

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.gustavolopezxyz.common.ui.theme.AppDimensions

@Composable
fun AppButton(onClick: () -> Unit, text: String, colors: ButtonColors = ButtonDefaults.buttonColors()) {
    Button(onClick, colors = colors) {
        Text(text.uppercase())
    }
}

@Composable
fun AppTextButton(onClick: () -> Unit, text: String, colors: ButtonColors = ButtonDefaults.textButtonColors()) {
    TextButton(onClick, colors = colors) {
        Text(text.uppercase())
    }
}

@Composable
fun AppTextButton(
    onClick: () -> Unit,
    text: String,
    icon: @Composable () -> Unit,
    colors: ButtonColors = ButtonDefaults.textButtonColors()
) {
    TextButton(onClick, colors = colors) {
        icon()

        Spacer(modifier = Modifier.width(AppDimensions.Default.padding.large))

        Text(text.uppercase())
    }
}