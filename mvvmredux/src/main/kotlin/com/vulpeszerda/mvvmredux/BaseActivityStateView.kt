package com.vulpeszerda.mvvmredux

import android.app.ProgressDialog
import android.view.View

/**
 * Created by vulpes on 2017. 9. 22..
 */
abstract class BaseActivityStateView<T, E : ReduxEvent>(
        private val activity: BaseActivity,
        errorHandler: (Throwable) -> Unit) :
        BaseStateView<T, E>(activity, errorHandler) {

    override val progressDialog: ProgressDialog by lazy {
        ProgressDialog(activity)
    }

    override val isAvailable: Boolean
        get() = !activity.isFinishing

    override val containerView: View? by lazy {
        activity.findViewById(android.R.id.content)
    }
}