/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.screens.overviewScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.gustavolopezxyz.common.ui.common.AppCard
import me.gustavolopezxyz.common.ui.common.AppDivider
import me.gustavolopezxyz.common.ui.common.AppLazyList
import me.gustavolopezxyz.common.ui.common.ListItemSpacer

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> OverviewLazyListCard(
    modifier: Modifier,
    listState: LazyListState,
    items: List<T>,
    isLoading: Boolean,
    title: @Composable (() -> Unit),
    empty: @Composable (() -> Unit)? = null,
    itemContent: @Composable ((item: T) -> Unit)
) {
    Box(modifier) {
        AppCard(modifier = Modifier.fillMaxHeight().fillMaxWidth()) {
            AppLazyList(state = listState) {
                stickyHeader { title() }

                items(items) {
                    itemContent(it)

                    ListItemSpacer()

                    AppDivider(modifier = Modifier.fillMaxWidth())
                }

                item {
                    if (isLoading) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            CircularProgressIndicator()
                        }
                    } else if (items.isEmpty()) {
                        empty?.invoke()
                    }
                }
            }
        }
    }
}