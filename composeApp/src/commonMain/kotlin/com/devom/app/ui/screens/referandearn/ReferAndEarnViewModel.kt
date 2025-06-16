package com.devom.app.ui.screens.referandearn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devom.Project
import com.devom.models.auth.UserRequestResponse
import com.devom.utils.Contact
import com.devom.utils.getContacts
import com.devom.utils.network.withSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ReferAndEarnViewModel : ViewModel() {
    private val _user = MutableStateFlow<UserRequestResponse?>(null)
    val user = _user

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts = _contacts

    init {
        getUserProfile()
    }

    fun getUserProfile() {
        viewModelScope.launch {
            Project.user.getUserProfileUseCase.invoke().collect {
                it.withSuccess {
                    _user.value = it.data
                }
            }
        }
    }

    fun getContactList() {
        viewModelScope.launch {
            _contacts.value = getContacts()
        }
    }
}