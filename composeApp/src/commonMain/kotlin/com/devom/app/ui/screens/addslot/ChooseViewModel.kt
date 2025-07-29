package com.devom.app.ui.screens.addslot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devom.Project
import com.devom.models.slots.Slot
import com.devom.utils.network.onResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ChooseViewModel : ViewModel() {


    private val _slots = MutableStateFlow(listOf<Slot>())
    val slots = _slots

    fun getAvailableSlots(user : String) {
        viewModelScope.launch {
            Project.pandit.getAvailableSlotsUseCase.invoke(user).collect {
                it.onResult {
                    this@ChooseViewModel.viewModelScope.launch {
                        _slots.emit(
                            it.data.map {
                                Slot(
                                    availableDate = it.availableDate,
                                    startTime = it.startTime,
                                    endTime = it.endTime,
                                    id = it.id,
                                    panditId = it.panditId,
                                    isBooked = it.isBooked,
                                    createdAt = it.createdAt,
                                    updatedAt = it.updatedAt
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}