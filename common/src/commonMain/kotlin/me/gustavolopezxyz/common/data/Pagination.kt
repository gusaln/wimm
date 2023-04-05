/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.data

import androidx.compose.runtime.*

/**
 * Returns a [LazyPaginationState] to manage a lazily paginated list.
 */
@Composable
fun <T> rememberLazyPaginationState(): LazyPaginationState<T> {
    return remember { LazyPaginationState() }
}

/**
 * Returns aa [LazyPaginationState] to manage a lazily paginated list.
 * Will notify the [onPageChange] callback when the page changes
 *
 * @param onPageChange the callback that modifies the item list and should load the new chunk of items.
 *
 * Usage:
 * ```
 * const val pageSize = 15
 * val pagination = rememberInfiniteListState() {
 *      items = items + transactionsListViewModel.getTransactions(pagesLoaded, pageSize)
 * }
 * ```
 */
@Composable
fun <T> rememberLazyPaginationState(
    onPageChange: (suspend LazyPaginationState<T>.() -> Unit)
): LazyPaginationState<T> {
    val pagination = remember { LazyPaginationState<T>() }
    val onChangeState by rememberUpdatedState(onPageChange)

    LaunchedEffect(pagination.pagesLoaded) {
        pagination.isLoading = true

        pagination.onChangeState()

        pagination.isLoading = false
    }

    return pagination
}


class LazyPaginationState<T>(pagesLoadedByDefault: Int = 1) {
    var pagesLoaded by mutableStateOf(pagesLoadedByDefault)
        private set

    /** The complete list of items. */
    var items by mutableStateOf(emptyList<T>())

    /** The loading state. */
    var isLoading by mutableStateOf(false)

    fun itemsLoadedCount(pageSize: Int) = pagesLoaded * pageSize

    /**
     * Updates [pagesLoaded] if needed.
     */
    fun loadUpToPage(page: Int) {
        if (pagesLoaded < page) {
            pagesLoaded = page
        }
    }

    @Composable
    fun rememberPages(pageSize: Int): State<List<List<T>>> {
        return derivedStateOf { items.chunked(pageSize) }
    }
}