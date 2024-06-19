package com.example.myapplication

import androidx.compose.foundation.lazy.LazyListState

interface ILazyListState {
    var lazyListState: LazyListState
    fun isLazyListStateInitialized(): Boolean
}