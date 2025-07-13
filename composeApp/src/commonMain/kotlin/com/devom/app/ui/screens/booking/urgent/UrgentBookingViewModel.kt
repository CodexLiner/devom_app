package com.devom.app.ui.screens.booking.urgent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devom.Project
import com.devom.models.pooja.GetPoojaResponse
import com.devom.utils.network.onResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UrgentBookingViewModel : ViewModel(){
    private val _poojaList = MutableStateFlow<List<GetPoojaResponse>>(listOf())
    val poojaList = _poojaList.asStateFlow()
    init {
        getPoojaList()
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
}