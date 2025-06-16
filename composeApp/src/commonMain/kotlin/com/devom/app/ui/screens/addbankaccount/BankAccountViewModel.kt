package com.devom.app.ui.screens.addbankaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devom.Project
import com.devom.models.payment.UserBankDetails
import com.devom.utils.Application
import com.devom.utils.network.onResultNothing
import com.devom.utils.network.withSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BankAccountViewModel : ViewModel() {
    private val _bankAccount = MutableStateFlow(UserBankDetails())
    val bankAccount = _bankAccount.asStateFlow()

    init {
        getBankAccountDetails()
    }


    fun updateBankAccount(details: UserBankDetails) {
        viewModelScope.launch {
            Project.payment.addBankDetailsUseCase.invoke(details).collect {
                it.onResultNothing {
                    _bankAccount.value = details
                    Application.showToast("Bank account updated successfully")
                }
            }
        }
    }


    private fun getBankAccountDetails() {
        viewModelScope.launch {
            Project.payment.getBankDetailsUseCase.invoke().collect {
                it.withSuccess {
                    _bankAccount.value = it.data
                }
            }
        }
    }
}