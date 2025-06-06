package com.texttomp3.texttospeech.ads

import android.app.Activity
import com.texttomp3.texttospeech.utils.Constants.AD_UNIT_ID_INTERSTITIAL_ADS
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd

class LoadInterstitialAds {
    companion object {
        private val instance: LoadInterstitialAds? = null
        private var mInterstitialAd: InterstitialAd? = null
        fun getInstance(): LoadInterstitialAds {
            return instance ?: LoadInterstitialAds()
        }
    }

    fun openAdsThenOpenActivity(activity: Activity, listener: InterstitialAdsListener) {
        val application = activity.application
        if (application is MyApplication) {
            mInterstitialAd = application.mInterstitialAd
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(activity)
                mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        listener.onStartActivity()
                        application.loadInterstitial(AD_UNIT_ID_INTERSTITIAL_ADS)
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        application.loadInterstitial(AD_UNIT_ID_INTERSTITIAL_ADS)
                        listener.onStartActivity()
                    }

                    override fun onAdShowedFullScreenContent() {
                    }
                }
            } else {
                application.loadInterstitial(AD_UNIT_ID_INTERSTITIAL_ADS)
                listener.onStartActivity()
            }
        }
    }

    fun showInterstitial(activity: Activity, listener: InterstitialAdsListener) {
        val application = activity.application
        if (application is MyApplication) {
            mInterstitialAd = application.mInterstitialAd
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(activity)
                mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        listener.onDismiss()
                        application.loadInterstitial(AD_UNIT_ID_INTERSTITIAL_ADS)
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        listener.onDismiss()
                        application.loadInterstitial(AD_UNIT_ID_INTERSTITIAL_ADS)
                    }

                    override fun onAdShowedFullScreenContent() {
                    }
                }
            } else {
                listener.onDismiss()
                application.loadInterstitial(AD_UNIT_ID_INTERSTITIAL_ADS)
            }
        }
    }
}