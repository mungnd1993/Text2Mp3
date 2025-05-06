package com.texttomp3.texttospeech.ui.activities

import android.annotation.SuppressLint
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
import com.texttomp3.texttospeech.helpers.PreferenceHelper
import com.texttomp3.texttospeech.utils.Constants.CYCLE
import com.texttomp3.texttospeech.utils.Constants.PLAN
import com.texttomp3.texttospeech.utils.Constants.PRICE
import com.texttomp3.texttospeech.utils.Coroutines
import com.texttomp3.texttospeech.utils.Utils
import com.texttomp3.texttospeech.viewmodels.SettingViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel

class PremiumActivity : BaseActivity<ActivityPremiumBinding>(){
    override fun createBinding(): ActivityPremiumBinding {
        return ActivityPremiumBinding.inflate(layoutInflater)
    }

    override fun initMain() {
        initView()
        initEvent()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        with(binding) {
            val plan = PreferenceHelper.getInstance(this@PremiumActivity).getString(PLAN)
            val price = PreferenceHelper.getInstance(this@PremiumActivity).getString(PRICE)
            val cycle = PreferenceHelper.getInstance(this@PremiumActivity).getLong(CYCLE)

            tvPlan.text = plan
            tvPrice.text = price
            tvCycle.text = "${getString(R.string.auto_renew)} ${Utils.formatToDate(cycle)}"
        }
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
}
