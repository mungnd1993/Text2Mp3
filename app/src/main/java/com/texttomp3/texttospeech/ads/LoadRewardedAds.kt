package com.texttomp3.texttospeech.ads

class LoadRewardedAds {
    private val instance: LoadRewardedAds? = null

    fun getInstance(): LoadRewardedAds {
        return instance ?: LoadRewardedAds()
    }

//    fun openRewardAdsThenOpenActivity(activity: Activity, listener: RewardedAdsListener) {
//        val application: Application = activity.getApplication()
//        if (application is MyApplication) {
//            val myApplication = application as MyApplication
//            myApplication.loadReward(activity, listener)
//            myApplication.showRewardedVideo()
//            if (myApplication.rewardedAd != null) {
//                myApplication.rewardedAd!!.show(activity, object : OnUserEarnedRewardListener() {
//                    fun onUserEarnedReward(@NonNull rewardItem: RewardItem?) {}
//                })
//                myApplication.rewardedAd!!.setFullScreenContentCallback(object :
//                    FullScreenContentCallback() {
//                    fun onAdDismissedFullScreenContent() {
//                        super.onAdDismissedFullScreenContent()
//                        Log.d("TAG", "onAdDismissedNgoai")
//                        listener.onStartActivity()
//                        myApplication.rewardedAd = null
//                        myApplication.loadRewardedAd()
//                    }
//
//                    fun onAdFailedToShowFullScreenContent(@NonNull adError: AdError?) {
//                        super.onAdFailedToShowFullScreenContent(adError)
//                        myApplication.rewardedAd = null
//                        myApplication.loadRewardedAd()
//                        Log.d("TAG", "onAdFailedToShowFullScreenContentNGaoi")
//                    }
//                })
//            }
//        }
//    }
}