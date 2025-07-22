package com.devom.app.ui.screens.signup.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devom.Project
import com.devom.models.auth.UserRequestResponse
import com.devom.models.other.City
import com.devom.models.other.Country
import com.devom.models.other.State
import com.devom.utils.network.onResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserDetailScreenViewModel : ViewModel() {
    private val _countryList = MutableStateFlow<List<Country>>(emptyList())
    val countryList = _countryList.asStateFlow()

    private val _stateList = MutableStateFlow<List<State>>(emptyList())
    val stateList = _stateList.asStateFlow()

    private val _cityList = MutableStateFlow<List<City>>(emptyList())
    val cityList = _cityList.asStateFlow()

    val selectedCountry = MutableStateFlow<Country?>(null)
    val selectedState = MutableStateFlow<State?>(null)
    val selectedCity = MutableStateFlow<City?>(null)


    init {
        getCountryList()
        getStateList()
    }

    fun setInitialValues(userResponse: UserRequestResponse) {
        viewModelScope.launch {
            selectedCountry.value = countryList.value.find { it.name == userResponse.country } ?: Country(userResponse.country)
            getStateList(selectedCountry.value?.isoCode.orEmpty())
            selectedState.value = stateList.value.find { it.name == userResponse.state } ?: State(userResponse.state)
            getCityList(selectedCountry.value?.isoCode.orEmpty(), selectedState.value?.isoCode.orEmpty()) // suspend until complete
            selectedCity.value = cityList.value.find { it.name == userResponse.city } ?: City(userResponse.city)
        }
    }

    private fun getCountryList() {
        viewModelScope.launch {
            Project.other.getAllCountriesUseCase.invoke().collect {
                it.onResult {
                    _countryList.value = it.data.filter { it.isoCode == "IN" }
                }
            }
        }
    }

    fun getStateList(countryCode: String = "IN") {
        viewModelScope.launch {
            if (countryCode.isNotEmpty()) Project.other.getAllStatesUseCase.invoke(countryCode)
                .collect {
                    it.onResult {
                        _stateList.value = it.data
                    }
                }
        }
    }

    fun getCityList(countryCode: String = "IN", stateCode: String) {
        viewModelScope.launch {
            Project.other.getAllCitiesUseCase.invoke(
                countryCode.ifBlank { "IN" },
                stateCode.ifBlank { "MP" }
            ).collect {
                it.onResult {
                    _cityList.value = it.data
                }
            }
        }
    }
}