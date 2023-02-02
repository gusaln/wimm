/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.core

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.gustavolopezxyz.common.ui.theme.AppDimensions

@Composable
fun MenuLayout(menu: @Composable() (() -> Unit)? = null, content: @Composable (() -> Unit)) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(AppDimensions.Default.padding.large),
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.large)
    ) {
        Box(modifier = Modifier.weight(2f)) {
            menu?.invoke()
        }

        Box(modifier = Modifier.weight(4f)) {
            content()
        }
    }
}