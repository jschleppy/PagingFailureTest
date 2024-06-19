package com.example.myapplication

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.LoadType.*
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class DataRemoteMediator @Inject constructor(
    val appDatabase: AppDatabase,
) : RemoteMediator<Int, DataClassEntity>() {
    val dao = appDatabase.dataClassDao()
    val keyValue = "UniqueKeyValue"

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DataClassEntity>
    ): MediatorResult {
        val page = when(loadType) {
            REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: DEFAULT_PAGE_INDEX
            }
            PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        val start = page * state.config.pageSize
        val limit = state.config.pageSize

        Log.d("DataRemoteMediator", "load() start=$start, limit=$limit")

        try {
            val data = List(limit) { index ->
                val trueIndex = start + index
                DataClass(
                    id = trueIndex.toLong(),
                    title = "Title: $trueIndex"
                )
            }
            val entityList = data.map { data ->
                DataClassEntity.from(data).also { it.key = keyValue }
            }

            val endOfPaginationReached = false
            appDatabase.withTransaction {
                val prevKey = if(page == DEFAULT_PAGE_INDEX ) null else page - 1
                val nextKey = if(endOfPaginationReached) null else page + 1

                val keys = entityList.map { item ->
                    RemoteKeys(
                        dataId = item.externalId,
                        dataKey = keyValue,
                        prevKey = prevKey,
                        nextKey = nextKey,
                    )
                }
                dao.insertRemoteKeys(keys)
                dao.insertList(entityList)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        }catch (t: Throwable) {
            Log.e("DataRemoteMediator", "Exception occurred", t)
            return MediatorResult.Error(t)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, DataClassEntity>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.externalId?.let { id ->
                dao.remoteKeysById(id, keyValue)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, DataClassEntity>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { entity  ->
                dao.remoteKeysById(entity.externalId, keyValue)
            }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, DataClassEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { entity ->
                dao.remoteKeysById(entity.externalId, keyValue)
            }
    }

    companion object {
        const val DEFAULT_PAGE_INDEX = 0
    }
}