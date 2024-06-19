package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
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
    viewModel.DefaultPagerStateInit { page ->
        viewModel.pageIndex = page
    }

    viewModel.DefaultPullRefreshBox(modifier = modifier.fillMaxSize(), viewModel = viewModel) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            item {
                viewModel.DefaultHorizontalPager(
                    modifier = Modifier.height(400.dp),
                    verticalAlignment = Alignment.Top,
                    displayIndicatorsBelowVsOnTop = false,
                ) { index ->
                    when (index) {
                        0 -> {
                            Box(modifier = Modifier.height(400.dp)) {
                                viewModel.lazyListState = rememberLazyListState()

                                if (viewModel.doesLazyPagingNeedRecollected()) {
                                    viewModel.lazyPagingItems =
                                        viewModel.getData().collectAsLazyPagingItems()
                                }

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
    LazyRow(
        modifier = modifier
            .border(width = 2.dp, color = Color.Black)
            .height(100.dp),
        state = viewModel.lazyListState,
        contentPadding = PaddingValues(5.dp),
    ) {
        if (viewModel.hasItems()) {
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
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}