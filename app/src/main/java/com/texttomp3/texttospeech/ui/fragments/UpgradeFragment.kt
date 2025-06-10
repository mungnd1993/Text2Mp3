package com.texttomp3.texttospeech.ui.fragments

import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import com.android.billingclient.api.ProductDetails
import com.texttomp3.texttospeech.R
import com.texttomp3.texttospeech.base.BaseFragment
import com.texttomp3.texttospeech.billing.GoogleBillingManager
import com.texttomp3.texttospeech.databinding.FragmentUpgradeBinding
import com.texttomp3.texttospeech.helpers.PreferenceHelper
import com.texttomp3.texttospeech.ui.activities.MainActivity
import com.texttomp3.texttospeech.utils.Constants.IS_FIRST_OPEN_APP
import com.texttomp3.texttospeech.utils.Constants.LINK_PRIVACY
import com.texttomp3.texttospeech.utils.Constants.LINK_TERM
import com.texttomp3.texttospeech.utils.Coroutines
import com.texttomp3.texttospeech.viewmodels.SettingViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import androidx.core.net.toUri
import com.texttomp3.texttospeech.utils.Constants.CANCEL_PLAN
import com.texttomp3.texttospeech.utils.Utils

class UpgradeFragment : BaseFragment<FragmentUpgradeBinding>(), GoogleBillingManager.OnPurchaseStateChangeListener {
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
    ): FragmentUpgradeBinding {
        return FragmentUpgradeBinding.inflate(inflater, container, boolean)
    }

    override fun initMain() {
        initView()
        initEvent()
    }

    private fun initView() {
        billingManager = GoogleBillingManager(requireContext(), requireActivity(), this, settingViewModel)
        billingManager.getProductDetail()

//        val textDiscount = getString(R.string.original)
//        val spannableString = SpannableString(textDiscount)
//        spannableString.setSpan(StrikethroughSpan(), 0, textDiscount.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val textPolicy = getString(R.string.policy)
        val spannableString2 = SpannableString(textPolicy)
        spannableString2.setSpan(UnderlineSpan(), 0, textPolicy.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val textTerm = getString(R.string.terms_of_use)
        val spannableString3 = SpannableString(textTerm)
        spannableString3.setSpan(UnderlineSpan(), 0, textTerm.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        with(binding) {
//            tvSub2.text = spannableString
            tvPolicy.text = spannableString2
            tvTerm.text = spannableString3
        }

        PreferenceHelper.getInstance(requireContext()).putBoolean(IS_FIRST_OPEN_APP, false)
    }

    private fun initEvent() {
        with(binding) {
            ivClose.setOnClickListener {
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
            }

            tvPolicy.setOnClickListener {
                startActivity(
                    Intent(Intent.ACTION_VIEW, LINK_PRIVACY.toUri())
                )
            }

            tvTerm.setOnClickListener {
                startActivity(
                    Intent(Intent.ACTION_VIEW, LINK_TERM.toUri())
                )
            }

            btContinue.setOnClickListener {
                if (productDetails != null) {
                    billingManager.upgradeToProVersion(productDetails!!, offerToken)
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = UpgradeFragment()
    }

    override fun onGetSubscriptionSuccessful(productDetails: ProductDetails?) {
        Coroutines.main {
            val list = productDetails!!.subscriptionOfferDetails
            if (list != null) {
                if (list.size == 4) {
                    binding.btContinue.text = getString(R.string.try_for_free)
                    binding.tvSub.text = getString(R.string.three_days_free_trial)
                    binding.tvSub2.text = getString(R.string.three_days_free_trial)

                    binding.tvPrice1.text = list[0].pricingPhases.pricingPhaseList[1].formattedPrice
                    binding.tvPrice2.text = list[2].pricingPhases.pricingPhaseList[1].formattedPrice

                    this.productDetails = productDetails
                    offerToken = list[0].offerToken

                    binding.clWeek.setOnClickListener {
                        binding.clWeek.setBackgroundResource(R.drawable.bg_upgrade_active)
                        binding.clAnnu.setBackgroundResource(R.drawable.bg_upgrade_unactive)
                        this.productDetails = productDetails
                        offerToken = list[0].offerToken
                    }

                    binding.clAnnu.setOnClickListener {
                        binding.clWeek.setBackgroundResource(R.drawable.bg_upgrade_unactive)
                        binding.clAnnu.setBackgroundResource(R.drawable.bg_upgrade_active)
                        this.productDetails = productDetails
                        offerToken = list[2].offerToken
                    }
                }
                else if (list.size == 2) {
                    binding.tvPrice1.text = list[0].pricingPhases.pricingPhaseList[0].formattedPrice
                    binding.tvPrice2.text = list[1].pricingPhases.pricingPhaseList[0].formattedPrice

                    this.productDetails = productDetails
                    offerToken = list[0].offerToken

                    binding.clWeek.setOnClickListener {
                        binding.clWeek.setBackgroundResource(R.drawable.bg_upgrade_active)
                        binding.clAnnu.setBackgroundResource(R.drawable.bg_upgrade_unactive)
                        this.productDetails = productDetails
                        offerToken = list[0].offerToken
                    }

                    binding.clAnnu.setOnClickListener {
                        binding.clWeek.setBackgroundResource(R.drawable.bg_upgrade_unactive)
                        binding.clAnnu.setBackgroundResource(R.drawable.bg_upgrade_active)
                        this.productDetails = productDetails
                        offerToken = list[1].offerToken
                    }
                }
            }
        }
    }

    override fun onGetProductDetailFailed() {
        requireActivity().supportFragmentManager.popBackStack()
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }

    override fun onNewSubscribe() {

    }

    override fun onAlreadySubscribed() {

    }

    override fun onHaveNotSubscribed() {

    }

    override fun onFreeTrialActive(remainingDays: Int) {
        PreferenceHelper.getInstance(requireContext()).putBoolean(CANCEL_PLAN, false)
        requireActivity().supportFragmentManager.popBackStack()
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }

    override fun onPurchasePending() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        billingManager.disconnect()
    }
}