package com.devom.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devom.Project
import com.devom.app.models.ApplicationStatus
import com.devom.models.other.BannersResponse
import com.devom.models.payment.GetWalletTransactionsResponse
import com.devom.models.pooja.GetPoojaResponse
import com.devom.models.slots.GetBookingsResponse
import com.devom.models.slots.UpdateBookingStatusInput
import com.devom.utils.cachepolicy.CachePolicy
import com.devom.utils.network.onResult
import com.devom.utils.network.withSuccessWithoutData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeScreenViewModel : ViewModel() {

    private val _poojaList = MutableStateFlow<List<GetPoojaResponse>>(listOf())
    val poojaList = _poojaList.asStateFlow()

    private val _banners = MutableStateFlow<List<BannersResponse>>(listOf())
    val banners = _banners.asStateFlow()

    init {
        getPoojaList()
        getBanners()
    }

    fun getPoojaList() {
        viewModelScope.launch {
            Project.pooja.getPoojaUseCase.invoke().collect {
                it.onResult {
                    _poojaList.value = it.data
                }
            }
        }
    }

    fun getBanners() {
        viewModelScope.launch {
            Project.other.getBannersUseCase.invoke().collect {
                it.onResult {
                    _banners.value = it.data
                }
            }
        }
    }
}