package com.example.myapplication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import kotlin.math.absoluteValue
import kotlin.math.sign

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
        pageOffsetFraction: Float, /* = 0f */
        animationSpec: AnimationSpec<Float>,
        /* = spring(
                    dampingRatio = LocalThemeTemplate.current.viewPagerTemplate().animationDamping,
                    stiffness = LocalThemeTemplate.current.viewPagerTemplate().animationStiffness,
                )*/
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

    @Composable
    fun DefaultHorizontalPageIndicator(
        modifier: Modifier, /*= Modifier,*/
        colorUnselected: Color, /*= LocalThemeTemplate.current.pageIndicatorTemplate().unselectedColor,*/
        colorSelected: Color, /*= LocalThemeTemplate.current.pageIndicatorTemplate().selectedColor,*/
        indicatorShape: Shape, /*= LocalThemeTemplate.current.pageIndicatorTemplate().indicatorShape,*/
        indicatorSpacing: Dp, /*= LocalThemeTemplate.current.pageIndicatorTemplate().indicatorSpacing,*/
        indicatorHeight: Dp, /*= LocalThemeTemplate.current.pageIndicatorTemplate().indicatorHeight,*/
        indicatorWidth: Dp, /*= LocalThemeTemplate.current.pageIndicatorTemplate().indicatorWidth,*/
    ) {
        // below is accompanist default page indicator which animates selected indicator
        val indicatorWidthPx = LocalDensity.current.run { indicatorWidth.roundToPx() }
        val spacingPx = LocalDensity.current.run { indicatorSpacing.roundToPx() }

        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier, contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(indicatorSpacing),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val indicatorModifier = Modifier
                        .size(width = indicatorWidth, height = indicatorHeight)
                        .background(color = colorUnselected, shape = indicatorShape)

                    repeat(pageCount) {
                        Box(indicatorModifier)
                    }
                }

                Box(
                    Modifier
                        .offset {
                            val position = pagerState.currentPage
                            val offset = pagerState.currentPageOffsetFraction
                            val next = pagerState.currentPage + offset.sign.toInt()
                            val scrollPosition =
                                ((next - position) * offset.absoluteValue + position).coerceIn(
                                    0f,
                                    (pageCount - 1)
                                        .coerceAtLeast(0)
                                        .toFloat()
                                )

                            IntOffset(
                                x = ((spacingPx + indicatorWidthPx) * scrollPosition).toInt(), y = 0
                            )
                        }
                        .size(width = indicatorWidth, height = indicatorHeight)
                        .then(
                            if (pageCount > 0) Modifier.background(
                                color = colorSelected,
                                shape = indicatorShape,
                            )
                            else Modifier
                        ))
            }
        }

        // below is a more simple approach which does not animate the selected indicator
//        Row(
//            modifier, horizontalArrangement = Arrangement.Center
//        ) {
//            repeat(pageCount) { iteration ->
//                val color =
//                    if (pagerState.currentPage == iteration) colorSelected else colorUnselected
//                Box(
//                    modifier = Modifier
//                        .padding(indicatorPadding)
//                        .clip(indicatorShape)
//                        .background(color)
//                        .size(indicatorSize)
//                )
//            }
//        }
    }
}