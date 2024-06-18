package com.example.myapplication

import androidx.compose.foundation.lazy.LazyListState

class DefaultLazyListState : ILazyListState {
    override lateinit var lazyListState: LazyListState
    override fun isLazyListStateInitialized(): Boolean {
        return this::lazyListState.isInitialized
    }
}