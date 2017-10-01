package com.vulpeszerda.mvvmredux

import android.content.Context
import android.content.Intent
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

/**
 * Created by vulpes on 2017. 9. 5..
 */
object RouterFactory {

    @Suppress("UNCHECKED_CAST")
    fun <T: Delegate> create(activity: AppCompatActivity, klass: Class<T>): T =
            klass.constructors.first().newInstance(ActivityDelegate(
                    activity)) as T

    @Suppress("UNCHECKED_CAST")
    fun <T: Delegate> create(fragment: Fragment, klass: Class<T>): T =
            klass.constructors.first().newInstance(FragmentDelegate(
                    fragment)) as T

    interface Delegate {
        val context: Context
        fun startActivity(intent: Intent, requestCode: Int?)
        fun setResult(resultCode: Int, data: Intent?)
        fun finish()
        fun finishAffinity()
    }

    class ActivityDelegate(private val activity: AppCompatActivity) : Delegate {
        override val context: Context
            get() = activity

        override fun startActivity(intent: Intent, requestCode: Int?) {
            if (requestCode != null) {
                activity.startActivityForResult(intent, requestCode)
            } else {
                activity.startActivity(intent)
            }
        }

        override fun setResult(resultCode: Int, data: Intent?) {
            activity.setResult(resultCode, data)
        }

        override fun finish() {
            activity.finish()
        }

        override fun finishAffinity() {
            ActivityCompat.finishAffinity(activity)
        }
    }

    class FragmentDelegate(private val fragment: Fragment) : Delegate {
        override val context: Context
            get() = fragment.context

        override fun startActivity(intent: Intent, requestCode: Int?) {
            if (requestCode != null) {
                fragment.startActivityForResult(intent, requestCode)
            } else {
                fragment.startActivity(intent)
            }
        }

        override fun setResult(resultCode: Int, data: Intent?) {
            fragment.activity?.setResult(resultCode, data)
        }

        override fun finish() {
            fragment.activity?.finish()
        }

        override fun finishAffinity() {
            fragment.activity?.run { ActivityCompat.finishAffinity(this) }
        }
    }

}