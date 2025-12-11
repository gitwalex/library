package com.gerwalex.library.ext

import android.content.Context
import android.util.Log
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * Prüft auf einen Job im Workmanager
 * @param workName Name of Work
 * @return WorkInfo.State state of Work or null, when not found
 */
suspend fun Context.getUniqueWorkInfoState(workName: String): WorkInfo.State? {
    val workManager = WorkManager.getInstance(this)
    return withContext(Dispatchers.IO) {
        val workInfos = workManager.getWorkInfosForUniqueWork(workName).get()
        val result = if (workInfos.size == 1) {
            // for (workInfo in workInfos) {
            val workInfo = workInfos[0]
            Log.d("WorkManager", "workInfo.state=${workInfo.state}, id=${workInfo.id}")
            when (workInfo.state) {
                WorkInfo.State.ENQUEUED -> Log.d("WorkManager", "$workName is enqueued and alive")
                WorkInfo.State.RUNNING -> Log.d("WorkManager", "$workName is running and alive")
                WorkInfo.State.SUCCEEDED -> Log.d("WorkManager", "$workName has succeded")
                WorkInfo.State.FAILED -> Log.d("WorkManager", "$workName has failed")
                WorkInfo.State.BLOCKED -> Log.d("WorkManager", "$workName is blocked and Alive")
                WorkInfo.State.CANCELLED -> Log.d("WorkManager", "$workName is cancelled")
            }
            workInfo.state
        } else {
            null
        }
        result
    }
}






