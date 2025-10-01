package org.app.carlos.billing

import android.app.Activity
import android.content.Context
import android.os.Looper
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.app.carlos.data.model.Theme
import org.app.carlos.data.repository.BillingRepository
import org.app.carlos.data.repository.PurchaseResult
import org.app.carlos.data.repository.ThemeRepository
import java.util.logging.Handler

class AndroidBillingRepository(
    private val context: Context,
    private val themeRepository: ThemeRepository
) : BillingRepository {

    private var purchaseContinuation: CancellableContinuation<PurchaseResult>? = null
    private var isBillingReady = false
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val billingClient = BillingClient.newBuilder(context)
        .setListener { billingResult, purchases ->
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    if (purchases != null) handlePurchases(purchases)
                }
                BillingClient.BillingResponseCode.USER_CANCELED -> {
                    purchaseContinuation?.resume(PurchaseResult.Failure) {}
                    purchaseContinuation = null
                }
                else -> {
                    purchaseContinuation?.resume(
                        PurchaseResult.Error("Billing error: ${billingResult.debugMessage}")
                    ) {}
                    purchaseContinuation = null
                }
            }
        }
        .enablePendingPurchases()
        .build()

    init {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                isBillingReady = false
                Log.w("Billing", "Service Google Play Billing disconnected ⚠️")
                android.os.Handler(Looper.getMainLooper()).postDelayed({
                    billingClient.startConnection(this)
                }, 2000)
            }

            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    isBillingReady = true
                    Log.d("Billing", "BillingClient ready ✅")
                    restorePurchasesSilently()
                } else {
                    isBillingReady = false
                    Log.e("Billing", "Connection error: ${result.debugMessage}")
                }
            }
        })
    }

    override suspend fun getThemes(): List<Theme> {
        return themeRepository.getAllThemes()
    }

    override suspend fun purchaseTheme(themeId: String): PurchaseResult =
        suspendCancellableCoroutine { continuation ->
            if (!isBillingReady) {
                Log.d("Billing", "purchaseTheme called for $themeId, isBillingReady=$isBillingReady")
                continuation.resume(PurchaseResult.Error("Billing not ready")) {}
                return@suspendCancellableCoroutine
            }

            scope.launch {
                val productDetails = queryProduct(themeId)
                if (productDetails == null) {
                    Log.e("Billing", "Product not found: $themeId")
                    continuation.resume(PurchaseResult.Error("Product not found")) {}
                    return@launch
                }

                val flowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(
                        listOf(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .build()
                        )
                    ).build()

                val activity = context as? Activity
                if (activity == null) {
                    continuation.resume(PurchaseResult.Error("No activity context")) {}
                    return@launch
                }

                purchaseContinuation = continuation
                billingClient.launchBillingFlow(activity, flowParams)
            }
        }

    override suspend fun restorePurchases(): PurchaseResult =
        suspendCancellableCoroutine { continuation ->
            if (!isBillingReady) {
                continuation.resume(PurchaseResult.Error("Billing not ready")) {}
                return@suspendCancellableCoroutine
            }

            billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            ) { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases.isNotEmpty()) {
                    handlePurchases(purchases)
                    continuation.resume(PurchaseResult.Success) {}
                } else {
                    continuation.resume(PurchaseResult.Failure) {}
                }
            }
        }

    private suspend fun queryProduct(productId: String): ProductDetails? {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
            )
            .build()

        return billingClient.queryProductDetails(params).productDetailsList?.firstOrNull()
    }

    private fun handlePurchases(purchases: List<Purchase>) {
        purchases.forEach { purchase ->
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (!purchase.isAcknowledged) {
                    val params = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    billingClient.acknowledgePurchase(params) {}
                }

                val themeId = purchase.products.firstOrNull()
                if (themeId != null) {
                    scope.launch {
                        themeRepository.markThemePurchased(themeId)
                        themeRepository.setCurrentTheme(themeId)
                    }
                }

                purchaseContinuation?.resume(PurchaseResult.Success) {}
                purchaseContinuation = null
            }
        }
    }

    private fun restorePurchasesSilently() {
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        ) { _, purchases ->
            if (purchases.isNotEmpty()) {
                handlePurchases(purchases)
            }
        }
    }
}