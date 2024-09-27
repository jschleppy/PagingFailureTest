package com.example.myapplication

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalFoundationApi::class)
@HiltViewModel
class MyViewModel @Inject constructor(
    val appDatabase: AppDatabase,
) : ViewModel() {
    var mediator: DataRemoteMediator? = null
    lateinit var pager: Pager<Int, DataClassEntity>
    private var _lazyPagingItems: LazyPagingItems<DataClass>? = null
    var lazyPagingItems: LazyPagingItems<DataClass>
        get() = _lazyPagingItems
            ?: throw UninitializedPropertyAccessException("\"lazyPagingItems\" was queried before being initialized")
        set(value) {
            _lazyPagingItems = value
        }
    lateinit var pagerState: PagerState
    var pageIndex: Int by mutableIntStateOf(0)
    var pageCount: Int by mutableIntStateOf(2)
    var userScrollEnabled: Boolean by mutableStateOf(true)
    var animatePageChanges: Boolean by mutableStateOf(true)
    var isRefreshing: Boolean by mutableStateOf(false)
    var isRefreshEnabled: Boolean by mutableStateOf(true)
    val lazyPagingItemCount: Int
        get() {
            return if(_lazyPagingItems != null) {
                lazyPagingItems.itemCount
            }else {
                0
            }
        }

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            appDatabase.dataClassDao().clearAll()
            appDatabase.dataClassDao().clearRemoteKeys()

            refreshLazyPagingItems()
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getData(): Flow<PagingData<DataClass>> {
        if(mediator == null) {
            mediator = DataRemoteMediator(
                appDatabase = appDatabase
            )
        }
        return mediator?.let {
            if(!this::pager.isInitialized) {
                pager = Pager(
                    PagingConfig(pageSize = 40, initialLoadSize = 40),
                    remoteMediator = it
                ) {
                    val keyValue = it.keyValue
                    appDatabase.dataClassDao().loadPagingSource(keyValue)
                }
            }
            pager.flow.map { pagingData ->
                pagingData.map { entity -> DataClass(id = entity.externalId, title = entity.title) }
            }
        } ?: emptyFlow()
    }

    fun onClickItem(item: DataClass) {
        pageIndex = 1
    }

    private fun refreshLazyPagingItems() {
        if (_lazyPagingItems == null) {
            return
        }

        lazyPagingItems.refresh()
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
}