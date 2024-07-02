package com.example.noteappandroid.databases.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.noteappandroid.databases.converters.InstantConverters
import com.example.noteappandroid.databases.dao.FolderDao
import com.example.noteappandroid.databases.dao.LabelDao
import com.example.noteappandroid.databases.dao.NoteAppDao
import com.example.noteappandroid.databases.dao.NoteLabelDao
import com.example.noteappandroid.entity.Folder
import com.example.noteappandroid.entity.Label
import com.example.noteappandroid.entity.NoteEntity
import com.example.noteappandroid.entity.NoteLabel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@TypeConverters(
    InstantConverters::class,
)
@Database(
    entities = [NoteEntity::class, Folder::class, Label::class, NoteLabel::class],
    version = 1,
    exportSchema = false,
)
abstract class NoteAppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteAppDao
    abstract fun folderDao(): FolderDao
    abstract fun labelDao(): LabelDao
    abstract fun noteLabelDao(): NoteLabelDao

    companion object {
        @Volatile
        private var instance: NoteAppDatabase? = null

        fun getInstance(context: Context): NoteAppDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, NoteAppDatabase::class.java, "notes.db")
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        val applicationScope = CoroutineScope(Dispatchers.IO)
                        applicationScope.launch {
                            getInstance(context).folderDao().createFolder(
                                Folder(
                                    0,
                                    -1,
                                    "General",
                                    isShowNoteCreationDate = true
                                )
                            )
                        }
                    }
                })
                .build()
    }

}
