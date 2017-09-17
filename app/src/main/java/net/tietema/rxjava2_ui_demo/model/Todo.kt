package net.tietema.rxjava2_ui_demo.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity
class Todo {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()

    var done: Boolean = false

    var text: String = ""
}