package com.devom.app.ui.screens.addbalance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devom.Project
import com.devom.models.payment.AddWalletBalanceRequest
import com.devom.models.payment.WalletBalance
import com.devom.network.getUser
import com.devom.utils.Application
import com.devom.utils.network.onResult
import com.devom.utils.network.onResultNothing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BankWalletViewModel : ViewModel() {

    private val _walletBalance = MutableStateFlow<WalletBalance?>(null)
    val walletBalance = _walletBalance.asStateFlow()
    init {
        getWalletBalance()
    }

    fun addWalletBalance(amount : Int) {
        viewModelScope.launch {
            Project.payment.addWalletBalanceUseCase.invoke(
                request = AddWalletBalanceRequest(
                    amount = amount.toDouble(),
                    walletId = getUser().userId.toString(),
                    type = "cash_wallet"
                )
            ).collect {
                it.onResultNothing {
                    Application.showToast("Balance added successfully")
                    getWalletBalance()
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