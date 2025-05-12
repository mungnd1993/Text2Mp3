package com.texttomp3.texttospeech.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.OnBackPressedCallback
import com.texttomp3.texttospeech.R
import com.texttomp3.texttospeech.base.BaseActivity
import com.texttomp3.texttospeech.databinding.ActivityPremiumBinding
import androidx.core.net.toUri
import com.android.billingclient.api.ProductDetails
import com.texttomp3.texttospeech.billing.GoogleBillingManager
import com.texttomp3.texttospeech.helpers.PreferenceHelper
import com.texttomp3.texttospeech.utils.Constants.CANCEL_PLAN
import com.texttomp3.texttospeech.utils.Constants.CYCLE
import com.texttomp3.texttospeech.utils.Constants.PLAN
import com.texttomp3.texttospeech.utils.Constants.PRICE
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
        handleOnBackPressed()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        with(binding) {
            val plan = PreferenceHelper.getInstance(this@PremiumActivity).getString(PLAN, getString(R.string.pro))
            val price = PreferenceHelper.getInstance(this@PremiumActivity).getString(PRICE, getString(R.string.undefined))
            val cycle = PreferenceHelper.getInstance(this@PremiumActivity).getLong(CYCLE, 0)

            val isCancel = PreferenceHelper.getInstance(this@PremiumActivity).getBoolean(CANCEL_PLAN, false)

            tvPlan.text = plan
            tvPrice.text = price
            if (cycle > 0) {
                if (isCancel) {
                    tvCycle.text = "${getString(R.string.expires_on)} ${Utils.formatToDate(cycle)}"
                } else {
                    tvCycle.text = "${getString(R.string.auto_renew)} ${Utils.formatToDate(cycle)}"
                }
            } else {
                tvCycle.text = getString(R.string.undefined)
            }
        }
    }

    private fun initEvent() {
        with(binding) {
            btGo.setOnClickListener {
                val uri = "https://play.google.com/store/account/subscriptions".toUri()
                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                    setPackage("com.android.vending")
                }
                startActivity(intent)
            }
        }
    }

    private fun handleOnBackPressed() {
        binding.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        billingManager = GoogleBillingManager(this, this, this, settingViewModel)
        billingManager.getSubscriptionData()
    }

    override fun onGetSubscriptionSuccessful(productDetails: ProductDetails?) {
        TODO("Not yet implemented")
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
