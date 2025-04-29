package com.texttomp3.texttospeech.ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.texttomp3.texttospeech.utils.Constants
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import java.util.Date

class AppOpenManager {
    var isLoadingAd = false
    var isShowingAd = false
    private val LOG_TAG = "AppOpenAdManager"
    private var appOpenAd: AppOpenAd? = null
    private var loadTime: Long = 0

    /**
     * Show the ad if one isn't already showing.
     *
     * @param activity the activity that shows the app open ad
     */
    fun showAdIfAvailable(activity: Activity) {
        showAdIfAvailable(
            activity,
            object : MyApplication.OnShowAdCompleteListener {
                override fun onShowAdComplete() {
                    // Empty because the user will go back to the activity that shows the ad.
                }
            })
    }

    /**
     * Show the ad if one isn't already showing.
     *
     * @param activity                 the activity that shows the app open ad
     * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
     */
    fun showAdIfAvailable(
        activity: Activity,
        onShowAdCompleteListener: MyApplication.OnShowAdCompleteListener
    ) {
//        val isDisplayType = FirebaseRemoteConfig.getInstance().getString(Constants.REMOTE_CONFIG_STRING) == "ok"

//        if (!isDisplayType) {
//            onShowAdCompleteListener.onShowAdComplete()
//            return
//        }

        // If the app open ad is already showing, do not show the ad again.
        if (isShowingAd) {
            return
        }

        // If the app open ad is not available yet, invoke the callback then load the ad.
        if (!isAdAvailable()) {
            onShowAdCompleteListener.onShowAdComplete()
//            loadAd(activity)
            return
        }
        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            /** Called when full screen content is dismissed.  */
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                isShowingAd = false
                onShowAdCompleteListener.onShowAdComplete()
//                loadAd(activity)
            }

            /** Called when fullscreen content failed to show.  */
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                isShowingAd = false
                onShowAdCompleteListener.onShowAdComplete()
//                loadAd(activity)
            }

            /** Called when fullscreen content is shown.  */
            override fun onAdShowedFullScreenContent() {
            }
        }
        isShowingAd = true
        appOpenAd?.show(activity)
    }

    fun loadAd(context: Context, callback: (Boolean) -> Unit) {
        // Do not load ad if there is an unused ad or one is already loading.
        if (isLoadingAd || isAdAvailable()) {
            return
        }
        isLoadingAd = true
        val startTime = System.currentTimeMillis()
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            Constants.AD_UNIT_ID_APP_OPEN_ADS,
            request,
            object : AppOpenAdLoadCallback() {
                /**
                 * Called when an app open ad has loaded.
                 *
                 * @param ad the loaded app open ad.
                 */
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                    startTime.toTimeLoadAds(callback)
                }

                /**
                 * Called when an app open ad has failed to load.
                 *
                 * @param loadAdError the error.
                 */
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAd = false
                    startTime.toTimeLoadAds(callback)
                }
            })

        Handler(Looper.getMainLooper()).postDelayed({
            if (isLoadingAd) {
                isLoadingAd = false
                callback(false)
            }
        }, 4000)
    }

    /**
     * Check xem thời gian load quảng cáo, nếu ít nhất là 2,5 giây thì hiện
     */
    private fun Long.toTimeLoadAds(callback: (Boolean) -> Unit) {
        val elapsedTime =  System.currentTimeMillis() - this
        if (elapsedTime >= 2500) {
            callback(true)
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                callback(true)
            }, 2500 - elapsedTime)
        }
    }

    /**
     * Check if ad was loaded more than n hours ago.
     */
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    /**
     * Check if ad exists and can be shown.
     */
    private fun isAdAvailable(): Boolean {
        // Ad references in the app open beta will time out after four hours, but this time limit
        // may change in future beta versions. For details, see:
        // https://support.google.com/admob/answer/9341964?hl=en
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

}