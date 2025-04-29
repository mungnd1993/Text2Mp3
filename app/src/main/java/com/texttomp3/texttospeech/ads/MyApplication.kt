package com.texttomp3.texttospeech.ads

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.texttomp3.texttospeech.KoinApplication
import com.texttomp3.texttospeech.utils.Constants.AD_UNIT_ID_INTERSTITIAL_ADS
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd

/**
 * Application class that initializes, loads and show ads when activities change states.
 * Lớp ứng dụng khởi tạo, tải và hiển thị quảng cáo khi các hoạt động thay đổi trạng thái.
 */
open class MyApplication: KoinApplication(), Application.ActivityLifecycleCallbacks, LifecycleObserver {
    var mInterstitialAd: InterstitialAd? = null
    var rewardedAd: RewardedAd? = null

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun initializeMobileAdsSdk() {
        MobileAds.initialize(
            this
        ) { }
        loadInterstitial(AD_UNIT_ID_INTERSTITIAL_ADS)

//        loadRewardedAd();
//        showRewardedVideo();
    }


    fun loadInterstitial(id: String) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            id,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    interstitialAd.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                mInterstitialAd = null
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                // Called when fullscreen content failed to show.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null
                            }

                            override fun onAdShowedFullScreenContent() {}
                        }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    mInterstitialAd = null
                }
            })
    }

