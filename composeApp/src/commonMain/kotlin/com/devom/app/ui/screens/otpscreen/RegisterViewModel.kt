package com.devom.app.ui.screens.otpscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devom.Project
import com.devom.models.auth.LoginWithOtpRequest
import com.devom.utils.Application
import com.devom.utils.network.ResponseResult
import com.devom.utils.network.onResult
import com.russhwolf.settings.set
import kotlinx.coroutines.launch
import com.devom.app.ACCESS_TOKEN_KEY
import com.devom.app.REFRESH_TOKEN_KEY
import com.devom.app.UUID_KEY
import com.devom.app.firebase.MyFirebaseMessagingService
import com.devom.app.settings
import com.devom.models.auth.SaveUserDeviceTokenRequest
import com.devom.network.NetworkClient
import com.devom.network.USER
import com.devom.utils.Application.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

class RegisterViewModel : ViewModel() {

    /**
     * validates otp
     */
    fun verifyOtp(otp: String, mobileNumber: String) {
        viewModelScope.launch {
            Project.user.loginWithOtpUseCase.invoke(
                LoginWithOtpRequest(mobileNo = mobileNumber, otp = otp)
            ).collect { result ->
                result.onResult {
                    settings[ACCESS_TOKEN_KEY] = (result as ResponseResult.Success).data.accessToken
                    settings[REFRESH_TOKEN_KEY] = result.data.refreshToken
                    settings[UUID_KEY] = result.data.uuid
                    settings[USER] = NetworkClient.config.jsonConfig.encodeToString(result.data)
                    MyFirebaseMessagingService.getToken { token, device ->
                        saveDeviceToken(token, device)
                    }
                    Application.isLoggedIn(true)
                }
            }
        }
    }

    fun saveDeviceToken(token: String = "", device: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Project.user.saveDeviceTokenUseCase.invoke(
                SaveUserDeviceTokenRequest(
                    deviceToken = token,
                    deviceType = device
                )
            ).collect { result ->
                result.onResult {
                    showToast("device token saved successfully")
                }
            }
        }
    }

    fun resendOtp(mobileNumber: String) {
        viewModelScope.launch {
            Project.user.generateOtpUseCase.invoke(mobileNumber).collect { result ->
                result.onResult {
                    showToast("otp sent successfully ${it.data.otp}")
                }
            }
        }
    }
}