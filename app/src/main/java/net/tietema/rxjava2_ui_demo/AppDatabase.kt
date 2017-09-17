package net.tietema.rxjava2_ui_demo

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import net.tietema.rxjava2_ui_demo.model.Todo
import net.tietema.rxjava2_ui_demo.model.TodoDao

/**
 * Created by jeroen on 16/09/17.
 */

@Database(entities = arrayOf(Todo::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}