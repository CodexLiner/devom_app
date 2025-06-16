package com.devom.app.ui.screens.rituals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devom.Project
import com.devom.models.auth.UserRequestResponse
import com.devom.models.pandit.GetPanditPoojaResponse
import com.devom.models.pandit.MapPanditPoojaItemInput
import com.devom.models.pooja.GetPoojaResponse
import com.devom.utils.Application
import com.devom.utils.network.onResult
import com.devom.utils.network.onResultNothing
import com.devom.utils.network.withSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RitualsViewModel : ViewModel() {
    private val _getPoojaItems = MutableStateFlow<List<GetPoojaResponse>>(listOf())
    val getPoojaItems = _getPoojaItems



    private val _rituals = MutableStateFlow<List<GetPanditPoojaResponse>?>(null)
    val rituals = _rituals

    fun getRituals() {
        viewModelScope.launch {
            Project.pandit.getPanditPoojaUseCase.invoke().collect {
                it.onResult {
                    _rituals.value = it.data
                }
            }
        }
    }

    fun getPoojaList() {
        viewModelScope.launch {
            Project.pooja.getPoojaUseCase.invoke().collect {
                it.onResult {
                    _getPoojaItems.value = it.data
                }
            }
        }
    }

    fun deletePoojaItem(input: GetPanditPoojaResponse) {
        viewModelScope.launch {
            Project.pandit.removePanditPoojaMappingUseCase.invoke(
                MapPanditPoojaItemInput(
                    panditId = input.panditId,
                    poojaId = input.poojaId
                )
            ).collect {
                it.onResultNothing {
                    getRituals()
                    Application.showToast("Deleted Successfully")
                }
            }
        }
    }

    fun mapPoojaItem(input: MapPanditPoojaItemInput) {
        viewModelScope.launch {
            Project.pandit.mapPanditPoojaItemUseCase.invoke(input).collect {
                it.onResultNothing {
                    getRituals()
                    Application.showToast("Pooja Item Mapped Successfully")
                }
            }
        }
    }
}
