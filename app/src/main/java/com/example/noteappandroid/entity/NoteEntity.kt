package com.example.noteappandroid.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import java.io.Serializable

@Entity(
    tableName = "Notes",
    foreignKeys = [ForeignKey(
        entity = Folder::class,
        parentColumns = ["id"],
        childColumns = ["folder_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "title")
    var title: String? = null,

    @ColumnInfo(name = "folder_id")
    var folderId: Long ?= null,

    @ColumnInfo(name = "date_time")
    var dateTime: String? = null,

    @ColumnInfo(name = "note_text")
    var noteText: String? = null,

    @ColumnInfo(name = "img_path")
    var imgPath: String? = null,

    @ColumnInfo(name = "web_link")
    var storeWebLink: String? = null,

    @ColumnInfo(name = "color")
    var color: String? = null,

    @ColumnInfo(name = "reminder_date")
    val reminderDate: Instant? = null,

    ) : Serializable {
    override fun toString(): String {
        return "$title : $dateTime"
    }
}
