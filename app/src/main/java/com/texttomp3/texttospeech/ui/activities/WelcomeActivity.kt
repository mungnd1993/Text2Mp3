package com.texttomp3.texttospeech.ui.activities

import com.texttomp3.texttospeech.ui.fragments.WelcomeFragment
import com.texttomp3.texttospeech.base.BaseActivity
import com.texttomp3.texttospeech.databinding.ActivityWelcomeBinding

class WelcomeActivity : BaseActivity<ActivityWelcomeBinding>() {
    override fun createBinding(): ActivityWelcomeBinding {
        return ActivityWelcomeBinding.inflate(layoutInflater)
    }

    override fun initMain() {
        initView()
        initEvent()
    }

    private fun initView() {
        addFragment(binding.fcvMain.id, WelcomeFragment.newInstance(), isReplace = true, true)
    }

    private fun initEvent() {

    }
}