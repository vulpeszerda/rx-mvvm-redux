package com.vulpeszerda.mvvmredux.sample.create

import com.vulpeszerda.mvvmredux.ReduxViewModel
import com.vulpeszerda.mvvmredux.sample.GlobalState
import com.vulpeszerda.mvvmredux.ReduxEvent
import com.vulpeszerda.mvvmredux.sample.database.TodoDatabase
import com.vulpeszerda.mvvmredux.sample.model.Todo
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * Created by vulpes on 2017. 8. 31..
 */
class TodoCreateViewModel(private val database: TodoDatabase) :
        ReduxViewModel<TodoCreateEvent, GlobalState<TodoCreateState>>() {

    override fun eventTransformer(event: TodoCreateEvent,
                                  getState: () -> GlobalState<TodoCreateState>):
            Observable<ReduxEvent> {
        return super.eventTransformer(event, getState)
                .filter { it is TodoCreateEvent.Save }
                .flatMap({ event ->
                    val saveEvent = event as TodoCreateEvent.Save
                    save(saveEvent.title, saveEvent.message)
                }, 1)
    }

    private fun save(title: String, message: String): Observable<ReduxEvent> {
        return Single
                .fromCallable {
                    val todo = Todo.create(title, message, false)
                    database.todoDao().insert(todo).firstOrNull() ?:
                            throw IllegalAccessException("Failed to create todo")
                }
                .subscribeOn(Schedulers.io())
                .toObservable()
                .flatMap<ReduxEvent> {
                    Observable.fromArray(
                            TodoCreateEvent.ShowFinishToast(),
                            TodoCreateEvent.NavigateFinish())
                }
                .onErrorReturn { ReduxEvent.Error(it, "save") }
                .startWith(TodoCreateEvent.SetLoading(true))
                .concatWith(Observable.just(TodoCreateEvent.SetLoading(false)))
    }

    override fun reduceState(state: GlobalState<TodoCreateState>,
                             event: ReduxEvent.State): GlobalState<TodoCreateState> {
        val prevState = super.reduceState(state, event)
        var newState = prevState
        val prevSubState = prevState.subState
        var newSubState = prevSubState
        when (event) {
            is TodoCreateEvent.SetLoading ->
                newSubState = newSubState.copy(loading = event.loading)
        }
        if (newSubState !== prevSubState) {
            newState = newState.copy(subState = newSubState)
        }
        return newState
    }
}