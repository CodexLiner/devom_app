package com.devom.app.ui.screens.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devom.Project
import com.devom.models.auth.UserRequestResponse
import com.devom.models.notification.GetNotificationResponse
import com.devom.utils.network.onResult
import com.devom.utils.network.withSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {

    private val _notifications = MutableStateFlow<List<GetNotificationResponse>>(emptyList())
    val notifications = _notifications

    init {
        getNotifications()

    }

    fun getNotifications() {
        viewModelScope.launch {
            Project.notification.getNotificationsUseCase.invoke().collect {
                it.onResult {
                    _notifications.value = it.data
                }
            }
        }
    }
}