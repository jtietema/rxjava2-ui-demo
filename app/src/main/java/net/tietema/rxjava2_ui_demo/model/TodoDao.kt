package net.tietema.rxjava2_ui_demo.model

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import io.reactivex.Flowable

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo")
    fun all(): Flowable<List<Todo>>

    @Insert
    fun add(todo: Todo)

    @Update
    fun update(todo: Todo)
}