//    fun loadRewardedAd() {
//        if (rewardedAd == null) {
//            isLoading = true
//            val adRequest = AdRequest.Builder().build()
//            RewardedAd.load(this,
//                AD_UNIT_ID_AD_REWARDED,
//                adRequest,
//                object : RewardedAdLoadCallback() {
//                    fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                        super.onAdFailedToLoad(loadAdError)
//                        rewardedAd = null
//                        isLoading = false
//                        Log.d(TAG, "adFailedToLoad")
//                        //                            Toast.makeText(currentActivity, "FailedLoad", Toast.LENGTH_SHORT).show();
//                    }
//
//                    fun onAdLoaded(ad: RewardedAd) {
//                        super.onAdLoaded(ad)
//                        rewardedAd = ad
//                        isLoading = false
//                        Log.d(TAG, "onAdLoaded")
//                        //                            Toast.makeText(currentActivity, "Loaded", Toast.LENGTH_SHORT).show();
//                    }
//                })
//        }
//    }
//
//    fun loadReward(activity: Activity?, rewardedAdsListener: RewardedAdsListener) {
//        if (rewardedAd == null) {
//            isLoading = true
//            val adRequest = AdRequest.Builder().build()
//            RewardedAd.load(this,
//                AD_UNIT_ID_AD_REWARDED,
//                adRequest,
//                object : RewardedAdLoadCallback() {
//                    fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                        super.onAdFailedToLoad(loadAdError)
//                        rewardedAd = null
//                        isLoading = false
//                        Log.d(TAG, "adFailedToLoad")
//                        //                            Toast.makeText(currentActivity, "FailedLoad", Toast.LENGTH_SHORT).show();
//                    }
//
//                    fun onAdLoaded(ad: RewardedAd) {
//                        super.onAdLoaded(ad)
//                        rewardedAd = ad
//                        isLoading = false
//                        Log.d(TAG, "onAdLoaded")
//                        rewardedAd!!.fullScreenContentCallback =
//                            object : FullScreenContentCallback() {
//                                override fun onAdShowedFullScreenContent() {
//                                    // Called when ad is shown.
//                                    Log.d(TAG, "onAdShowedFullScreenContent")
//                                    rewardedAdsListener.onCloseProgressBar()
//                                }
//
//                                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
//                                    // Called when ad fails to show.
//                                    Log.d(TAG, "onAdFailedToShowFullScreenContent")
//                                    // Don't forget to set the ad reference to null so you
//                                    // don't show the ad a second time.
//                                    rewardedAd = null
//                                }
//
//                                override fun onAdDismissedFullScreenContent() {
//                                    // Called when ad is dismissed.
//                                    Log.d(TAG, "onAdDismissedFullScreenContent")
//                                    // Don't forget to set the ad reference to null so you
//                                    // don't show the ad a second time.
//                                    rewardedAd = null
//                                    rewardedAdsListener.onStartActivity()
//                                    // Preload the next rewarded ad.
//                                }
//                            }
//                        rewardedAd!!.show(activity!!, object : OnUserEarnedRewardListener() {
//                            fun onUserEarnedReward(rewardItem: RewardItem) {}
//                        })
//                    }
//                })
//        }
//    }
//
//    fun showRewardedVideo() {
//        if (rewardedAd == null) {
//            Log.d("TAG", "The rewarded ad wasn't ready yet.")
//            return
//        }
//        rewardedAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
//            override fun onAdShowedFullScreenContent() {
//                // Called when ad is shown.
//                Log.d(TAG, "onAdShowedFullScreenContent")
//                //                        Toast.makeText(currentActivity, "onAdShowedFullScreenContent", Toast.LENGTH_SHORT).show();
//            }
//
//            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
//                // Called when ad fails to show.
//                Log.d(TAG, "onAdFailedToShowFullScreenContent")
//                // Don't forget to set the ad reference to null so you
//                // don't show the ad a second time.
//                rewardedAd = null
//                //                        Toast.makeText(currentActivity, "onAdFailedToShowFullScreenContent", Toast.LENGTH_SHORT).show();
//                loadRewardedAd()
//            }
//
//            override fun onAdDismissedFullScreenContent() {
//                // Called when ad is dismissed.
//                Log.d(TAG, "onAdDismissedFullScreenContent")
//                // Don't forget to set the ad reference to null so you
//                // don't show the ad a second time.
//                rewardedAd = null
//                //                        Toast.makeText(currentActivity, "onAdDismissedFullScreenContent", Toast.LENGTH_SHORT).show();
//                // Preload the next rewarded ad.
//                loadRewardedAd()
//            }
//        }
//        rewardedAd!!.show(
//            activity,
//            object : OnUserEarnedRewardListener() {
//                fun onUserEarnedReward(rewardItem: RewardItem) {}
//            })
//    }

    //    public void loadRewardedAd() {
    //        if (rewardedAd == null) {
    //            isLoading = true;
    //            AdRequest adRequest = new AdRequest.Builder().build();
    //            RewardedAd.load(this,
    //                    AD_UNIT_ID_AD_REWARDED,
    //                    adRequest,
    //                    new RewardedAdLoadCallback() {
    //                        @Override
    //                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
    //                            super.onAdFailedToLoad(loadAdError);
    //                            rewardedAd = null;
    //                            isLoading = false;
    //                            Log.d(TAG, "adFailedToLoad");
    ////                            Toast.makeText(currentActivity, "FailedLoad", Toast.LENGTH_SHORT).show();
    //                        }
    //
    //                        @Override
    //                        public void onAdLoaded(@NonNull RewardedAd ad) {
    //                            super.onAdLoaded(ad);
    //                            rewardedAd = ad;
    //                            isLoading = false;
    //                            Log.d(TAG, "onAdLoaded");
    ////                            Toast.makeText(currentActivity, "Loaded", Toast.LENGTH_SHORT).show();
    //                        }
    //                    });
    //        }
    //    }
    //    public void loadReward(Activity activity, RewardedAdsListener rewardedAdsListener) {
    //        if (rewardedAd == null) {
    //            isLoading = true;
    //            AdRequest adRequest = new AdRequest.Builder().build();
    //            RewardedAd.load(this,
    //                    AD_UNIT_ID_AD_REWARDED,
    //                    adRequest,
    //                    new RewardedAdLoadCallback() {
    //                        @Override
    //                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
    //                            super.onAdFailedToLoad(loadAdError);
    //                            rewardedAd = null;
    //                            isLoading = false;
    //                            Log.d(TAG, "adFailedToLoad");
    ////                            Toast.makeText(currentActivity, "FailedLoad", Toast.LENGTH_SHORT).show();
    //                        }
    //
    //                        @Override
    //                        public void onAdLoaded(@NonNull RewardedAd ad) {
    //                            super.onAdLoaded(ad);
    //                            rewardedAd = ad;
    //                            isLoading = false;
    //                            Log.d(TAG, "onAdLoaded");
    //                            rewardedAd.setFullScreenContentCallback(
    //                                    new FullScreenContentCallback() {
    //                                        @Override
    //                                        public void onAdShowedFullScreenContent() {
    //                                            // Called when ad is shown.
    //                                            Log.d(TAG, "onAdShowedFullScreenContent");
    //                                            rewardedAdsListener.onCloseProgressBar();
    //                                        }
    //
    //                                        @Override
    //                                        public void onAdFailedToShowFullScreenContent(AdError adError) {
    //                                            // Called when ad fails to show.
    //                                            Log.d(TAG, "onAdFailedToShowFullScreenContent");
    //                                            // Don't forget to set the ad reference to null so you
    //                                            // don't show the ad a second time.
    //                                            rewardedAd = null;
    //                                        }
    //
    //                                        @Override
    //                                        public void onAdDismissedFullScreenContent() {
    //                                            // Called when ad is dismissed.
    //                                            Log.d(TAG, "onAdDismissedFullScreenContent");
    //                                            // Don't forget to set the ad reference to null so you
    //                                            // don't show the ad a second time.
    //                                            rewardedAd = null;
    //                                            rewardedAdsListener.onStartActivity();
    //                                            // Preload the next rewarded ad.
    //                                        }
    //                                    });
    //                            rewardedAd.show(activity, new OnUserEarnedRewardListener() {
    //                                @Override
    //                                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
    //
    //                                }
    //                            });
    //                        }
    //                    });
    //
    //        }
    //    }
    //    public void showRewardedVideo() {
    //        if (rewardedAd == null) {
    //            Log.d("TAG", "The rewarded ad wasn't ready yet.");
    //            return;
    //        }
    //        rewardedAd.setFullScreenContentCallback(
    //                new FullScreenContentCallback() {
    //                    @Override
    //                    public void onAdShowedFullScreenContent() {
    //                        // Called when ad is shown.
    //                        Log.d(TAG, "onAdShowedFullScreenContent");
    ////                        Toast.makeText(currentActivity, "onAdShowedFullScreenContent", Toast.LENGTH_SHORT).show();
    //                    }
    //
    //                    @Override
    //                    public void onAdFailedToShowFullScreenContent(AdError adError) {
    //                        // Called when ad fails to show.
    //                        Log.d(TAG, "onAdFailedToShowFullScreenContent");
    //                        // Don't forget to set the ad reference to null so you
    //                        // don't show the ad a second time.
    //                        rewardedAd = null;
    ////                        Toast.makeText(currentActivity, "onAdFailedToShowFullScreenContent", Toast.LENGTH_SHORT).show();
    //                        loadRewardedAd();
    //                    }
    //
    //                    @Override
    //                    public void onAdDismissedFullScreenContent() {
    //                        // Called when ad is dismissed.
    //                        Log.d(TAG, "onAdDismissedFullScreenContent");
    //                        // Don't forget to set the ad reference to null so you
    //                        // don't show the ad a second time.
    //                        rewardedAd = null;
    ////                        Toast.makeText(currentActivity, "onAdDismissedFullScreenContent", Toast.LENGTH_SHORT).show();
    //                        // Preload the next rewarded ad.
    //                        loadRewardedAd();
    //                    }
    //                });
    //        rewardedAd.show(
    //                activity,
    //                new OnUserEarnedRewardListener() {
    //                    @Override
    //                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
    //
    //                    }
    //                });
    //    }
    /**
     * Interface definition for a callback to be invoked when an app open ad is complete
     * (i.e. dismissed or fails to show).
     */
    interface OnShowAdCompleteListener {
        fun onShowAdComplete()
    }


    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {

        // An ad activity is started when an ad is showing, which could be AdActivity class from Google
        // SDK or another activity class implemented by a third party mediation partner. Updating the
        // currentActivity only when an ad is not showing will ensure it is not an ad activity, but the
        // one that shows the ad.
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }
}