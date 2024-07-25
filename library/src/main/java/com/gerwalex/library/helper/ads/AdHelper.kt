package com.gerwalex.library.helper.ads

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun BannerAdView(adSize: AdSize, adUnitId: String, modifier: Modifier = Modifier) {
    val isInEditMode = LocalInspectionMode.current
    if (isInEditMode) {
        Text(
            modifier = modifier
                .fillMaxWidth()
                .background(Color.Red)
                .padding(horizontal = 2.dp, vertical = 6.dp),
            textAlign = TextAlign.Center,
            color = Color.White,
            text = "BannerAdView Here",
        )
    } else {
        AndroidView(
            modifier = modifier.fillMaxWidth(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(adSize)
                    this.adUnitId = adUnitId

                    AdRequest.Builder().build().apply {
                        Log.d("AdHelpder", "isTestDevice: ${this.isTestDevice(context)}")
                        loadAd(this)
                    }
                }
            }
        )
    }
}

@Composable
fun BannerAdView(
    adUnitId: String, maxHeight: Int,
    modifier: Modifier = Modifier,
    adListener: AdListener? = null,
) {
    val isInEditMode = LocalInspectionMode.current
    if (isInEditMode) {
        Text(
            modifier = modifier
                .fillMaxWidth()
                .background(Color.Red)
                .padding(horizontal = 2.dp, vertical = 6.dp),
            textAlign = TextAlign.Center,
            color = Color.White,
            text = "BannerAdView Here",
        )
    } else {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val adSize = remember {
                AdSize.getInlineAdaptiveBannerAdSize(
                    maxWidth.value.toInt(), maxHeight
                )
            }
            AndroidView(
                modifier = modifier.fillMaxWidth(),
                factory = { context ->
                    AdView(context).apply {
                        setAdSize(adSize)
                        adListener?.let { this.adListener = it }
                        this.adUnitId = adUnitId
                        AdRequest.Builder().build().apply {
                            Log.d("AdHelpder", "isTestDevice: ${this.isTestDevice(context)}")
                            loadAd(this)
                        }
                    }
                }
            )
        }
    }
}


@Composable
fun rememberRewardedInterstatialAd(
    rewarded_interstitial_ad_id: String,
    modifier: Modifier = Modifier,
    onAdLoadAdError: (LoadAdError) -> Unit,
): StateFlow<RewardedInterstitialAd?> {
    val loadedAd = MutableStateFlow<RewardedInterstitialAd?>(null)
    val context = LocalContext.current

    RewardedInterstitialAd.load(
        context,
        rewarded_interstitial_ad_id,
        AdRequest.Builder().build(),
        object : RewardedInterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedInterstitialAd) {
                loadedAd.value = ad
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                onAdLoadAdError(loadAdError)
            }
        })
    return remember { loadedAd }
}
