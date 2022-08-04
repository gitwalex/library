package com.gerwalex.lib.main

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class ActivityResultUtil<Input, Result>(
    caller: ActivityResultCaller,
    contract: ActivityResultContract<Input, Result>,
    private val onActivityResult: OnActivityResult<Result>,
) {

    private val launcher: ActivityResultLauncher<Input>
    private fun callOnActivityResult(result: Result) {
        onActivityResult.onActivityResult(result)
    }

    /**
     * Launch activity, same as [ActivityResultLauncher.launch] except that it allows a callback
     * executed after receiving a result from the target activity.
     */
    fun launch(input: Input) {
        launcher.launch(input)
    }

    init {
        launcher = caller.registerForActivityResult(contract) { result: Result -> callOnActivityResult(result) }
    }
}

/**
 * Specialised method for launching new activities.
 */
fun Fragment.registerActivityForResult(onActivityResult: OnActivityResult<ActivityResult>):
        ActivityResultUtil<Intent, ActivityResult> {
    return registerForActivityResult(StartActivityForResult(), onActivityResult)
}

/**
 * Specialised method for launching new activities.
 */
fun AppCompatActivity.registerActivityForResult(onActivityResult: OnActivityResult<ActivityResult>):
        ActivityResultUtil<Intent, ActivityResult> {
    return registerForActivityResult(StartActivityForResult(), onActivityResult)
}

/**
 * Register activity result using a [ActivityResultContract] and an in-place activity result callback like
 * the default approach. You can still customise callback using [.launch].
 */
fun <Input, Result> AppCompatActivity.registerForActivityResult(
    contract: ActivityResultContract<Input, Result>,
    onActivityResult: OnActivityResult<Result>,
): ActivityResultUtil<Input, Result> {
    return ActivityResultUtil(this, contract, onActivityResult)
}

/**
 * Register activity result using a [ActivityResultContract] and an in-place activity result callback like
 * the default approach. You can still customise callback using [.launch].
 */
fun <Input, Result> Fragment.registerForActivityResult(
    contract: ActivityResultContract<Input, Result>,
    onActivityResult: OnActivityResult<Result>,
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

