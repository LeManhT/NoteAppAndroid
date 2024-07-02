package com.example.noteappandroid.di

import android.content.Context
import androidx.room.Room
import com.example.noteappandroid.databases.db.NoteAppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context): NoteAppDatabase =
        NoteAppDatabase.getInstance(context)

    @Provides
    fun providesNotesDao(dataBase: NoteAppDatabase) = dataBase.noteDao()

    @Provides
    fun providesFoldersDao(dataBase: NoteAppDatabase) = dataBase.folderDao()

    @Provides
    fun providesLabelsDao(dataBase: NoteAppDatabase) = dataBase.labelDao()

    @Provides
    fun providesNoteLabelsDao(dataBase: NoteAppDatabase) = dataBase.noteLabelDao()
}