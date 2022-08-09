package com.gerwalex.lib.main

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult

class ActivityResultUtil<Input, Result>(
    caller: ActivityResultCaller,
    contract: ActivityResultContract<Input, Result>,
    private var onActivityResult: OnActivityResult<Result>? = null,
) {

    private val launcher: ActivityResultLauncher<Input>
    private fun callOnActivityResult(result: Result) {
        onActivityResult?.onActivityResult(result)
    }

    /**
     * Launch activity, same as [ActivityResultLauncher.launch] except that it allows a callback
     * executed after receiving a result from the target activity.
     */
    fun launch(input: Input) {
        launcher.launch(input)
    }

    /**
     * Launch activity, same as [ActivityResultLauncher.launch] except that it allows a callback
     * executed after receiving a result from the target activity.
     */
    fun launch(input: Input, onActivityResult: OnActivityResult<Result>) {
        this.onActivityResult = onActivityResult
        launcher.launch(input)
    }

    init {
        launcher = caller.registerForActivityResult(contract) { result: Result -> callOnActivityResult(result) }
    }
}

/**
 * Registriert StartActivityForResult
 *
 * @param onActivityResult Resulthandler wird gerufen, wenn der launch ein Ergebnis gebracht hat. Kann leer/null sein,
 * dann wird der Resulthandler beim launch erwartet. Ist er dort auch leer/null, wird der Aufruf durhcgeführt, aber kein
 * Ergebnis geliefert.
 */
fun ActivityResultCaller.registerActivityForResult(onActivityResult: OnActivityResult<ActivityResult>? = null):
        ActivityResultUtil<Intent, ActivityResult> {
    return registerForActivityResult(StartActivityForResult(), onActivityResult)
}

/**
 * Regisriert ActivityForResult
 *
 * @param contract der ActivityResultContract.
 *
 * @param onActivityResult Resulthandler wird gerufen, wenn der launch ein Ergebnis gebracht hat. Kann leer/null sein,
 * dann wird der Resulthandler beim launch erwartet. Ist er dort auch leer/null, wird der Aufruf durhcgeführt, aber kein
 * Ergebnis geliefert.
 */
fun <Input, Result> ActivityResultCaller.registerForActivityResult(
    contract: ActivityResultContract<Input, Result>,
    onActivityResult: OnActivityResult<Result>? = null,
): ActivityResultUtil<Input, Result> {
    return ActivityResultUtil(this, contract, onActivityResult)
}

/**
 * Callback interface
 */
fun interface OnActivityResult<O> {

    /**
     * Called after receiving a result from the target activity
     */
    fun onActivityResult(result: O)
}

