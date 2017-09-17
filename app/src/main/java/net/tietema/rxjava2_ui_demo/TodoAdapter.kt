package net.tietema.rxjava2_ui_demo

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import net.tietema.rxjava2_ui_demo.model.Todo

/**
 * Created by jeroen on 17/09/17.
 */
class TodoAdapter : RecyclerView.Adapter<TodoViewHolder>() {

    var items: List<Todo> = emptyList()

    private val todoCheckedSubject: PublishSubject<Todo> = PublishSubject.create()
    val todoChecked: Observable<Todo>
        get() = todoCheckedSubject

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = items[position]

        holder.apply {
            text.text = todo.text
            checkbox.isChecked = todo.done
            checkbox.setOnCheckedChangeListener { _, _ ->
                todoCheckedSubject.onNext(todo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_list_item, parent, false)
        return TodoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

}

class TodoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val checkbox: CheckBox = view.findViewById(R.id.checkbox)
    val text: TextView = view.findViewById(R.id.todo)
}