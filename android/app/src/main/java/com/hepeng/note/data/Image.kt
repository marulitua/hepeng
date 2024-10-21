package com.hepeng.note.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Image(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "path") val path: String,
    var isUpload: Boolean = false,
    var isParsed: Boolean = false,
)