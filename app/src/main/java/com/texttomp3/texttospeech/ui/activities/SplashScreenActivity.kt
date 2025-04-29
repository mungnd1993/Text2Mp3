package com.texttomp3.texttospeech.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.ProductDetails
import com.texttomp3.texttospeech.ads.MyApplication
import com.texttomp3.texttospeech.R
import com.texttomp3.texttospeech.ads.AppOpenManager
import com.texttomp3.texttospeech.ads.GoogleMobileAdsConsentManager
import com.texttomp3.texttospeech.billing.GoogleBillingManager
import com.texttomp3.texttospeech.databinding.ActivitySplashBinding
import com.texttomp3.texttospeech.helpers.PreferenceHelper
import com.texttomp3.texttospeech.utils.Constants.IS_FIRST_OPEN_APP
import com.texttomp3.texttospeech.viewmodels.SettingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.util.concurrent.atomic.AtomicBoolean

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity(), GoogleBillingManager.OnPurchaseStateChangeListener {
    private val binding: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    private lateinit var billingManager: GoogleBillingManager
    private val settingViewModel: SettingViewModel by lazy {
        getViewModel()
    }

    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)
    private var isFirstOpenApp: Boolean = true
    private var isNavigated = false

    override fun onCreate(savedInstanceState: Bundle?) {
//        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cl_splash)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        hideNavigationBar()
        billingManager = GoogleBillingManager(this, this, this, settingViewModel)
        billingManager.queryPurchases()
        isFirstOpenApp = PreferenceHelper.getInstance(this).getBoolean(IS_FIRST_OPEN_APP, true)
        setupUMP()
    }

    private fun setupUMP() {
        googleMobileAdsConsentManager =
            GoogleMobileAdsConsentManager.getInstance(applicationContext)
        googleMobileAdsConsentManager.gatherConsent(this) { consentError ->
            if (consentError != null) {
                initializeMobileAdsSdk()
            }

            if (googleMobileAdsConsentManager.canRequestAds) {
                initializeMobileAdsSdk()
            }
        }

        if (googleMobileAdsConsentManager.canRequestAds) {
            initializeMobileAdsSdk()
        }
    }

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }
        kotlin.runCatching {
            (application as MyApplication).initializeMobileAdsSdk()

            if (settingViewModel.proVersion.value) {
                lifecycleScope.launch {
                    delay(1000)
                    openMainActivity()
                }
            } else {
                val appOpenAdManager = AppOpenManager()
                appOpenAdManager.loadAd(this) { success ->
                    if (success) {
                        appOpenAdManager.showAdIfAvailable(this, object : MyApplication.OnShowAdCompleteListener {
                            override fun onShowAdComplete() {
                                openMainActivity()
                            }
                        })
                    } else {
                        openMainActivity()
                    }
                }
            }
        }
    }


    private fun openMainActivity() {
        if (isNavigated) return
        isNavigated = true

        if (isFirstOpenApp) {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun hideNavigationBar() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideNavigationBar()
        }
    }

    override fun onGetSubscriptionSuccessful(productDetails: ProductDetails?) {

    }

    override fun onGetProductDetailFailed() {

    }

    override fun onNewSubscribe() {

    }

    override fun onAlreadySubscribed() {

    }

    override fun onHaveNotSubscribed() {

    }

    override fun onFreeTrialActive(remainingDays: Int) {

    }

    override fun onPurchasePending() {
    }
}