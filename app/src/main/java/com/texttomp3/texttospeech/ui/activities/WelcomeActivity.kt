package com.texttomp3.texttospeech.ui.activities

import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
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
        addFragment(binding.fcvMain.id, WelcomeFragment.newInstance(), isReplace = true, false)
    }

    private fun initEvent() {
        handleOnBackPressed()
    }

    private fun handleOnBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragmentManager = supportFragmentManager
                if (fragmentManager.backStackEntryCount > 0) {
                    fragmentManager.popBackStack()
                } else {
                    finish()
                }
            }
        })
    }

}