package me.gustavolopezxyz.common.data

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import kotlinx.coroutines.flow.distinctUntilChanged

data class InfiniteLazyListState(val lastVisibleItemIndex: Int, val buffer: Int)

/**
 * Handles the loading of items in a LazyList
 *
 * Based on [this post https://manavtamboli.medium.com/infinite-list-paged-list-in-jetpack-compose-b10fc7e74768](https://manavtamboli.medium.com/infinite-list-paged-list-in-jetpack-compose-b10fc7e74768)
 *
 * Will notify the [onLoadMore] callback once needed
 * @param buffer the number of items before the end of the list to call the onLoadMore callback.
 *              Note that if you have a hidden item at the button, like a loader, it still counts towards the total number of items in the list.
 *              This means that you should at set a buffer of at least the number of conditionally rendered items at the bottom
 * @param onLoadMore will notify when we need to load more
 */
@Composable
fun LazyListState.LaunchOnBottomReachedEffect(
    buffer: Int = 0,
    onLoadMore: (InfiniteLazyListState) -> Unit
) {
    val onLoadMoreState by rememberUpdatedState(onLoadMore)

    val loadMore = remember {
        derivedStateOf {
            val layoutInfo = layoutInfo
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
                        layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0, buffer
                    )
                )
            }
    }
}