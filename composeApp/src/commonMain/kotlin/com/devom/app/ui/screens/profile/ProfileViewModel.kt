package com.devom.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devom.Project
import com.devom.app.settings
import com.devom.models.auth.UserRequestResponse
import com.devom.network.NetworkClient
import com.devom.network.USER
import com.devom.utils.Application
import com.devom.utils.date.convertIsoToDate
import com.devom.utils.date.toLocalDateTime
import com.devom.utils.network.onResult
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val _user = MutableStateFlow(UserRequestResponse())
    val user = _user

    init {
        getUserProfile()
    }

    fun getUserProfile() {
        viewModelScope.launch {
            Project.user.getUserProfileUseCase.invoke().collect {
                it.onResult {
                    _user.value = it.data
                }
            }
        }
    }

    fun updateUserProfile(userRequestResponse: UserRequestResponse, image : ByteArray? = null, message : String =  "Profile updated successfully") {
        viewModelScope.launch {
            Project.user.updateUserProfileUseCase.invoke(
                userRequestResponse.copy(
                    dateOfBirth = if (userRequestResponse.dateOfBirth.contains("T")) userRequestResponse.dateOfBirth.convertIsoToDate()
                        ?.toLocalDateTime()?.date.toString() else userRequestResponse.dateOfBirth
                ),
                image
            ).collect {
                it.onResult {
                    _user.value = it.data
                    settings[USER] = NetworkClient.config.jsonConfig.encodeToString(it.data)
                    Application.showToast(message)
                }
            }
        }
    }

    fun setUserResponse(userRequestResponse: UserRequestResponse) {
        viewModelScope.launch {
            _user.emit(userRequestResponse.copy())
        }
    }
}