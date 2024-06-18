package com.example.myapplication

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DataClassDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: DataClassEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: List<DataClassEntity>)

    @Query("SELECT * FROM DataClassEntity WHERE data_id in (SELECT dataId FROM remote_keys where dataKey = :key) and data_key = :key ORDER BY id ASC")
    fun loadPagingSource(key: String?) : PagingSource<Int, DataClassEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemoteKeys(remoteKeys: List<RemoteKeys>)

    @Query("SELECT * FROM remote_keys WHERE dataId = :id and dataKey = :key")
    suspend fun remoteKeysById(id: Long, key: String?): RemoteKeys?

    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()

    @Query("DELETE FROM DataClassEntity")
    suspend fun clearAll()
}