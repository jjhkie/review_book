package com.work.review_book

import androidx.room.Database
import androidx.room.RoomDatabase
import com.work.review_book.dao.HistoryDao
import com.work.review_book.model.History

//Room 사용

@Database(entities = [History::class],version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}