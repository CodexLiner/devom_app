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
import com.devom.app.settings
import com.devom.network.NetworkClient
import com.devom.network.USER
import com.devom.utils.Application.showToast

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
                    Application.isLoggedIn(true)
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