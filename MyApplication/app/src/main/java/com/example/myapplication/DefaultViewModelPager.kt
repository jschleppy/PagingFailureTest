package com.example.myapplication

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@OptIn(ExperimentalFoundationApi::class)
open class DefaultViewModelPager(
    pageIndex: Int = 0,
    pageCount: Int = 0,
    showPageIndicator: Boolean = true,
    animatePageChanges: Boolean = true,
    userScrollEnabled: Boolean = true
) : IViewModelPager {
    override lateinit var pagerState: PagerState
    override var pageIndex: Int by mutableIntStateOf(pageIndex)
    override var pageCount: Int by mutableIntStateOf(pageCount)
    override var userScrollEnabled: Boolean by mutableStateOf(userScrollEnabled)
    override var animatePageChanges: Boolean by mutableStateOf(animatePageChanges)
    override var showPageIndicator: Boolean by mutableStateOf(showPageIndicator)

    override fun pagerStateIsInitialized(): Boolean {
        return this::pagerState.isInitialized
    }
}