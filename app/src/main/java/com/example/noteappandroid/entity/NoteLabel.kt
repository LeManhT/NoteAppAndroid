    package com.example.noteappandroid.entity

    import androidx.room.ColumnInfo
    import androidx.room.Entity
    import androidx.room.ForeignKey
    import androidx.room.PrimaryKey

    @Entity(
        tableName = "note_labels",
        foreignKeys = [
            ForeignKey(
                entity = NoteEntity::class,
                parentColumns = ["id"],
                childColumns = ["note_id"],
                onDelete = ForeignKey.CASCADE,
            ),
            ForeignKey(
                entity = Label::class,
                parentColumns = ["id"],
                childColumns = ["label_id"],
                onDelete = ForeignKey.CASCADE,
            ),
        ]
    )
    data class NoteLabel(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val id: Long = 0L,

        @ColumnInfo(name = "note_id")
        val noteId: Long ?= null,

        @ColumnInfo(name = "label_id")
        val labelId: Long
    )
