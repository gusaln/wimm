/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.gustavolopezxyz.common.ui.theme.AppDimensions

internal val ListItemHorizontalPadding = 16.dp
internal val ListItemVerticalPadding = 4.dp
internal val ListItemPrimaryAndSecondaryTextSpace = 8.dp
internal val ListItemPrimaryTextStyle @Composable get() = MaterialTheme.typography.bodyLarge
internal val ListItemSecondaryTextStyle @Composable get() = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)

@Composable
fun AppList(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(AppDimensions.Default.listSpaceBetween),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier, verticalArrangement = verticalArrangement) {
        content()
    }
}

@Composable
fun ListItemSpacer() {
    Spacer(Modifier.height(AppDimensions.Default.listSpaceBetween))
}

@Composable
fun AppLazyList(
    modifier: Modifier = Modifier,
    spaceBetween: Dp = AppDimensions.Default.listSpaceBetween,
    state: LazyListState = rememberLazyListState(),
    content: LazyListScope.() -> Unit
) {
    LazyColumn(modifier, state, verticalArrangement = Arrangement.spacedBy(spaceBetween)) {
        content()
    }
}

@Composable
fun AppListTitle(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable RowScope.() -> Unit
) {
    ProvideTextStyle(MaterialTheme.typography.titleSmall) {
        Row(modifier.padding(bottom = 8.dp), horizontalArrangement, verticalAlignment) {
            content()
        }
    }
}

@Composable
fun AppListTitle(
    title: String,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    AppListTitle(modifier, horizontalArrangement, verticalAlignment) { Text(title) }
}

@Composable
fun AppListItem(
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = ListItemHorizontalPadding,
    verticalPadding: Dp = ListItemVerticalPadding,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier.padding(horizontalPadding, verticalPadding),
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
fun AppListItem(
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = ListItemHorizontalPadding,
    verticalPadding: Dp = ListItemVerticalPadding,
    secondaryText: @Composable ColumnScope.() -> Unit = {},
    action: @Composable RowScope.() -> Unit,
    text: @Composable ColumnScope.() -> Unit,
) {
    AppListItem(modifier = modifier, horizontalPadding, verticalPadding) {
        Column(Modifier.weight(1f)) {
            ProvideTextStyle(ListItemPrimaryTextStyle) {
                text()
            }
            Spacer(Modifier.height(ListItemPrimaryAndSecondaryTextSpace))
            ProvideTextStyle(ListItemSecondaryTextStyle) {
                secondaryText()
            }
        }

        action()
    }
}