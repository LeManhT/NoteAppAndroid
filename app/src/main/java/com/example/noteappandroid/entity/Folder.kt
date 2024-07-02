package com.example.noteappandroid.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Entity(tableName = "folders")
data class Folder @Ignore constructor(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "parent_id", defaultValue = "NULL")
    val parentId: Long? = null,

    @Deprecated(
        message = "This shouldn't be used directly. Use folder.getTitle(context) instead.",
        replaceWith = ReplaceWith("folder.getTitle(context)", "import com.noto.app.util.getTitle"),
    )
    @ColumnInfo(name = "title")
    val title: String = "",

    @ColumnInfo(name = "color")
    val color: MyColor = MyColor.Gray,

    @ColumnInfo(name = "creation_date")
    val creationDate: Instant = Clock.System.now(),

    @ColumnInfo(name = "is_archived", defaultValue = "0")
    val isArchived: Boolean = false,

    @ColumnInfo(name = "is_pinned", defaultValue = "0")
    val isPinned: Boolean = false,

    @ColumnInfo(name = "is_show_note_creation_date", defaultValue = "0")
    val isShowNoteCreationDate: Boolean = false,

    @Ignore
    @Transient
    val folders: List<Pair<Folder, Int>> = emptyList(),
) {

    // Room constructor
    constructor(
        id: Long = 0L,
        parentId: Long? = null,
        title: String = "",
        color: MyColor = MyColor.Gray,
        creationDate: Instant = Clock.System.now(),
        isArchived: Boolean = false,
        isPinned: Boolean = false,
        isShowNoteCreationDate: Boolean = false,
    ) : this(
        id,
        parentId,
        title,
        color,
        creationDate,
        isArchived,
        isPinned,
        isShowNoteCreationDate,
        emptyList(),
    )

    @Suppress("FunctionName")
    companion object {
//        const val GeneralFolderId = -1L
//        fun GeneralFolder() = Folder(id = GeneralFolderId, position = 0, color = MyColor.Black)
    }
}
