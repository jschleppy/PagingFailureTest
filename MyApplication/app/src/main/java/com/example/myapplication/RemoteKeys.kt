package com.example.myapplication

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys",
    indices = [
        Index(value = ["dataId"],),
        Index(value = ["dataKey"],),
        Index(value = ["dataId", "dataKey"], unique = true),
    ]
)
class RemoteKeys constructor(
    val dataId: Long,
    val dataKey: String?,
    val prevKey: Int?,
    val nextKey: Int?,
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0L
}