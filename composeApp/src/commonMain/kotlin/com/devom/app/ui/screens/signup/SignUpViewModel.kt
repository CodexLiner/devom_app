package com.devom.app.ui.screens.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devom.Project
import com.devom.models.auth.UserRequestResponse
import com.devom.utils.date.convertIsoToDate
import com.devom.utils.date.toLocalDateTime
import com.devom.utils.network.onResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {
    private val _signUpState = MutableStateFlow<Boolean?>(null)
    val signUpState: StateFlow<Boolean?> = _signUpState

    fun signUp(user: UserRequestResponse) {
        viewModelScope.launch {
            Project.user.registerUserUseCase.invoke(
                user.apply {
                    userTypeId = 2
                    dateOfBirth = if (dateOfBirth.contains("T")) dateOfBirth.convertIsoToDate()
                        ?.toLocalDateTime()?.date.toString() else dateOfBirth
                }).collect {
                it.onResult {
                    _signUpState.value = true
                }
            }
        }
    }
}