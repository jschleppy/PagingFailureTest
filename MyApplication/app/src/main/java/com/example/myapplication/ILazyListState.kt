package com.example.myapplication

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable

interface ILazyListState {
    var lazyListState: LazyListState
    fun isLazyListStateInitialized(): Boolean

    @Composable
    fun InitLazyListState() {
        if(!isLazyListStateInitialized()) {
            lazyListState = rememberLazyListState()
        }
    }
}