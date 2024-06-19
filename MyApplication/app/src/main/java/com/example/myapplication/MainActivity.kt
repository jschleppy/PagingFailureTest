package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.myapplication.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = rememberNavController(),
                        startDestination = "root2",
                        route = "root1"
                    ) {
                        navigation(
                            startDestination = "route1",
                            route = "root2"
                        ) {
                            composable(
                                route = "route1"
                            ) {
                                TestList(
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TestList(modifier: Modifier) {
    val viewModel: MyViewModel = hiltViewModel()
    DefaultPagerStateInit(viewModel) { page ->
        viewModel.pageIndex = page
    }

    DefaultPullRefreshBox(modifier = modifier.fillMaxSize(), viewModel = viewModel) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            item {
                DefaultHorizontalPager(
                    modifier = Modifier.height(400.dp),
                    viewModel = viewModel,
                    verticalAlignment = Alignment.Top,
                ) { index ->
                    when (index) {
                        0 -> {
                            Box(modifier = Modifier.height(400.dp)) {
                                DataRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    viewModel = viewModel,
                                )
                            }
                        }

                        1 -> {
                            Box(modifier = Modifier.height(400.dp).background(Color.Black))
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun DataRow(modifier: Modifier,
            viewModel: MyViewModel) {
    val lazyListState = rememberLazyListState()

    if (viewModel.doesLazyPagingNeedRecollected()) {
        viewModel.lazyPagingItems =
            viewModel.getData().collectAsLazyPagingItems()
    }
    LazyRow(
        modifier = modifier
            .border(width = 2.dp, color = Color.Black)
            .height(100.dp),
        state = lazyListState,
        contentPadding = PaddingValues(5.dp),
    ) {
        items(viewModel.lazyPagingItemCount) { i ->
            val item = viewModel.lazyPagingItems[i]
            item?.let {
                Greeting(
                    name = it.title,
                    modifier = Modifier.fillMaxSize().clickable {
                        viewModel.onClickItem(it)
                    }
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DefaultPullRefreshBox(
    modifier: Modifier,
    viewModel: MyViewModel,
    content: @Composable BoxScope.() -> Unit
) {
    val pullRefreshState = pullRefreshState(viewModel) {
        viewModel.refresh()
    }

    Box(
        modifier = modifier.pullRefresh(pullRefreshState, enabled = viewModel.isRefreshEnabled),
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun pullRefreshState(viewModel: MyViewModel,
                     onRefresh: () -> Unit): PullRefreshState {
    return rememberPullRefreshState(
        refreshing = viewModel.isRefreshing, onRefresh = onRefresh
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DefaultPagerStateInit(
    viewModel: MyViewModel,
    onPageChanged: (page: Int) -> Unit, /*= { },*/
) {
    viewModel.pagerState = rememberPagerState(
        initialPage = viewModel.pageIndex
    ) {
        viewModel.pageCount
    }
    ScrollToPageLaunchedEffect(
        viewModel = viewModel,
        pageOffsetFraction = 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow,
        )
    )
    LaunchedEffect(viewModel.pagerState) {
        snapshotFlow { viewModel.pagerState.currentPage }.collect { page ->
            onPageChanged(page)
        }
    }
}

@Composable
fun ScrollToPageLaunchedEffect(
    viewModel: MyViewModel,
    pageOffsetFraction: Float,
    animationSpec: AnimationSpec<Float>,
) {
    LaunchedEffect(viewModel.pageIndex) {
        viewModel.scrollToPage(
            page = viewModel.pageIndex,
            pageOffsetFraction = pageOffsetFraction,
            animationSpec = animationSpec
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DefaultHorizontalPager(
    modifier: Modifier,
    viewModel: MyViewModel,
    verticalAlignment: Alignment.Vertical,
    pageContent: @Composable PagerScope.(page: Int) -> Unit
) {
    AnimatedVisibility(viewModel.pageCount > 0) {
        Column(modifier = modifier) {
            HorizontalPager(
                modifier = Modifier.fillMaxWidth(),
                state = viewModel.pagerState,
                userScrollEnabled = viewModel.userScrollEnabled,
                verticalAlignment = verticalAlignment,
                pageContent = pageContent,
            )
        }
    }
}