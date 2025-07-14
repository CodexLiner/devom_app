package com.devom.app.ui.screens.addbalance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devom.Project
import com.devom.models.payment.Notes
import com.devom.models.payment.RazorpayOrderRequest
import com.devom.models.payment.RazorpayOrderResponse
import com.devom.models.payment.VerifyTransactionRequest
import com.devom.models.payment.WalletBalance
import com.devom.network.getUser
import com.devom.utils.network.onResult
import com.devom.utils.network.onResultNothing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.meenagopal24.sdk.models.PaymentData

class BankWalletViewModel : ViewModel() {

    private val _walletBalance = MutableStateFlow<WalletBalance?>(null)
    val walletBalance = _walletBalance.asStateFlow()
    init {
        getWalletBalance()
    }

    fun createTransaction(
        amount: Int,
        purpose: String,
        function: (RazorpayOrderResponse) -> Unit,
    ) {
        viewModelScope.launch {
            Project.payment.createOrderUseCase.invoke(
                RazorpayOrderRequest(
                    amount = amount,
                    currency = "INR",
                    notes = Notes(
                        customer_name = getUser().fullName,
                        user_id = getUser().userId.toString(),
                        event = purpose
                    ),
                    receipt = purpose
                )
            ).collect { it ->
                it.onResult {
                    function(it.data)
                }
            }
        }
    }

    fun verifyTransaction(data: PaymentData?, function: () -> Unit) {
        viewModelScope.launch {
            Project.payment.verifyTransactionUseCase.invoke(
                VerifyTransactionRequest(
                    razorpayPaymentId = data?.paymentId.orEmpty(),
                    razorpayOrderId = data?.orderId.orEmpty(),
                    razorpaySignature = data?.signature.orEmpty()
                )
            ).collect {
                it.onResultNothing {
                    function()
                }
            }
        }
    }

    fun getWalletBalance() {
        viewModelScope.launch {
            Project.payment.getWalletBalanceUseCase.invoke().collect {
                it.onResult {
                    _walletBalance.value = it.data.balance
                }
            }
        }
    }
}