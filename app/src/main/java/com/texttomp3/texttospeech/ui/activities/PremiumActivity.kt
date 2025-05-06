package com.texttomp3.texttospeech.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.texttomp3.texttospeech.R
import com.texttomp3.texttospeech.base.BaseActivity
import com.texttomp3.texttospeech.databinding.ActivityPremiumBinding
import androidx.core.net.toUri
import com.android.billingclient.api.ProductDetails
import com.texttomp3.texttospeech.billing.GoogleBillingManager
import com.texttomp3.texttospeech.utils.Coroutines
import com.texttomp3.texttospeech.utils.Utils
import com.texttomp3.texttospeech.viewmodels.SettingViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel

class PremiumActivity : BaseActivity<ActivityPremiumBinding>(), GoogleBillingManager.OnPurchaseStateChangeListener {
    private lateinit var billingManager: GoogleBillingManager
    private val settingViewModel: SettingViewModel by lazy {
        getViewModel()
    }

    override fun createBinding(): ActivityPremiumBinding {
        return ActivityPremiumBinding.inflate(layoutInflater)
    }

    override fun initMain() {
        initView()
        initEvent()
    }

    private fun initView() {
        billingManager = GoogleBillingManager(this, this, this, settingViewModel)
        billingManager.getProductDetail()
        billingManager.getSubscriptionData()
    }

    private fun initEvent() {
        with(binding) {
            btGo.setOnClickListener {
                val uri = "https://play.google.com/store/account/subscriptions".toUri()
                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                    setPackage("com.android.vending") // Chỉ mở bằng Google Play Store
                }
                startActivity(intent)

            }

            ivBack.setOnClickListener {
                finish()
            }
        }
    }

    override fun onGetSubscriptionSuccessful(productDetails: ProductDetails?) {
        Coroutines.main {
            val list = productDetails!!.subscriptionOfferDetails
            Utils.log("hshshshss", productDetails.toString())
            if (list != null) {
                Utils.log("hshshshss", list.toString())
            }
        }
    }

    override fun onGetProductDetailFailed() {
        TODO("Not yet implemented")
    }

    override fun onNewSubscribe() {
        TODO("Not yet implemented")
    }

    override fun onAlreadySubscribed() {
        TODO("Not yet implemented")
    }

    override fun onHaveNotSubscribed() {
        TODO("Not yet implemented")
    }

    override fun onFreeTrialActive(remainingDays: Int) {
        TODO("Not yet implemented")
    }

    override fun onPurchasePending() {
        TODO("Not yet implemented")
    }

}
