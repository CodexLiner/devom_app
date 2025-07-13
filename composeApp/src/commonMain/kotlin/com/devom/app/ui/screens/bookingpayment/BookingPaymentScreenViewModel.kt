package com.devom.app.ui.screens.bookingpayment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devom.Project
import com.devom.models.payment.Notes
import com.devom.models.payment.RazorpayOrderRequest
import com.devom.models.payment.RazorpayOrderResponse
import com.devom.models.payment.VerifyTransactionRequest
import com.devom.models.payment.WalletBalance
import com.devom.models.slots.BookPanditSlotInput
import com.devom.network.getUser
import com.devom.utils.Application
import com.devom.utils.network.onResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.meenagopal24.sdk.models.PaymentData

class BookingPaymentScreenViewModel : ViewModel() {

    private val _walletBalance = MutableStateFlow<WalletBalance?>(null)
    val walletBalance = _walletBalance.asStateFlow()

    init {
        getWalletBalance()
    }

    fun bookPanditSlot(
        input: BookPanditSlotInput,
        selectedPaymentMode: String,
        poojaPrice: Float,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            if (selectedPaymentMode == "cash") {
                bookSlot(input, onSuccess)
                return@launch
            }

            if ((_walletBalance.value?.cashWallet?.toFloatOrNull() ?: 0f) >= poojaPrice) {
                viewModelScope.launch {
                    bookSlot(input, onSuccess)
                }
            } else {
                Application.showToast("Insufficient Balance")
            }
        }
    }

    private suspend fun bookSlot(input: BookPanditSlotInput, onSuccess: () -> Unit) {
        Project.pandit.bookPanditSlotUseCase.invoke(input).collect { result ->
            result.onResult {
                onSuccess()
                Application.showToast("Slot Booked Successfully")
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

    fun createTransaction(
        amount: Int,
        poojaName: String,
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
                        event = poojaName
                    ),
                    receipt = poojaName
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
                it.onResult {
                    function()
                }
            }
        }
    }
}
