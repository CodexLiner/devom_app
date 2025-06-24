package com.devom.app.ui.screens.panditlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devom.Project
import com.devom.models.slots.GetAllPanditByPoojaIdResponse
import com.devom.utils.network.onResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PanditListScreenViewModel : ViewModel() {

    private val _allPanditList = MutableStateFlow<List<GetAllPanditByPoojaIdResponse>>(emptyList())
    val allPanditList = _allPanditList.asStateFlow()

    fun getAllPanditByPoojaId(poojaId : Int) {
        viewModelScope.launch {
            Project.pandit.getAllPanditByPoojaId.invoke(poojaId).collect {
                it.onResult {
                    _allPanditList.value = it.data
                }
            }
        }
    }
}
