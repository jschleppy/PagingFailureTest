package com.example.myapplication

import androidx.compose.runtime.snapshotFlow
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

interface ILazyPagingItems<T : Any> {
    val lazyPagingItemCount: Int
        get() {
            return if(isLazyPagingItemsInitialized()) {
                lazyPagingItems.itemCount
            }else {
                0
            }
        }

    fun hasItems() : Boolean {
        return lazyPagingItemCount > 0
    }

    // a safeguard so isLazyPagingItemsNeedRefreshed doesn't return true
    // continuously when itemCount == 0.
    var retryCount: Int
    var lazyPagingItems: LazyPagingItems<T>
    var refreshPagingItems: Boolean
    var loadAttempted: Boolean
    var isLoadingRefresh: Boolean
    var isLoadingAppend: Boolean
    var isLoadingPrepend: Boolean
    var isLoadingError: Boolean
    var isLoadingErrorRefresh: Boolean
    var isLoadingErrorPrepend: Boolean
    var isLoadingErrorAppend: Boolean
    var isNotLoading: Boolean
    fun isLazyPagingItemsInitialized(): Boolean

    // TODO deprecate?
    fun doesLazyPagingNeedRecollected(): Boolean {
        // always if it was never initialized
        if (!isLazyPagingItemsInitialized()) {
            return true
        }
        // otherwise, set to any manual override
        var needsRecollected = refreshPagingItems

        // if itemCount == 0, it may have failed to load, retry
        if (lazyPagingItems.itemCount == 0) {
//            if(retryCount++ < MAX_RETRY_COUNT) {
            needsRecollected = true
//            }
        }else {
            retryCount = 0
        }

        // reset this, have it only return true once for this value
        refreshPagingItems = false

        return needsRecollected
    }

    fun refreshLazyPagingItems() {
        loadAttempted = false

        // nothing needed to do, isLazyPagingItemsNeedRefreshed will return true
        if (!isLazyPagingItemsInitialized()) {
            return
        }

        // this gets isLazyPagingItemsNeedRefreshed to return true, so
        // view can know to call collectAsLazyPagingItems again
        refreshPagingItems = true
        // this
        lazyPagingItems.refresh()
    }

    fun applyDefaultLoadStateListener() {
        if (isLazyPagingItemsInitialized()) {
            defaultLoadStateListener()
        }
    }

    fun defaultLoadStateListener() : LazyPagingItems<T> {
        lazyPagingItems.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    loadAttempted = true
                    isLoadingRefresh = true
                    isLoadingErrorRefresh = false
                    isNotLoading = false
                }
                loadState.refresh is LoadState.NotLoading -> {
                    isLoadingRefresh = false
                    isNotLoading = !isLoadingPrepend && !isLoadingAppend && !isLoadingRefresh
                }
                loadState.append is LoadState.Loading -> {
                    isLoadingAppend = true
                    isLoadingErrorAppend = false
                    isNotLoading = false
                }
                loadState.append is LoadState.NotLoading -> {
                    isLoadingAppend = false
                    isNotLoading = !isLoadingPrepend && !isLoadingAppend && !isLoadingRefresh
                }
                loadState.prepend is LoadState.Loading -> {
                    isLoadingPrepend = true
                    isLoadingErrorPrepend = false
                    isNotLoading = false
                }
                loadState.prepend is LoadState.NotLoading -> {
                    isLoadingPrepend = false
                    isNotLoading = !isLoadingPrepend && !isLoadingAppend && !isLoadingRefresh
                }
                loadState.refresh is LoadState.Error -> {
                    isLoadingRefresh = false
                    isLoadingErrorRefresh = true
                    isLoadingError = isLoadingErrorPrepend || isLoadingErrorRefresh || isLoadingErrorAppend
                    isNotLoading = true

                    retry()
                }
                loadState.append is LoadState.Error -> {
                    isLoadingAppend = false
                    isLoadingErrorAppend = true
                    isLoadingError = isLoadingErrorPrepend || isLoadingErrorRefresh || isLoadingErrorAppend
                    isNotLoading = true

                    retry()
                }
                loadState.prepend is LoadState.Error -> {
                    isLoadingPrepend = false
                    isLoadingErrorPrepend = true
                    isLoadingError = isLoadingErrorPrepend || isLoadingErrorRefresh || isLoadingErrorAppend
                    isNotLoading = true
                }
            }
        }
        return lazyPagingItems
    }

    fun retryOnlyLoadStateListener() : LazyPagingItems<T> {
        lazyPagingItems.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                }
                loadState.refresh is LoadState.NotLoading -> {
                }
                loadState.append is LoadState.Loading -> {
                }
                loadState.append is LoadState.NotLoading -> {
                }
                loadState.prepend is LoadState.Loading -> {
                }
                loadState.prepend is LoadState.NotLoading -> {
                }
                loadState.refresh is LoadState.Error -> {
                    retry()
                }
                loadState.append is LoadState.Error -> {
                    retry()
                }
                loadState.prepend is LoadState.Error -> {
                    retry()
                }
            }
        }
        return lazyPagingItems
    }

    fun onLazyLoadingRefreshChanged(scope: CoroutineScope, onChanged: (Boolean) -> Unit) {
        snapshotFlow { isLoadingRefresh }
            .onEach {
                onChanged(it)
            }.launchIn(scope)
    }

    fun onLazyLoadingAppendChanged(scope: CoroutineScope, onChanged: (Boolean) -> Unit) {
        snapshotFlow { isLoadingAppend }
            .onEach {
                onChanged(it)
            }.launchIn(scope)
    }

    fun onLazyLoadingPrependChanged(scope: CoroutineScope, onChanged: (Boolean) -> Unit) {
        snapshotFlow { isLoadingPrepend }
            .onEach {
                onChanged(it)
            }.launchIn(scope)
    }

    companion object {
        const val MAX_RETRY_COUNT = 5
    }
}