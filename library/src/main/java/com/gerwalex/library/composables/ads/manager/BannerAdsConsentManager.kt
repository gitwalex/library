package com.gerwalex.library.composables.ads.manager

import android.app.Activity
import android.content.Context
import com.google.android.ump.ConsentForm.OnConsentFormDismissedListener
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentInformation.PrivacyOptionsRequirementStatus
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform


/**
 * The Google Mobile Ads SDK provides the User Messaging Platform (Google's IAB Certified consent
 * management platform) as one solution to capture consent for users in GDPR impacted countries.
 * This is an example and you can choose another consent management platform to capture consent.
 */
class BannerAdsConsentManager private constructor(context: Context) {
    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(context)

    /** Interface definition for a callback to be invoked when consent gathering is complete.  */
    interface OnConsentGatheringCompleteListener {
        fun consentGatheringComplete(error: FormError?)
    }

    /** Helper variable to determine if the app can request ads.  */
    fun canRequestAds(): Boolean {
        return consentInformation.canRequestAds()
    }

    // [START is_privacy_options_required]
    val isPrivacyOptionsRequired: Boolean
        /** Helper variable to determine if the privacy options form is required.  */
        get() = (consentInformation.getPrivacyOptionsRequirementStatus()
                == PrivacyOptionsRequirementStatus.REQUIRED)

    // [END is_privacy_options_required]
    /**
     * Helper method to call the UMP SDK methods to request consent information and load/present a
     * consent form if necessary.
     */
    fun gatherConsent(
        activity: Activity, onConsentGatheringCompleteListener: OnConsentGatheringCompleteListener
    ) {
        // For testing purposes, you can force a DebugGeography of EEA or NOT_EEA.
//        val debugSettings =
//            ConsentDebugSettings.Builder(activity) // .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
//                .addTestDeviceHashedId(MyActivity.TEST_DEVICE_HASHED_ID)
//                .build()

        val params =
            ConsentRequestParameters.Builder()//.setConsentDebugSettings(debugSettings)
                .build()

        // [START request_consent_info_update]
        // Requesting an update to consent information should be called on every app launch.
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            { // Called when consent information is successfully updated.
                // [START_EXCLUDE silent]
                loadAndShowConsentFormIfRequired(activity, onConsentGatheringCompleteListener)
            },  // [END_EXCLUDE]
            { requestConsentError: FormError? ->  // Called when there's an error updating consent information.
                // [START_EXCLUDE silent]
                onConsentGatheringCompleteListener.consentGatheringComplete(requestConsentError)
            })
        // [END_EXCLUDE]
        // [END request_consent_info_update]
    }

    private fun loadAndShowConsentFormIfRequired(
        activity: Activity, onConsentGatheringCompleteListener: OnConsentGatheringCompleteListener
    ) {
        // [START load_and_show_consent_form]
        UserMessagingPlatform.loadAndShowConsentFormIfRequired(
            activity,
            OnConsentFormDismissedListener { formError: FormError? ->
                // Consent gathering process is complete.
                // [START_EXCLUDE silent]
                onConsentGatheringCompleteListener.consentGatheringComplete(formError)
            })
        // [END load_and_show_consent_form]
    }

    /** Helper method to call the UMP SDK method to present the privacy options form.  */
    fun showPrivacyOptionsForm(
        activity: Activity, onConsentFormDismissedListener: OnConsentFormDismissedListener
    ) {
        // [START present_privacy_options_form]
        UserMessagingPlatform.showPrivacyOptionsForm(activity, onConsentFormDismissedListener)
        // [END present_privacy_options_form]
    }

    companion object {
        private var instance: BannerAdsConsentManager? = null

        /** Public constructor  */
        fun getInstance(context: Context): BannerAdsConsentManager {
            if (instance == null) {
                instance = BannerAdsConsentManager(context)
            }

            return instance!!
        }
    }
}