package com.example.myapplication

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["data_id"], unique = true)
    ]
)
data class DataClassEntity constructor(
    @ColumnInfo(name = "data_id") val externalId: Long,
    @ColumnInfo(name = "data_title") val title: String,
    @ColumnInfo(name = "data_key") var key: String?,
) {

    @PrimaryKey(autoGenerate = true) var id: Long = 0L

    companion object {
        fun from(dataClass: DataClass) : DataClassEntity {
            return DataClassEntity(
                externalId = dataClass.id,
                title = dataClass.title,
                key = null,
            )
        }
    }
}
