package com.cjf.thread.extension

import android.content.Context
import android.os.Build
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import com.cjf.thread.executor.ThreadTaskExecutor
import com.cjf.thread.inflater.AsyncLayoutInflatePlus
import com.cjf.thread.task.Task
import com.cjf.thread.task.TaskManager
import java.util.*

/**
 * <p>Title: Ok </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @date : 2020/7/21 21:48
 * @version : 1.0
 */

@RequiresApi(Build.VERSION_CODES.HONEYCOMB)
fun inflate(context: Context, @LayoutRes resid: Int, parent: ViewGroup?,
            callback: AsyncLayoutInflatePlus.OnInflateFinishedListener): AsyncLayoutInflatePlus {
    val inflate = AsyncLayoutInflatePlus(context)
    inflate.inflate(resid, parent, callback)
    return inflate
}

fun task(taskBuilder: Task.Builder<*, *, *>): TaskManager {
    val builders = ArrayList<Task.Builder<*, *, *>>()
    builders.add(taskBuilder)
    return task(builders)
}

fun task(taskList: List<Task.Builder<*, *, *>?>): TaskManager {
    return TaskManager().setTaskList(taskList)
}

fun thread(): ThreadTaskExecutor {
    return ThreadTaskExecutor.getInstance()
}

fun <T> post(io: () -> T, main: (any: T) -> Unit) {
    postIo {
        val ioAny = io()
        postOnMain {
            main(ioAny)
        }
    }
}

fun postIo(run: () -> Unit) {
    thread().postIo { run() }
}

fun postDelayed(run: () -> Unit, delayMillis: Long): Boolean {
    return thread().postDelayed({ run() }, delayMillis)
}

fun postAtTime(runnable: Runnable, uptimeMillis: Long): Boolean {
    return thread().postAtTime(runnable, uptimeMillis)
}

fun postToMain(run: () -> Unit) {
    thread().postToMain { run() }
}

fun postOnMain(run: () -> Unit) {
    thread().postOnMain { run() }
}

fun isMainThread(): Boolean {
    return thread().isMainThread
}