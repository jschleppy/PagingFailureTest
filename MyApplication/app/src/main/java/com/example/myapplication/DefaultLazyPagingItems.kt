package com.example.myapplication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.paging.compose.LazyPagingItems

open class DefaultLazyPagingItems<T : Any> : ILazyPagingItems<T> {
    private var _lazyPagingItems: LazyPagingItems<T>? = null
    override var lazyPagingItems: LazyPagingItems<T>
        get() = _lazyPagingItems
            ?: throw UninitializedPropertyAccessException("\"lazyPagingItems\" was queried before being initialized")
        set(value) {
            _lazyPagingItems = value
        }
    override fun isLazyPagingItemsInitialized(): Boolean {
        return _lazyPagingItems != null
    }
    override var refreshPagingItems: Boolean by mutableStateOf(false)
    override var loadAttempted: Boolean = false
    override var isLoadingAppend: Boolean by mutableStateOf(false)
    override var isLoadingPrepend: Boolean by mutableStateOf(false)
    override var isLoadingRefresh: Boolean by mutableStateOf(false)
    override var isLoadingError: Boolean by mutableStateOf(false)
    override var isNotLoading: Boolean by mutableStateOf(true)
    override var isLoadingErrorAppend: Boolean by mutableStateOf(false)
    override var isLoadingErrorPrepend: Boolean by mutableStateOf(false)
    override var isLoadingErrorRefresh: Boolean by mutableStateOf(false)
}