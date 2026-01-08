package com.gerwalex.library.helper

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory

/**
 * Helper class to manage the in-app review flow provided by the Google Play Core library.
 *
 * This class simplifies the process of requesting and launching an in-app review dialog.
 * The in-app review flow is designed to ask users for a rating and review without
 * them having to leave the app.
 *
 * The process is two-fold:
 * 1. Request a `ReviewInfo` object from the `ReviewManager`. This is an asynchronous operation.
 * 2. If the request is successful, use the obtained `ReviewInfo` object to launch the review
 *    flow. The review dialog may or may not be shown to the user, based on Google Play's internal logic and quotas.
 *
 * For more details on the in-app review API, see the official documentation:
 * https://developer.android.com/guide/playcore/in-app-review
 *
 * @param context The application context used to create the `ReviewManager`.
 */
class InAppReviewHelper(context: Context) {
    private val reviewManager = ReviewManagerFactory.create(context.applicationContext)

    /**
     * Launches the in-app review flow. This should be called after a [ReviewInfo] object
     * has been successfully obtained from [requestReview].
     *
     * The flow of the review process is managed by the Google Play Store, which determines
     * if and when to show the review dialog to the user based on its own quotas and logic.
     * The `addOnCompleteListener` is called when the flow is finished, regardless of whether
     * the user submitted a review or even saw the dialog.
     *
     * @param activity The activity that is hosting the review flow.
     * @param reviewInfo The [ReviewInfo] object obtained from the `reviewManager`.
     */
    fun startInAppReview(activity: Activity, reviewInfo: ReviewInfo) {
        val flow = reviewManager.launchReviewFlow(activity, reviewInfo)
        flow.addOnCompleteListener { _ ->
            // TODO: was passiert nach einem Review?
            // The flow has finished. The API does not indicate whether the user
            // reviewed or not, or even whether the review dialog was shown. Thus, no
            // matter the result, we continue our app flow.
        }
    }

    fun requestReview(onSuccess: (ReviewInfo) -> Unit) {
        reviewManager.requestReviewFlow().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // We got the ReviewInfo object
                onSuccess(task.result)
                Log.d("InAppReviewHelper", "Review possible: ")
            } else {
                // There was some problem, log or handle the error code.
                task.exception?.printStackTrace()
            }
        }
    }
}