package com.gerwalex.lib.permissions

/**
 * Util für Permissions.
 *
 * Mit freundlicher Unterstützung von
 *
 * https://hamurcuabi.medium.com/permissions-with-the-easiest-way-9c466ab1b2c1
 *
 */
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

/**
 * Util für Permissions.
 *
 * Mit freundlicher Unterstützung von
 *
 * https://hamurcuabi.medium.com/permissions-with-the-easiest-way-9c466ab1b2c1
 *
 */
class Permission private constructor(
    private val caller: ActivityResultCaller,
    var onPermissionRequestResult: OnPermissionResult? = null,
) {

    constructor(
        caller: Activity,
        onPermissionRequestResult: OnPermissionResult? = null,
    ) : this(caller as ActivityResultCaller, onPermissionRequestResult)

    constructor(
        caller: Fragment,
        onPermissionRequestResult: OnPermissionResult? = null,
    ) : this(caller as ActivityResultCaller, onPermissionRequestResult)

    /**
     * activity: lateinit, da beim Aufruf ein Fargment noch nicht attacched ist.
     */
    private lateinit var activity: Activity
    private val launcher: ActivityResultLauncher<Array<String>>

    init {
        launcher = caller.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            onPermissionRequestResult(it)
        }
    }

    /**
     * Wird nach Berechtigungabfrage oder nach Prüfung der launch-Voraussetzungen gerufen.
     */
    private fun onPermissionRequestResult(it: Map<String, Boolean>) {
        onPermissionRequestResult!!.onPermissionResult(getPermissionStateNew(activity,
            it as MutableMap<String, Boolean>))
    }

    /**
     * Resulthandler: Functional Callback interface
     */
    fun interface OnPermissionResult {

        /**
         * Called after receiving a result
         */
        fun onPermissionResult(result: PermissionState)
    }

    /**
     * Prüft die Vorausstzungen für Berechtigungsabfrage:
     *
     * 1. activity kann ermittelt werden.
     * 2. Es sind noch nicht alle Berechtigungen vorhanden. (untested)
     *
     * Alle Berechtigungen vorhanden: Direkter Aufruf von Permission#onPermissionRequestResult, ansonsten
     * Start der Berechtigungsabfrage.
     */
    fun launch(
        permissionList: Array<String>, onPermissionRequestResult: OnPermissionResult,
    ) {
        this.onPermissionRequestResult = onPermissionRequestResult
        activity = when (caller) {
            is Fragment -> caller.requireActivity()
            is Activity -> caller
            else -> throw IllegalArgumentException("cannot determine Acivity")
        }
        val map = HashMap<String, Boolean>()
        permissionList.forEach {
            map[it] = ActivityCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }
        val deniedList: List<String> = map
            .filter {
                it.value.not()
            }
            .map {
                it.key
            }
        if (deniedList.isEmpty()) {
            onPermissionRequestResult(map)
        } else {
            launcher.launch(permissionList)
        }
    }

    /**
     * Mögliche Ergebnisse für launch(Single|Multiple)Permission:
     *
     *Granted, Denied, PermanmentlyDenied
     */
    sealed class PermissionState {

        object Granted : PermissionState()
        object Denied : PermissionState()
        object PermanentlyDenied : PermissionState()
    }

    /**
     * Ermttlung der vergebenen Berechtigungen
     */
    private fun getPermissionStateNew(activity: Activity, result: MutableMap<String, Boolean>): PermissionState {
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
                ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
            }

            if (permanentlyMappedList.contains(false)) {
                state = PermissionState.PermanentlyDenied
            }
        }
        return state
    }
}

/**
 * Registriert PermissionRequest ohne ResultHandler. In diesem Fall muss beim launch dann ein Resulthandler
 * mitgeliefert werden.
 */
fun Fragment.registerPermission(): Permission {
    return Permission(this, null)
}

/**
 * Registriert PermissionRequest mit ResultHandler
 */
fun Fragment.registerPermission(onPermissionRequestResult: Permission.OnPermissionResult): Permission {
    return Permission(this, onPermissionRequestResult)
}

/**
 * Registriert PermissionRequest ohne ResultHandler. In diesem Fall muss beim launch dann ein Resulthandler
 * mitgeliefert werden.
 */
fun Activity.registerPermission(): Permission {
    return Permission(this, null)
}

/**
 * Registriert PermissionRequest mit ResultHandler
 */
fun Activity.registerPermission(onPermissionRequestResult: Permission.OnPermissionResult): Permission {
    return Permission(this, onPermissionRequestResult)
}

/**
 * Prüft und startet Permissionabfrage. Wurde bei der Registrierung bereits ein Resulthandler mitgegeben, wird eine
 * IllegallStateException geworfen
 */
fun Permission.launchSinglePermission(
    permission: String,
    onPermissionRequestResult: Permission.OnPermissionResult,
) {
    launchMultiplePermission(arrayOf(permission), onPermissionRequestResult)
}

/**
 * Prüft und startet Permissionabfrage. Wurde bei der Registrierung kein Resulthandler mitgegeben, wird eine
 * IllegallStateException geworfen
 */
fun Permission.launchSinglePermission(permission: String) {
    launchMultiplePermission(arrayOf(permission))
}

/**
 * Abholen einer Permission(später im Code)
 */
fun Permission.launchMultiplePermission(permissionList: Array<String>) {
    onPermissionRequestResult?.let {
        launchMultiplePermission(permissionList, it)
    } ?: throw IllegalStateException("Do not know what to do with PermissionResult")
}

/**
 * Abholen einer Permission(später im Code)
 */
fun Permission.launchMultiplePermission(
    permissionList: Array<String>,
    onPermissionRequestResult: Permission.OnPermissionResult,
) {
    this.onPermissionRequestResult?.let {
        throw IllegalStateException("Duplicate ResultHandler: Registration and launch")
    } ?: this.launch(permissionList, onPermissionRequestResult)
}

