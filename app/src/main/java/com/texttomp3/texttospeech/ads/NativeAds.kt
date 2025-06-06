package com.texttomp3.texttospeech.ads

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import coil.load
import com.texttomp3.texttospeech.utils.Constants.AD_UNIT_ID_NATIVE_ADS
import com.texttomp3.texttospeech.R
import com.texttomp3.texttospeech.utils.Constants.AD_UNIT_ID_NATIVE_VIDEO_ADS
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MediaAspectRatio
import com.google.android.gms.ads.VideoController
import com.google.android.gms.ads.VideoController.VideoLifecycleCallbacks
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAd.OnNativeAdLoadedListener
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

class NativeAds {
    companion object {
        private val instance: NativeAds? = null
        @SuppressLint("StaticFieldLeak")
        private lateinit var adLoader: AdLoader
        fun getInstance(): NativeAds {
            return instance ?: NativeAds()
        }
    }


    fun navigateAds(
        activity: Activity,
        frameLayout: FrameLayout,
        idNativeAds: Int,
        failAdlNavigate: NativeAdsListener
    ) {
        adLoader = AdLoader.Builder(activity, AD_UNIT_ID_NATIVE_ADS)
            .forNativeAd(OnNativeAdLoadedListener { nativeAd ->
                try {
                    if (!adLoader.isLoading) {
                        val adView = activity.layoutInflater
                            .inflate(idNativeAds, null) as NativeAdView
                        populateNativeAdView(activity, nativeAd, adView)
                        frameLayout.removeAllViews()
                        frameLayout.addView(adView)
                    }
                    if (activity.isDestroyed) {
                        nativeAd.destroy()
                        return@OnNativeAdLoadedListener
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    failAdlNavigate.onFail()
                }

                override fun onAdLoaded() {
                    failAdlNavigate.onSuccess()
                    super.onAdLoaded()
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setRequestCustomMuteThisAd(true)
                    .build()
            )
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun navigateAdsMedium(
        activity: Activity,
        frameLayout: FrameLayout,
        idNativeAds: Int,
        failAdlNavigate: NativeAdsListener
    ) {
        val adOptions =
            NativeAdOptions.Builder().setMediaAspectRatio(MediaAspectRatio.LANDSCAPE).build()
        adLoader = AdLoader.Builder(activity, AD_UNIT_ID_NATIVE_ADS)
            .forNativeAd(OnNativeAdLoadedListener { nativeAd ->
                try {
                    if (!adLoader.isLoading) {
                        val adView = activity.layoutInflater
                            .inflate(idNativeAds, null) as NativeAdView
                        populateNativeAdViewMedium(activity, nativeAd, adView)
                        frameLayout.removeAllViews()
                        frameLayout.addView(adView)
                    }
                    if (activity.isDestroyed) {
                        nativeAd.destroy()
                        return@OnNativeAdLoadedListener
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Xử lý lỗi bằng cách ghi nhật ký, thay đổi giao diện người dùng, v.v.
                    failAdlNavigate.onFail()
                }

                override fun onAdLoaded() {
                    failAdlNavigate.onSuccess()
                    super.onAdLoaded()
                }
            })
            .withNativeAdOptions(adOptions)
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }


    private fun populateNativeAdView(activity: Activity, nativeAd: NativeAd, adView: NativeAdView) {

        adView.headlineView = adView.findViewById(R.id.tv_ad_headline)
        adView.bodyView = adView.findViewById(R.id.tv_ad_body)
        adView.callToActionView = adView.findViewById(R.id.bt_ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.iv_ad_app_icon)

        // Headline
        (adView.headlineView as? TextView)?.apply {
            text = nativeAd.headline
            background = null
        }

        // Body
        (adView.bodyView as? TextView)?.apply {
            if (nativeAd.body == null) {
                visibility = View.INVISIBLE
            } else {
                text = nativeAd.body
                visibility = View.VISIBLE
            }
            background = null
        }

        // Call to Action
        (adView.callToActionView as? Button)?.apply {
            if (nativeAd.callToAction == null) {
                visibility = View.INVISIBLE
            } else {
                text = nativeAd.callToAction
                visibility = View.VISIBLE
            }
            backgroundTintList = ContextCompat.getColorStateList(activity, R.color.blue_bold)
        }

        // Icon
        (adView.iconView as? ImageView)?.apply {
            background = null
            if (nativeAd.icon == null) {
                visibility = View.GONE
            } else {
                if (nativeAd.icon?.drawable == null) {
                    load(nativeAd.icon!!.uri) {
                        crossfade(true)
                    }
                } else {
                    setImageDrawable(nativeAd.icon?.drawable)
                }
                visibility = View.VISIBLE
            }
        }

        // Tell the SDK that the native ad view is ready
        adView.setNativeAd(nativeAd)

        // Video controller setup
        val vc = nativeAd.mediaContent?.videoController
        vc?.let {
            if (it.hasVideoContent()) {
                it.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
                    override fun onVideoEnd() {
                        super.onVideoEnd()
                        // Optional: handle video end
                    }
                }
            }
        }
    }


    private fun populateNativeAdViewMedium(
        activity: Activity,
        nativeAd: NativeAd,
        adView: NativeAdView
    ) {
        adView.mediaView = adView.findViewById(R.id.mv_ads)
        adView.headlineView = adView.findViewById(R.id.tv_ad_headline)
        adView.bodyView = adView.findViewById(R.id.tv_ad_body)
        adView.callToActionView = adView.findViewById(R.id.bt_ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.iv_ad_app_icon)

        // Headline
        (adView.headlineView as? TextView)?.apply {
            text = nativeAd.headline
            background = null
        }

        // Media Content
        adView.mediaView?.mediaContent = nativeAd.mediaContent

        // MediaView scaling logic
        adView.mediaView?.setOnHierarchyChangeListener(object :
            ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewAdded(parent: View, child: View) {
                if (child is ImageView) {
                    val drawable = child.drawable as? BitmapDrawable ?: return
                    val bitmap = drawable.bitmap
                    val width = bitmap.width
                    val height = bitmap.height
                    val heightHor = (activity.resources.displayMetrics.widthPixels * 0.6).toInt()
                    if (width > height) {
                        adView.mediaView?.layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        child.adjustViewBounds = true
                    } else {
                        adView.mediaView?.setImageScaleType(ImageView.ScaleType.CENTER)
                        adView.mediaView?.layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            heightHor
                        )
                        child.adjustViewBounds = true
                    }
                }
            }

            override fun onChildViewRemoved(parent: View, child: View) {}
        })

        // Body
        (adView.bodyView as? TextView)?.apply {
            background = null
            if (nativeAd.body == null) {
                visibility = View.INVISIBLE
            } else {
                text = nativeAd.body
                visibility = View.VISIBLE
            }
        }

        // Call to Action
        (adView.callToActionView as? Button)?.apply {
            if (nativeAd.callToAction == null) {
                visibility = View.INVISIBLE
            } else {
                text = nativeAd.callToAction
                visibility = View.VISIBLE
            }
            backgroundTintList = ContextCompat.getColorStateList(activity, R.color.blue_bold)
        }

        // Icon
        (adView.iconView as? ImageView)?.apply {
            background = null
            if (nativeAd.icon == null) {
                visibility = View.GONE
            } else {
                if (nativeAd.icon?.drawable == null) {
                    nativeAd.icon?.uri?.let { uri ->
                        load(uri) {
                            crossfade(true)
                        }
                    }
                } else {
                    setImageDrawable(nativeAd.icon?.drawable)
                }
                visibility = View.VISIBLE
            }
        }

        // Finalize the ad view
        adView.setNativeAd(nativeAd)

        // Video handling
        nativeAd.mediaContent?.videoController?.let { vc ->
            if (vc.hasVideoContent()) {
                vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
                    override fun onVideoEnd() {
                        super.onVideoEnd()
                    }
                }
            }
        }
    }

}