package net.tietema.rxjava2_ui_demo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import net.tietema.rxjava2_ui_demo.model.Todo
import net.tietema.rxjava2_ui_demo.model.TodoDao
import android.arch.persistence.room.Room
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject


interface TodoView {
    // producers
    val addClicks: Observable<Unit>
    val description: Observable<CharSequence>
    val checkedTodo: Observable<Todo>

    // consumer(s)
    val render: (ViewState) -> Unit
}

data class ViewState(
        val todos: List<Todo>,
        val description: CharSequence
)

class MainActivity : AppCompatActivity(), TodoView {

    private val db by lazy { Room.databaseBuilder(applicationContext,
            AppDatabase::class.java, "todos").build() }

    private val presenter by lazy { Presenter(db.todoDao()) }
    private val disposables = CompositeDisposable()

    private val adapter = TodoAdapter()
    private val addButton: Button by lazy { findViewById<Button>(R.id.button) }
    private val descriptionEditText: EditText by lazy { findViewById<EditText>(R.id.editText) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<RecyclerView>(R.id.recyclerView).let {
            it.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            it.adapter = adapter
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.bind(this, disposables)
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }

    override val addClicks: Observable<Unit> = Observable.defer {
        addButton.clicks()
    }
    override val description: Observable<CharSequence> = Observable.defer {
        descriptionEditText.textChanges()
    }

    override val checkedTodo: Observable<Todo> = adapter.todoChecked

    override val render: (ViewState) -> Unit = { (items, text) ->
        adapter.items = items
        adapter.notifyDataSetChanged()

        // check if the text actually changed before setting it
        // setting it will reset the text carrot position
        if (descriptionEditText.text.toString() != text.toString()) {
            descriptionEditText.setText(text)
        }
    }

}
class Presenter(private val todoDao: TodoDao) {

    private val description = BehaviorSubject.create<CharSequence>()

    fun bind(view: TodoView, disposables: CompositeDisposable) {
        disposables += view.addClicks
                .withLatestFrom(description, BiFunction<Unit, CharSequence, CharSequence> { _, description -> description})
                .observeOn(Schedulers.io())
                .subscribe {
                    todoDao.add(Todo().apply { text = it.toString() })
                    description.onNext("")
                }

        disposables += view.description
                .subscribe { description.onNext(it) }

        disposables += view.checkedTodo
                .observeOn(Schedulers.io())
                .map {
                    it.done = !it.done
                    it
                }
                .subscribe {
                    todoDao.update(it)
                }

        disposables += todoDao.all()
                .switchMap { list ->
                    description.first("").toFlowable()
                            .map { ViewState(list, it) }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view.render)
    }
}
