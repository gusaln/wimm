/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.gustavolopezxyz.common.ui.theme.AppDimensions

@Composable
fun SimplePaginationControl(
    isPrevEnabled: Boolean,
    isNextEnabled: Boolean,
    onPrevPage: () -> Unit,
    onNextPage: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            AppDimensions.Default.spacing.large,
            Alignment.CenterHorizontally
        )
    ) {
        IconButton(onClick = onPrevPage, enabled = isPrevEnabled) {
            Icon(Icons.Default.KeyboardArrowLeft, "prev page")
        }

        IconButton(onClick = onNextPage, enabled = isNextEnabled) {
            Icon(Icons.Default.KeyboardArrowRight, "next page")
        }
    }
}