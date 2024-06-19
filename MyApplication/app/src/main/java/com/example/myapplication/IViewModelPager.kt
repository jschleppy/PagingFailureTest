package com.example.myapplication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalFoundationApi::class)
interface IViewModelPager {
    var pageIndex: Int
    var userScrollEnabled: Boolean
    var pageCount: Int
    var animatePageChanges: Boolean
    var showPageIndicator: Boolean
    var pagerState: PagerState

    fun pagerStateIsInitialized(): Boolean

    @Composable
    fun ScrollToPageLaunchedEffect(
        pageOffsetFraction: Float,
        animationSpec: AnimationSpec<Float>,
    ) {
        LaunchedEffect(pageIndex) {
            scrollToPage(
                page = pageIndex,
                pageOffsetFraction = pageOffsetFraction,
                animationSpec = animationSpec
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    suspend fun scrollToPage(
        page: Int,
        pageOffsetFraction: Float = 0f,
        animationSpec: AnimationSpec<Float> = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
    ) {
        if (animatePageChanges) {
            pagerState.animateScrollToPage(page, pageOffsetFraction, animationSpec)
        } else {
            pagerState.scrollToPage(page, pageOffsetFraction)
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun DefaultPagerStateInit(
        onPageChanged: (page: Int) -> Unit, /*= { },*/
    ) {
        pagerState = rememberPagerState(
            initialPage = pageIndex
        ) {
            pageCount
        }
        ScrollToPageLaunchedEffect(
            pageOffsetFraction = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMediumLow,
            )
        )
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                onPageChanged(page)
            }
        }
    }

    @Composable
    fun DefaultHorizontalPager(
        modifier: Modifier, /*= Modifier,*/
        verticalAlignment: Alignment.Vertical, /*= Alignment.CenterVertically,*/
        displayIndicatorsBelowVsOnTop: Boolean, /*= true,*/
        pageContent: @Composable PagerScope.(page: Int) -> Unit
    ) {
        AnimatedVisibility(pageCount > 0) {
            Column(modifier = modifier) {
                HorizontalPager(
                    modifier = Modifier.fillMaxWidth(),
                    state = pagerState,
                    userScrollEnabled = userScrollEnabled,
                    verticalAlignment = verticalAlignment,
                    pageContent = pageContent,
                )
            }
        }
    }
}