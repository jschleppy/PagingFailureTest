package com.example.myapplication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

open class DefaultRefreshableViewModel(
    initialRefreshState: Boolean = false,
    initialEnabledState: Boolean = true,
    initialIndicatorYOffset: Dp = 0.dp,
) : IViewModelRefreshable {
    override var isRefreshing: Boolean by mutableStateOf(initialRefreshState)
    override var isRefreshEnabled: Boolean by mutableStateOf(initialEnabledState)
    override var refreshIndicatorYOffset: Dp by mutableStateOf(initialIndicatorYOffset)
    override fun refresh() {
        TODO("Called refresh from wrong location")
    }
}