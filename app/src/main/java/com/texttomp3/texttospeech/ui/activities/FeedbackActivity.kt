package com.texttomp3.texttospeech.ui.activities

import android.content.Intent
import android.os.Build
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.texttomp3.texttospeech.adapters.PaddingDecoration
import com.texttomp3.texttospeech.adapters.ProblemAdapter
import com.texttomp3.texttospeech.data.models.Problem
import com.texttomp3.texttospeech.ui.fragments.ThankBottomSheet
import com.texttomp3.texttospeech.R
import com.texttomp3.texttospeech.base.BaseActivity
import com.texttomp3.texttospeech.databinding.ActivityFeedbackBinding
import com.texttomp3.texttospeech.utils.Constants.EMAIL
import com.texttomp3.texttospeech.utils.Constants.THANK_BOTTOM_SHEET
import com.texttomp3.texttospeech.utils.Coroutines
import com.texttomp3.texttospeech.viewmodels.SettingViewModel
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel

class FeedbackActivity : BaseActivity<ActivityFeedbackBinding>(), ProblemAdapter.OnClickListener {
    private val settingViewModel: SettingViewModel by lazy {
        getViewModel()
    }

    private lateinit var adapter: ProblemAdapter

    private val sendFeedbackLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                showThankBottomSheet()
            } else {
                showThankBottomSheet()
            }
        }

    override fun createBinding(): ActivityFeedbackBinding {
        return ActivityFeedbackBinding.inflate(layoutInflater)
    }

    override fun initMain() {
        initView()
        initEvent()
        handleOnBackPressed()
    }

    private fun initView() {
        with(binding) {
            adapter = ProblemAdapter(this@FeedbackActivity)
            rvProblem.adapter = adapter
            val padding = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
            rvProblem.addItemDecoration(PaddingDecoration(padding))
            val layoutManager = FlexboxLayoutManager(this@FeedbackActivity)
            rvProblem.layoutManager = layoutManager

            lifecycleScope.launch {
                settingViewModel.problems.collect {
                    adapter.submitData(it)
                    if (it.any { item -> item.isSelection }) {
                        etText.isEnabled = true
                    } else {
                        etText.text?.clear()
                        etText.isEnabled = false
                    }
                }
            }

            lifecycleScope.launch {
                settingViewModel.detail.collect {
                    if (it.isNotEmpty()) {
                        btSumbit.setBackgroundColor(getColor(R.color.blue))
                        btSumbit.setTextColor(getColor(R.color.white))
                        btSumbit.isEnabled = true
                    } else {
                        btSumbit.setBackgroundColor(getColor(R.color.grey_button))
                        btSumbit.setTextColor(getColor(R.color.black))
                        btSumbit.isEnabled = false
                    }
                }
            }
        }
    }

    private fun initEvent() {
        with(binding) {
            btSumbit.setOnClickListener {
                sendFeedback(etText.text.toString())
            }

            etText.addTextChangedListener {
                settingViewModel.setDetail(it.toString())
            }
        }
    }

    private fun sendFeedback(text: String) {
        kotlin.runCatching {
            Coroutines.default {
                val deviceName = Build.MODEL
                val deviceMan = Build.MANUFACTURER
                val androidV = Build.VERSION.SDK_INT
                val reasons = settingViewModel.problems.first()
                var textReason = buildString {
                    appendLine("--------------------------")
                    reasons.filter { it.isSelection }
                        .forEach { appendLine(it.text) }
                    append("--------------------------")
                }
                textReason = "--------------------------\n$textReason--------------------------"

                val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
                intent.type = "message/rfc822"
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(EMAIL))
                intent.putExtra(Intent.EXTRA_SUBJECT,  getString(R.string.text_feedback_from_app, getString(R.string.app_name)))
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    text + "\n\n" + textReason + "\n\n" +
                            getString(R.string.text_email_device) + deviceMan + " - " + deviceName + "\n"
                            + getString(R.string.text_email_sdk) + androidV
                )
                sendFeedbackLauncher.launch(intent)
            }
        }
    }

    private fun showThankBottomSheet() {
        val thankBottomSheet = ThankBottomSheet.newInstance()
        thankBottomSheet.show(supportFragmentManager, THANK_BOTTOM_SHEET)
    }

    override fun onClick(item: Problem) {
        settingViewModel.setProblem(item.text)
    }

    private fun handleOnBackPressed() {
        binding.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                settingViewModel.reset()
                finish()
            }
        })
    }

}