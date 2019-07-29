package com.dhwaniris.dynamicForm.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity

/**
 * Resolves requirement for activity
 */
object ActivityResolver {
    /**
     * @param contextToResolver local Context class from view or activity
     * Iterate to find Activity may return null in case of no activity found
     * (null in case AppContext or Service Context)
     */
    fun getActivity(contextToResolver: Context): Activity? {
        var context = contextToResolver
        while (context is ContextWrapper) {
            if (context is AppCompatActivity || context is Activity) {
                return context as Activity
            }
            context = context.baseContext
        }
        return null
    }
}