package com.github.vulpeszerda.mvvmreduxsample.create

import com.github.vulpeszerda.mvvmredux.AbsReduxViewModel
import com.github.vulpeszerda.mvvmredux.ReduxEvent
import com.github.vulpeszerda.mvvmreduxsample.GlobalEvent
import com.github.vulpeszerda.mvvmreduxsample.GlobalState
import com.github.vulpeszerda.mvvmreduxsample.database.TodoDatabase
import com.github.vulpeszerda.mvvmreduxsample.model.Todo
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class TodoCreateViewModel(private val database: TodoDatabase) :
    AbsReduxViewModel<GlobalState<TodoCreateState>>() {

    override fun eventTransformer(
        events: Observable<ReduxEvent>,
        getState: () -> GlobalState<TodoCreateState>
    ): Observable<ReduxEvent> =
        super.eventTransformer(events, getState)
            .filter { it is TodoCreateEvent.Save }
            .flatMap({ event ->
                if (event is TodoCreateEvent.Save) {
                    save(event.title, event.message)
                } else {
                    Observable.just(event)
                }
            }, 1)

    private fun save(title: String, message: String): Observable<ReduxEvent> =
        Single
            .fromCallable {
                val todo = Todo.create(title, message, false)
                database.todoDao().insert(todo).firstOrNull()
                    ?: throw IllegalAccessException("Failed to createDiffCompletable todo")
            }
            .subscribeOn(Schedulers.io())
            .toObservable()
            .flatMap<ReduxEvent> {
                Observable.fromArray(
                    TodoCreateEvent.ShowFinishToast,
                    GlobalEvent.NavigateFinish()
                )
            }
            .onErrorReturn { GlobalEvent.Error(it, "save") }
            .startWith(TodoCreateEvent.SetLoading(true))
            .concatWith(
                Observable.just(
                    TodoCreateEvent.SetLoading(
                        false
                    )
                )
            )

    override fun reduceState(
        state: GlobalState<TodoCreateState>,
        event: ReduxEvent.State
    ): GlobalState<TodoCreateState> {
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