package com.work.review_book

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.work.review_book.dao.HistoryDao
import com.work.review_book.dao.ReviewDao
import com.work.review_book.model.History
import com.work.review_book.model.Review

//Room 사용

@Database(entities = [History::class, Review::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun reviewDao(): ReviewDao

}


fun getAppDatabase(context: Context): AppDatabase {

    val migration12 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE 'REVIEW' ('id' INTEGER,'review' TEXT," + "PRIMARY KEY('id'))")
        }

    }

    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "BookSearchDB"
    )
        .addMigrations(migration12)
        .build()

}