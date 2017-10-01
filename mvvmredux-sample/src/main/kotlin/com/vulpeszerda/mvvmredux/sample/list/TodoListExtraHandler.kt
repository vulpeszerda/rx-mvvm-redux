package com.vulpeszerda.mvvmredux.sample.list

import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.vulpeszerda.mvvmredux.AbsExtraHandler
import com.vulpeszerda.mvvmredux.ReduxEvent

/**
 * Created by vulpes on 2017. 9. 21..
 */
class TodoListExtraHandler(private val activity: TodoListActivity,
                           errorHandler: (Throwable) -> Unit) :
        AbsExtraHandler<TodoListEvent>(activity, errorHandler) {

    override fun onExtraEvent(extra: ReduxEvent.Extra) {
        when (extra) {
            is TodoListEvent.ShowClearedToast ->
                Toast.makeText(activity, "Cleared", Toast.LENGTH_SHORT).show()
            is TodoListEvent.ShowClearConfirm ->
                AlertDialog.Builder(activity).setTitle("Confirm")
                        .setMessage("Are you sure to clear all todo?")
                        .setPositiveButton("Clear all") { _, _ ->
                            emitAction(TodoListEvent.ConfirmClearAll())
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
        }
    }
}