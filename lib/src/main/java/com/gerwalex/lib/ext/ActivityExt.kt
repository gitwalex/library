package com.gerwalex.batteryguard.ext

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.gerwalex.lib.permissions.PermissionState

object ActivityExt {

    /**
     * Liefert zu der Liste der angeforderten Berechtigungen den jeweiligen Status
     *
     * @param permissionList der Berechtigungen
     *
     * @return Map<Name der Berechtigung, PermissionState>
     */
    @JvmStatic
    fun Activity.checkState(permissionList: Array<String>): Map<String, PermissionState> {
        val map = HashMap<String, PermissionState>()
        permissionList.forEach {
            map[it] = checkState(it)
        }
        return map
    }

    /**
     * Liefert zu einer Berechtigung den Status
     * @param permissionName Name der Berechtigung
     *
     * @return PermissionState (Granted, Denied, PermamentlyDenied)
     */
    @JvmStatic
    fun Activity.checkState(permissionName: String): PermissionState {
        return when (ActivityCompat.checkSelfPermission(this, permissionName) == PackageManager.PERMISSION_GRANTED) {
            true -> PermissionState.Granted
            false -> {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionName))
                    PermissionState.Denied else PermissionState.PermanentlyDenied
            }
        }
    }
}