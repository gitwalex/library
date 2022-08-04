package com.gerwalex.lib.permissions

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.fragment.app.Fragment

/**
 * Util für Permissions.
 *
 * Wie funktionieren Permissions? : https://medium.com/@sharmaprateek196/registerforactivityresult-api-ask-android-permissions-in-a-cooler-way-55acc3bb2895
 *
 * Mit freundlicher Unterstützung von
 *
 * https://hamurcuabi.medium.com/permissions-with-the-easiest-way-9c466ab1b2c1
 *
 */
object PermissionUtil {

    @JvmInline
    value class Permission(val launcher: ActivityResultLauncher<Array<String>>)

    /**
     * Mögliche Ergebnisse für launch(Single|Multiple)Permission
     */
    sealed class PermissionState {

        object Granted : PermissionState()
        object Denied : PermissionState()
        object PermanentlyDenied : PermissionState()
    }

    private fun getPermissionState(
        activity: Activity,
        result: MutableMap<String, Boolean>,
    ): PermissionState {
        val deniedList: List<String> = result
            .filter {
                it.value.not()
            }
            .map {
                it.key
            }
        var state = when (deniedList.isEmpty()) {
            true -> PermissionState.Granted
            false -> PermissionState.Denied
        }

        if (state == PermissionState.Denied) {
            val permanentlyMappedList = deniedList.map {
                shouldShowRequestPermissionRationale(activity, it)
            }

            if (permanentlyMappedList.contains(false)) {
                state = PermissionState.PermanentlyDenied
            }
        }
        return state
    }

    /**
     * Permission-Extension für Fragment. Hier Behandlung des Ergebnisses. Muss beim Start initialisiert werden.
     */
    fun Fragment.registerPermission(onPermissionResult: (PermissionState) -> Unit): Permission {
        return Permission(
            this.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                onPermissionResult(getPermissionState(requireActivity(), it as MutableMap<String, Boolean>))
            }
        )
    }

    /**
     * Permission-Extension für Activity. Hier Behandlung des Ergebnisses. Muss beim Start initialisiert werden.
     */
    fun AppCompatActivity.registerPermission(onPermissionResult: (PermissionState) -> Unit): Permission {
        return Permission(
            this.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                onPermissionResult(getPermissionState(this, it as MutableMap<String, Boolean>))
            }
        )
    }

    /**
     * Abholen einer Permission(später im Code)
     */
    fun Permission.launchSinglePermission(permission: String) {
        this.launcher.launch(arrayOf(permission))
    }

    /**
     * Abholen einer Permission(später im Code)
     */
    fun Permission.launchMultiplePermission(permissionList: Array<String>) {
        this.launcher.launch(permissionList)
    }

    class Example : Fragment() {

        /**
         * Register for launch for Permission.
         */
        private val registerForPermission =
            registerPermission { result ->
                when (result) {
                    PermissionState.Granted -> {
                        // ok, Permission granted
                        Toast
                            .makeText(requireContext(), "Permission is accepted", Toast.LENGTH_SHORT)
                            .show()
                        // do Stuff when granted
                    }
                    PermissionState.Denied -> {
                        Toast
                            .makeText(requireContext(), "Permission is declined", Toast.LENGTH_SHORT)
                            .show()
                        // do Stuff when denied
                    }
                    PermissionState.PermanentlyDenied -> {
                        // do Stuff when premaently denied
                    }
                }
            }

        /**
         * Hier launch for Permission. Permission wird als Parameter mitgegeben
         */
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            // [..] Single Permission
            registerForPermission.launchSinglePermission(android.Manifest.permission.BLUETOOTH_CONNECT)
            // [..] Multiple Permission, hier bluetooth und Settings
            registerForPermission.launchMultiplePermission(arrayOf(android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.WRITE_SETTINGS))
        }
    }
}