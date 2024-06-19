package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    val appDatabase: AppDatabase,
) : ViewModel(),
    IViewModelRefreshable by DefaultRefreshableViewModel(),
    IViewModelPager by DefaultViewModelPager(pageCount = 2, showPageIndicator = false),
    ILazyPagingItems<DataClass> by DefaultLazyPagingItems(),
    ILazyListState by DefaultLazyListState()
{
    var mediator: DataRemoteMediator? = null
    lateinit var pager: Pager<Int, DataClassEntity>

    init {
        refresh()
    }

    override fun refresh() {
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
}