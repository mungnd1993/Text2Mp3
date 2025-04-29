package com.texttomp3.texttospeech.billing

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsParams.Product
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.queryProductDetails
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList
import com.texttomp3.texttospeech.R
import com.texttomp3.texttospeech.utils.Constants
import com.texttomp3.texttospeech.utils.Constants.SUBSCRIPTION_PRODUCT_ID
import com.texttomp3.texttospeech.utils.Utils
import com.texttomp3.texttospeech.viewmodels.SettingViewModel

class GoogleBillingManager(
    private val context: Context,
    private val activity: Activity,
    private val listener: OnPurchaseStateChangeListener? = null,
    private val settingViewModel: SettingViewModel? = null
) : PurchasesUpdatedListener {
    private val params = PendingPurchasesParams.newBuilder().enableOneTimeProducts().enablePrepaidPlans().build()
    private var billingClient: BillingClient =
        BillingClient.newBuilder(context).enablePendingPurchases(params).setListener(this).build()

    fun disconnect() {
        billingClient.endConnection()
    }

    fun getProductDetail() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                ImmutableList.of(
                    Product.newBuilder()
                        .setProductId(SUBSCRIPTION_PRODUCT_ID)
                        .setProductType(ProductType.SUBS)
                        .build()
                )
            )
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                Utils.log("billing", "onBillingServiceDisconnected")
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                Utils.log("billing", "onBillingSetupFinished")
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Lấy thông tin gói tuần
                    if (billingClient.isReady) {
                        billingClient.queryProductDetailsAsync(params) { result, productDetailsList ->
                            Utils.log("billing", "$productDetailsList")
                            if (productDetailsList.isNotEmpty()) {
                                val productDetails = productDetailsList.first()

                                listener?.onGetSubscriptionSuccessful(
                                    productDetails
                                )
                            } else {
                                listener?.onGetProductDetailFailed()
                            }
                        }
                    }
                } else {
                    Utils.log("billing", "billingResult.responseCode = ${billingResult.responseCode}")
                }
            }
        })
    }

    /**
     * Kiểm tra xem đã mua hay chưa
     */
    fun getSubscriptionData(callback: ((Pair<String, String>) -> Unit)? = null) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {

            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    querySubscriptionPurchases(callback)
                }
            }
        })
    }

    /**
     * Kiểm tra lifetime trước, nếu không có thì kiểm tra subs
     */
    fun queryPurchases() {
        val lifetimeParams = QueryPurchasesParams.newBuilder()
            .setProductType(ProductType.INAPP)
            .build()
        billingClient.queryPurchasesAsync(
            lifetimeParams
        ) { billingResult: BillingResult?, list: List<Purchase> ->
            if (list.isNotEmpty()) {
                val purchase = list[0]
                Utils.log("billing", "inapp purchase time " + purchase.purchaseTime)
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
//                    AppPreferences.getInstance(context)?.setProVersion(true)
//                    AppPreferences.getInstance(context)?.setSubscriptionPeriod(PERIOD_LIFETIME)
                    listener?.onAlreadySubscribed()
                } else {
                    // Không có thông tin gói InApp thì lấy thông tin Subscription
                    querySubscriptionPurchases()
                }
            } else {
                // Không có thông tin gói InApp thì lấy thông tin Subscription
                querySubscriptionPurchases()
            }
        }
    }

    /**
     * Kiểm tra subs
     */
    private fun querySubscriptionPurchases(callback: ((Pair<String, String>) -> Unit)? = null) {
        val subscriptionParams = QueryPurchasesParams.newBuilder()
            .setProductType(ProductType.SUBS)
            .build()
        billingClient.queryPurchasesAsync(subscriptionParams) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases.isNotEmpty()) {
                val purchase = purchases[0] // Lấy giao dịch đầu tiên
                when (purchase.purchaseState) {
                    Purchase.PurchaseState.PURCHASED -> {
                        val productId = purchase.products[0]
                        if (callback != null) {
                            queryProductDetails(listOf(productId), callback)
                        }
                        Utils.log("duckaaa", "querySubscriptionPurchases set ProVersion = true")
                        settingViewModel?.setProVersion(true)
                        val remainingDays = getRemainingTrialDays(purchase)
                        if (remainingDays > 0) {
                            listener?.onFreeTrialActive(remainingDays)
                        } else {
                            listener?.onAlreadySubscribed()
                        }
                    }
                    else -> {
                        Utils.log("duckaaa", "querySubscriptionPurchases set ProVersion = false1")
                        settingViewModel?.setProVersion(false)
                        listener?.onHaveNotSubscribed()
                    }
                }
            } else {
                Utils.log("duckaaa", "querySubscriptionPurchases set ProVersion = false2")
                settingViewModel?.setProVersion(false)
                listener?.onHaveNotSubscribed()
            }
        }
    }

    private fun queryProductDetails(productIds: List<String>, callback: ((Pair<String, String>) -> Unit)? = null) {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                productIds.map {
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(it)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                }
            )
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (productDetails in productDetailsList) {
                    productDetails.subscriptionOfferDetails?.forEach {
                        Utils.log("billing", "subscriptionOfferDetails: = ${it.basePlanId}")
                    }
                    val offer = productDetails.subscriptionOfferDetails?.getOrNull(0)
                    val phase = offer?.pricingPhases?.pricingPhaseList?.getOrNull(0)

                    val price = phase?.formattedPrice
                    val billingPeriod = phase?.billingPeriod // Ví dụ: P1W, P1M, P1Y

                    val readablePeriod = when (billingPeriod) {
                        "P1M" -> context.getString(R.string.text_month)
                        "P1Y" -> context.getString(R.string.text_year)
                        else -> context.getString(R.string.text_unknown)
                    }
                    callback?.invoke(Pair(price.toString(), readablePeriod))
                    Utils.log("billing", "Gói: $readablePeriod, Giá: $price")
                }
            }
        }
    }

    private fun getRemainingTrialDays(purchase: Purchase): Int {
        val purchaseTimeMillis = purchase.purchaseTime
        val trialDurationMillis = 3L * 24 * 60 * 60 * 1000 // 3 ngày
        val currentTimeMillis = System.currentTimeMillis()
        val elapsedTimeMillis = currentTimeMillis - purchaseTimeMillis
        val remainingMillis = trialDurationMillis - elapsedTimeMillis
        return if (remainingMillis > 0) (remainingMillis / (24 * 60 * 60 * 1000)).toInt() else 0
    }

    /**
     * Nâng cấp lên phiên bản Pro
     *
     * @param productDetails gói sản phẩm đã chọn: one time purchase, subscription
     * @param offerToken     offerToken của subscription, nếu là one time purchase thì offerToken = null
     * @param offerPeriod    chu kì: tháng/năm/trọn đời
     */
    fun upgradeToProVersion(
        productDetails: ProductDetails,
        offerToken: String?,
    ) {
        try {
            val productDetailsParamsList: ImmutableList<ProductDetailsParams> =
                if (offerToken != null) {
                    Utils.log("billing", offerToken)
                    ImmutableList.of(
                        ProductDetailsParams.newBuilder() // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                            .setProductDetails(productDetails) // For One-time product, `setOfferToken` method should not be called.
                            // For subscriptions, to get an offer token, call ProductDetails.subscriptionOfferDetails()
                            // for a list of offers that are available to the user
                            .setOfferToken(offerToken)
                            .build()
                    )
                } else {
                    ImmutableList.of(
                        ProductDetailsParams.newBuilder() // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                            .setProductDetails(productDetails) // For One-time product, `setOfferToken` method should not be called.
                            // For subscriptions, to get an offer token, call ProductDetails.subscriptionOfferDetails()
                            // for a list of offers that are available to the user
                            .build()
                    )
                }
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()

            // Launch the billing flow
            val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)

        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.error_try_again), Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun upgradeToProVersion(productDetails: ProductDetails) {
        try {
            val offerToken =
                productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: ""
            val productDetailsParamsList = ImmutableList.of(
                ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .setOfferToken(offerToken)
                    .build()
            )
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()

            billingClient.launchBillingFlow(activity, billingFlowParams)
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.error_try_again), Toast.LENGTH_SHORT)
                .show()
        }
    }

    /**
     * Kết quả mua hàng trả về, tương tự billingResult ở hàm trên
     */
    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        Utils.log("billing", "Đang trả kết quả về billingResult.responseCode = ${billingResult.responseCode}")
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            val purchase = purchases[0]
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                Utils.log("billing", "Đang trả kết quả về 1")
                settingViewModel?.setProVersion(true)
                listener?.onFreeTrialActive(2)
                if (!purchase.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams) { result ->
//                        if (result.responseCode == BillingClient.BillingResponseCode.OK) {
//                            settingViewModel?.setProVersion(true)
//                            proSub?.let { settingViewModel?.saveProSub(it) }
//                            listener?.onFreeTrialActive(getRemainingTrialDays(purchase))
//                        }
                    }
                } else {
//                    LogHelper.log("billing", "Đang trả kết quả về 2")
//                    val remainingDays = getRemainingTrialDays(purchase)
//                    settingViewModel?.setProVersion(true)
//                    proSub?.let { settingViewModel?.saveProSub(it) }
//                    if (remainingDays > 0) {
//                        listener?.onFreeTrialActive(remainingDays)
//                    } else {
//                        listener?.onAlreadySubscribed()
//                    }
                }
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Utils.log("billing", "Đang trả kết quả về 3")
            settingViewModel?.setProVersion(true)
            listener?.onAlreadySubscribed()
//            Toast.makeText(
//                context,
//                context.getString(R.string.already_subscribed),
//                Toast.LENGTH_SHORT
//            ).show()
        } else {
            Utils.log("billing", "Đang trả kết quả về 4")
            listener?.onNewSubscribe()
        }
    }

    interface OnPurchaseStateChangeListener {
        fun onGetSubscriptionSuccessful(productDetails: ProductDetails?)
        fun onGetProductDetailFailed()
        fun onNewSubscribe()
        fun onAlreadySubscribed()
        fun onHaveNotSubscribed()
        fun onFreeTrialActive(remainingDays: Int)
        fun onPurchasePending()
    }

}