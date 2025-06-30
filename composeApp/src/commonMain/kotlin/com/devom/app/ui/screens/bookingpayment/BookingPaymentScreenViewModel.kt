package com.devom.app.ui.screens.bookingpayment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devom.Project
import com.devom.models.slots.BookPanditSlotInput
import com.devom.utils.Application
import com.devom.utils.network.onResult
import kotlinx.coroutines.launch

class BookingPaymentScreenViewModel : ViewModel() {

    fun bookPanditSlot(input: BookPanditSlotInput, selectedPaymentMode: String, poojaPrice: Float , onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (selectedPaymentMode == "case") {
                bookSlot(input , onSuccess)
                return@launch
            }

            Project.payment.getWalletBalanceUseCase.invoke().collect { result ->
                result.onResult { wallet ->
                    val balance = wallet.data.balance.cashWallet.toFloatOrNull() ?: 0f
                    if (balance > poojaPrice) {
                        viewModelScope.launch {
                            bookSlot(input , onSuccess)
                        }
                    } else {
                        Application.showToast("Insufficient Balance")
                    }
                }
            }
        }
    }

    private suspend fun bookSlot(input: BookPanditSlotInput , onSuccess: () -> Unit) {
        Project.pandit.bookPanditSlotUseCase.invoke(input).collect { result ->
            result.onResult {
                onSuccess()
                Application.showToast("Slot Booked Successfully")
            }
        }
    }
}
