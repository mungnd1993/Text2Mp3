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
import coil.load
import com.texttomp3.texttospeech.utils.Constants.AD_UNIT_ID_NATIVE_ADS
import com.texttomp3.texttospeech.R
import com.texttomp3.texttospeech.utils.Constants.AD_UNIT_ID_NATIVE_VIDEO_ADS
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MediaAspectRatio
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
        adLoader = AdLoader.Builder(activity, AD_UNIT_ID_NATIVE_VIDEO_ADS)
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
        (adView.headlineView as TextView?)?.text = nativeAd.headline
        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.INVISIBLE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as TextView?)?.text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.INVISIBLE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as Button?)?.text = nativeAd.callToAction
        }
        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.GONE
        } else {
            if (nativeAd.icon?.drawable == null) {
                (adView.iconView as ImageView).load(nativeAd.icon!!.uri) {
                    crossfade(true)
                }
            } else {
                (adView.iconView as ImageView?)!!.setImageDrawable(
                    nativeAd.icon?.drawable
                )
            }
            adView.iconView!!.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        val vc = nativeAd.mediaContent!!.videoController

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {
            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.videoLifecycleCallbacks = object : VideoLifecycleCallbacks() {
                override fun onVideoEnd() {
                    super.onVideoEnd()
                }
            }
        }
    }


    private fun populateNativeAdViewMedium(
        activity: Activity,
        nativeAd: NativeAd,
        adView: NativeAdView
    ) {
        adView.mediaView = adView.findViewById<View>(R.id.mv_ads) as MediaView
        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.tv_ad_headline)
        adView.bodyView = adView.findViewById(R.id.tv_ad_body)
        adView.callToActionView = adView.findViewById(R.id.bt_ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.iv_ad_app_icon)
        //        adView.setStarRatingView(adView.findViewById(R.id.rb_ad_stars));
//        adView.setStarRatingView(adView.findViewById(R.id.rb_ads));
        // The headline and mediaContent are guaranteed to be in every NativeAd.
        (adView.headlineView as TextView?)!!.text = nativeAd.headline
        adView.mediaView!!.mediaContent = nativeAd.mediaContent

        // điều chỉnh kích thước media view
        adView.mediaView?.setOnHierarchyChangeListener(object :
            ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewAdded(parent: View, child: View) {
                if (child is ImageView) {
                    val imageView = child
                    val drawable = imageView.drawable as BitmapDrawable
                    val bitmap = drawable.bitmap
                    val width = bitmap.width
                    val height = bitmap.height
                    val heightHor = (activity.resources.displayMetrics.widthPixels * 0.6).toInt()
                    if (width > height) {
                        val params = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        adView.mediaView!!.layoutParams = params
                        imageView.adjustViewBounds = true
                    } else {
                        adView.mediaView!!.setImageScaleType(ImageView.ScaleType.CENTER)
                        val params = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            heightHor
                        )
                        adView.mediaView!!.layoutParams = params
                        imageView.adjustViewBounds = true
                    }
                }
            }

            override fun onChildViewRemoved(parent: View, child: View) {}
        })
        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.INVISIBLE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as TextView?)?.text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.INVISIBLE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as Button?)?.text = nativeAd.callToAction
        }
        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.GONE
        } else {
            if (nativeAd.icon!!.drawable == null) {
                (adView.iconView as? ImageView)?.let { imageView ->
                    nativeAd.icon?.uri?.let { uri ->
                        imageView.load(uri) {
                            crossfade(true)
                        }
                    }
                }
            } else {
                (adView.iconView as ImageView?)?.setImageDrawable(
                    nativeAd.icon!!.drawable
                )
            }
            adView.iconView?.visibility = View.VISIBLE
        }

//        if (nativeAd.getStarRating() == null) {
//            adView.getStarRatingView().setVisibility(View.INVISIBLE);
//        } else {
//            ((RatingBar) adView.getStarRatingView())
//                    .setRating(nativeAd.getStarRating().floatValue());
//            adView.getStarRatingView().setVisibility(View.VISIBLE);
//            Drawable drawableReview = ((RatingBar) adView.getStarRatingView()).getProgressDrawable();
//            drawableReview.setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.SRC_ATOP);
//        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        val vc = nativeAd.mediaContent!!.videoController

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {
            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.videoLifecycleCallbacks = object : VideoLifecycleCallbacks() {
                override fun onVideoEnd() {
                    super.onVideoEnd()
                }
            }
        }
    }
}