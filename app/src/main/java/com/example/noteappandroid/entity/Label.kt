package com.example.noteappandroid.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "labels",
    foreignKeys = [ForeignKey(
        entity = Folder::class,
        parentColumns = ["id"],
        childColumns = ["folder_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Label(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "folder_id")
    val folderId: Long? = null,

    @ColumnInfo(name = "title")
    val title: String = "",

    @ColumnInfo(name = "color")
    val color: MyColor = MyColor.Gray,
)