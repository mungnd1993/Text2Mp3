package com.texttomp3.texttospeech.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import com.texttomp3.texttospeech.R
import com.texttomp3.texttospeech.base.BaseActivity
import com.texttomp3.texttospeech.databinding.ActivityPremiumBinding
import androidx.core.net.toUri
import com.texttomp3.texttospeech.helpers.PreferenceHelper
import com.texttomp3.texttospeech.utils.Constants.CYCLE
import com.texttomp3.texttospeech.utils.Constants.PLAN
import com.texttomp3.texttospeech.utils.Constants.PRICE
import com.texttomp3.texttospeech.utils.Utils

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
            val plan = PreferenceHelper.getInstance(this@PremiumActivity).getString(PLAN, getString(R.string.pro))
            val price = PreferenceHelper.getInstance(this@PremiumActivity).getString(PRICE, getString(R.string.undefined))
            val cycle = PreferenceHelper.getInstance(this@PremiumActivity).getLong(CYCLE, 0)

            tvPlan.text = plan
            tvPrice.text = price
            if (cycle > 0) {
                tvCycle.text = "${getString(R.string.auto_renew)} ${Utils.formatToDate(cycle)}"
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

            ivBack.setOnClickListener {
                finish()
            }
        }
    }
}
