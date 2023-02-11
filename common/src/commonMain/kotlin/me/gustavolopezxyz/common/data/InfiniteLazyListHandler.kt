package me.gustavolopezxyz.common.data

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import kotlinx.coroutines.flow.distinctUntilChanged


/**
 * Extends rememberLazyListState to make any lazy column (or lazy row) infinite.
 * Will notify the [onLoadMore] callback once needed
 * @param initialFirstVisibleItemIndex see [rememberLazyListState]
 * @param initialFirstVisibleItemScrollOffset see [rememberLazyListState]
 * @param buffer the number of items before the end of the list to call the onLoadMore callback.
 *              Note that if you have a hidden item at the button, like a loader, it still counts towards the total number of items in the list.
 *              This means that you should at set a buffer of at least the number of conditionally rendered items at the bottom
 * @param onLoadMore will notify when we need to load more
 */
@Composable
fun rememberLazyListStateWithLoadMoreHandler(
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0,
    buffer: Int = 2,
    onLoadMore: (InfiniteLazyListState) -> Unit
): LazyListState {

    val listState = rememberLazyListState(initialFirstVisibleItemIndex, initialFirstVisibleItemScrollOffset)

    InfiniteLazyListHandler(listState, buffer, onLoadMore)

    return listState
}

data class InfiniteLazyListState(val lastVisibleItemIndex: Int, val buffer: Int)

/**
 * Handles the loading of items in a LazyList
 *
 * Will notify the [onLoadMore] callback once needed
 * @param listState see [LazyListState]
 * @param buffer the number of items before the end of the list to call the onLoadMore callback.
 *              Note that if you have a hidden item at the button, like a loader, it still counts towards the total number of items in the list.
 *              This means that you should at set a buffer of at least the number of conditionally rendered items at the bottom
 * @param onLoadMore will notify when we need to load more
 */
@Composable
internal fun InfiniteLazyListHandler(
    listState: LazyListState,
    buffer: Int = 0,
    onLoadMore: (InfiniteLazyListState) -> Unit
) {
    val onLoadMoreState by rememberUpdatedState(onLoadMore)

    val loadMore = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            lastVisibleItemIndex > (totalItemsNumber - buffer)
        }
    }

    LaunchedEffect(loadMore) {
        snapshotFlow { loadMore.value }
            .distinctUntilChanged()
            .collect {
                if (it) onLoadMoreState(
                    InfiniteLazyListState(
                        listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0, buffer
                    )
                )
            }
    }
}