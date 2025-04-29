package com.texttomp3.texttospeech.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.billingclient.api.ProductDetails
import com.texttomp3.texttospeech.R
import com.texttomp3.texttospeech.base.BaseFragment
import com.texttomp3.texttospeech.billing.GoogleBillingManager
import com.texttomp3.texttospeech.databinding.FragmentUpgrade2Binding
import com.texttomp3.texttospeech.ui.activities.MainActivity
import com.texttomp3.texttospeech.utils.Constants.DOWNLOAD
import com.texttomp3.texttospeech.utils.Constants.LINK_PRIVACY
import com.texttomp3.texttospeech.utils.Constants.LINK_TERM
import com.texttomp3.texttospeech.utils.Coroutines
import com.texttomp3.texttospeech.utils.Utils
import com.texttomp3.texttospeech.viewmodels.SettingViewModel
import io.github.vejei.cupertinoswitch.CupertinoSwitch
import org.koin.androidx.viewmodel.ext.android.getViewModel

class Upgrade2Fragment(private val type: String) : BaseFragment<FragmentUpgrade2Binding>(), GoogleBillingManager.OnPurchaseStateChangeListener {
    private lateinit var billingManager: GoogleBillingManager
    private var productDetails: ProductDetails? = null
    private var offerToken: String? = null
    private val settingViewModel: SettingViewModel by lazy {
        getViewModel()
    }
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        boolean: Boolean
    ): FragmentUpgrade2Binding {
        return FragmentUpgrade2Binding.inflate(inflater, container, boolean)
    }

    override fun initMain() {
        initView()
        initEvent()
    }

    private fun initView() {
        billingManager = GoogleBillingManager(requireContext(), requireActivity(), this, settingViewModel)
        billingManager.getProductDetail()

        val textDiscount = getString(R.string.original)
        val spannableString = SpannableString(textDiscount)
        spannableString.setSpan(
            StrikethroughSpan(),
            0,
            textDiscount.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val textPolicy = getString(R.string.policy)
        val spannableString2 = SpannableString(textPolicy)
        spannableString2.setSpan(
            UnderlineSpan(),
            0,
            textPolicy.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val textTerm = getString(R.string.terms_of_use)
        val spannableString3 = SpannableString(textTerm)
        spannableString3.setSpan(
            UnderlineSpan(),
            0,
            textTerm.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        with(binding) {
            tvPolicy.text = spannableString2
            tvTerm.text = spannableString3

            if (type == DOWNLOAD) {
                btContinue.visibility = View.GONE
                btAds.visibility = View.GONE
                clSecured.visibility = View.GONE
                clTry.visibility = View.VISIBLE
                ivClose.visibility = View.VISIBLE
            }
        }
    }

    private fun initEvent() {
        with(binding) {
            btAds.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }

            btContinue.setOnClickListener {
                if (productDetails != null) {
                    billingManager.upgradeToProVersion(productDetails!!, offerToken)
                }
            }

            swFree.setOnStateChangeListener(object : CupertinoSwitch.OnStateChangeListener {
                override fun onChanged(view: CupertinoSwitch, checked: Boolean) {
                }

                override fun onSwitchOn(view: CupertinoSwitch) {
                    tvHeading1.text = getString(R.string.three_days_free_trial_auto_renewal)
                    tvHeading2.text = getString(R.string.three_days_free_trial_auto_renewal)
                }

                override fun onSwitchOff(view: CupertinoSwitch) {
                    tvHeading1.text = getString(R.string.billed_week)
                    tvHeading2.text = getString(R.string.billed_year)
                }
            })


            tvPolicy.setOnClickListener {
                startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(LINK_PRIVACY))
                )
            }

            tvTerm.setOnClickListener {
                startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(LINK_TERM))
                )
            }

            ivClose.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(type: String) = Upgrade2Fragment(type)
    }

    @SuppressLint("SetTextI18n")
    override fun onGetSubscriptionSuccessful(productDetails: ProductDetails?) {
        Coroutines.main {
            val list = productDetails!!.subscriptionOfferDetails
            if (list != null) {
                binding.tvPrice1.text = "${list[0].pricingPhases.pricingPhaseList[1].formattedPrice} / ${getString(R.string.week)}"
                binding.tvPrice2.text = "${list[2].pricingPhases.pricingPhaseList[1].formattedPrice} / ${getString(R.string.year)}"

                this.productDetails = productDetails
                offerToken = list[0].offerToken

                binding.clWeek.setOnClickListener {
                    binding.clWeek.setBackgroundResource(R.drawable.bg_upgrade_active)
                    binding.clAnnual.setBackgroundResource(R.drawable.bg_upgrade_unactive)
                    this.productDetails = productDetails
                    offerToken = list[0].offerToken
                }

                binding.clAnnual.setOnClickListener {
                    binding.clWeek.setBackgroundResource(R.drawable.bg_upgrade_unactive)
                    binding.clAnnual.setBackgroundResource(R.drawable.bg_upgrade_active)
                    this.productDetails = productDetails
                    offerToken = list[2].offerToken
                }
            }
        }
    }

    override fun onGetProductDetailFailed() {
        Utils.toast(requireContext(), getString(R.string.error_try_again))
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun onNewSubscribe() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }

    override fun onAlreadySubscribed() {

    }

    override fun onHaveNotSubscribed() {

    }

    override fun onFreeTrialActive(remainingDays: Int) {

    }

    override fun onPurchasePending() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        billingManager.disconnect()
    }
}