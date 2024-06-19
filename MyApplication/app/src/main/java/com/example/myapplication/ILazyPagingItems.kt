package com.example.myapplication

import androidx.paging.compose.LazyPagingItems

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
            needsRecollected = true
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
        lazyPagingItems.refresh()
    }
}