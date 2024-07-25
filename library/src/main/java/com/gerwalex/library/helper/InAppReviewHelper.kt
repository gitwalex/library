package com.gerwalex.library.helper

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory

class InAppReviewHelper(context: Context) {
    private val reviewManager = ReviewManagerFactory.create(context.applicationContext)
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