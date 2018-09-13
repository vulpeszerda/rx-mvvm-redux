package com.vulpeszerda.mvvmredux.sample.detail

import com.vulpeszerda.mvvmredux.ContextService
import com.vulpeszerda.mvvmredux.StateConsumer
import com.vulpeszerda.mvvmredux.sample.BaseStateView
import kotlinx.android.synthetic.main.todo_detail.message as viewMessage
import kotlinx.android.synthetic.main.todo_detail.title as viewTitle

class TodoDetailStateView(
    contextService: ContextService
) : BaseStateView<TodoDetailState>("TodoDetailStateView", contextService) {

    init {
        addConsumer(
            StateConsumer.createFromAction(
                hasChange = { prev, curr -> prev.subState.todo != curr.subState.todo },
                apply = { _, curr ->
                    viewTitle.text = curr.subState.todo?.title
                    viewMessage.text = curr.subState.todo?.message
                })
        )
        addConsumer(
            StateConsumer.createFromAction(
                hasChange = { prev, curr -> prev.subState.loading != curr.subState.loading },
                apply = { _, curr ->
                    if (curr.subState.loading) {
                        showProgressDialog("Loading..")
                    } else {
                        hideProgressDialog()
                    }
                })
        )
    }
}