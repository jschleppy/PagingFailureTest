package com.example.myapplication

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalMaterialApi::class)
interface IViewModelRefreshable {
    var isRefreshing: Boolean
    var isRefreshEnabled: Boolean
    var refreshIndicatorYOffset: Dp
    fun refresh()

    @Composable
    fun pullRefreshState(onRefresh: () -> Unit): PullRefreshState {
        return rememberPullRefreshState(
            refreshing = isRefreshing, onRefresh = onRefresh
        )
    }

    @Composable
    fun DefaultPullRefreshBox(
        modifier: Modifier,
        viewModel: IViewModelRefreshable,
        content: @Composable BoxScope.() -> Unit
    ) {
        val pullRefreshState = pullRefreshState {
            viewModel.refresh()
        }

        Box(
            modifier = modifier.pullRefresh(pullRefreshState, enabled = isRefreshEnabled),
        ) {
            content()

            DefaultPullRefreshIndicator(
                modifier = Modifier,
                pullRefreshState = pullRefreshState,
                backgroundColor = Color.White,
                contentColor = Color.Black,
                scale = false,
            )
        }
    }

    @Composable
    fun BoxScope.DefaultPullRefreshIndicator(
        modifier: Modifier,
        pullRefreshState: PullRefreshState,
        backgroundColor: Color, /*= LocalThemeTemplate.current.pullRefreshTemplate().backgroundColor,*/
        contentColor: Color, /*= LocalThemeTemplate.current.pullRefreshTemplate().contentColor,*/
        scale: Boolean, /*= LocalThemeTemplate.current.pullRefreshTemplate().scale,*/
    ) {
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = modifier
                .align(Alignment.TopCenter)
                .offset(y = refreshIndicatorYOffset),
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            scale = scale,
        )
    }
}