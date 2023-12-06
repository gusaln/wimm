/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.gustavolopezxyz.common.ui.theme.AppDimensions

@Composable
fun MenuLayout(menu: @Composable() (() -> Unit)? = null, content: @Composable (() -> Unit)) {
    ContainerLayout {
        Box(modifier = Modifier.weight(2f)) {
            menu?.invoke()
        }

        Box(modifier = Modifier.weight(4f)) {
            content()
        }
    }
}

@Composable
fun ContainerLayout(
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable (RowScope.() -> Unit)
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(AppDimensions.Default.padding.extraLarge),
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.large, alignment)
    ) {
        content()
    }
}