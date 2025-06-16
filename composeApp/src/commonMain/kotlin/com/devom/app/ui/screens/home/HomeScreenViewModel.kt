package com.devom.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devom.Project
import com.devom.app.models.ApplicationStatus
import com.devom.models.payment.GetWalletTransactionsResponse
import com.devom.models.slots.GetBookingsResponse
import com.devom.models.slots.UpdateBookingStatusInput
import com.devom.utils.cachepolicy.CachePolicy
import com.devom.utils.network.onResult
import com.devom.utils.network.withSuccessWithoutData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeScreenViewModel : ViewModel() {

    private val _bookings = MutableStateFlow<List<GetBookingsResponse>>(listOf())
    val bookings: StateFlow<List<GetBookingsResponse>> = _bookings


    private val _transactions = MutableStateFlow(GetWalletTransactionsResponse())
    val transactions = _transactions

    init {
        getTransactions()
    }

    fun getBookings() {
        viewModelScope.launch {
            Project.pandit.getPanditBookingsUseCase.invoke(cachePolicy = CachePolicy.CacheAndNetwork)
                .collect {
                    it.onResult {
                        _bookings.value = it.data
                    }
                }
        }
    }


    fun getTransactions() {
        viewModelScope.launch {
            Project.payment.getWalletTransactionsUseCase.invoke().collect {
                it.onResult {
                    _transactions.value = it.data
                }
            }
        }
    }

    fun updateBookingStatus(id: Int, applicationStatus: ApplicationStatus) {
        viewModelScope.launch {
            Project.pandit.updateBookingStatusUseCase.invoke(
                UpdateBookingStatusInput(
                    id = id,
                    status = applicationStatus.status
                )
            ).collect {
                it.withSuccessWithoutData {
                    getBookings()
                }
                it.onResult {
                    getBookings()
                }
            }
        }
    }
}