package com.devom.app.ui.screens.signup.fragments

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devom.app.ui.components.DatePickerDialog
import com.devom.app.ui.components.DropDownItem
import com.devom.app.ui.components.ExposedDropdown
import com.devom.models.auth.UserRequestResponse
import devom_app.composeapp.generated.resources.Res
import com.devom.app.ui.components.TextInputField
import com.devom.models.other.City
import com.devom.models.other.Country
import com.devom.models.other.State
import com.devom.utils.date.convertIsoToDate
import com.devom.utils.date.toIsoDateTimeString
import com.devom.utils.date.toLocalDateTime
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import devom_app.composeapp.generated.resources.calendar_icon_description
import devom_app.composeapp.generated.resources.calendar_linear
import devom_app.composeapp.generated.resources.date_of_birth
import devom_app.composeapp.generated.resources.enter_address
import devom_app.composeapp.generated.resources.enter_city
import devom_app.composeapp.generated.resources.enter_country
import devom_app.composeapp.generated.resources.enter_email
import devom_app.composeapp.generated.resources.enter_full_name
import devom_app.composeapp.generated.resources.enter_phone
import devom_app.composeapp.generated.resources.enter_state

@Composable
internal fun UserDetailsScreenMainContent(
    userResponse: UserRequestResponse,
    onChange: (UserRequestResponse) -> Unit,
) {
    val viewModel = viewModel {
        UserDetailScreenViewModel()
    }
    val countryList = viewModel.countryList.collectAsState()
    val stateList = viewModel.stateList.collectAsState()
    val cityList = viewModel.cityList.collectAsState()

    val selectedCountry = viewModel.selectedCountry.collectAsState()
    val selectedCity = viewModel.selectedCity.collectAsState()
    val selectedState = viewModel.selectedState.collectAsState()

    val datePickerState = remember { mutableStateOf(false) }

    LaunchedEffect(userResponse) {
        viewModel.setInitialValues(userResponse)
    }

    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextInputField(
            initialValue = userResponse.fullName.toString(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            placeholder = stringResource(Res.string.enter_full_name)
        ) {
            userResponse.fullName = it
            onChange(userResponse)
        }
        TextInputField(
            initialValue = userResponse.email.toString(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            placeholder = stringResource(Res.string.enter_email)
        ) {
            userResponse.email = it
            onChange(userResponse)
        }
        TextInputField(
            initialValue = userResponse.mobileNo.toString(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            maxLength = 10,
            placeholder = stringResource(Res.string.enter_phone)
        ) {
            userResponse.mobileNo = it
            onChange(userResponse)
        }

        ExposedDropdown(
            isSearchEnabled = true,
            selectedOption = DropDownItem(option = selectedCountry.value?.name.orEmpty() , selectedCountry.value?.isoCode.orEmpty()),
            options = countryList.value.map { DropDownItem(it.name, it.isoCode) },
            placeholder = stringResource(Res.string.enter_country)
        ) {
            userResponse.country = it.option
            viewModel.selectedCountry.value = Country(name = it.option, isoCode = it.id)
            viewModel.getStateList(it.id)
            onChange(userResponse)
        }

        ExposedDropdown(
            isSearchEnabled = selectedCountry.value?.name?.isNotEmpty() == true ,
            enabled = selectedCountry.value?.name?.isNotEmpty() == true,
            disabledMessage = "Please Select Country First",
            selectedOption = DropDownItem(option = selectedState.value?.name.orEmpty() , selectedState.value?.isoCode.orEmpty()),
            options = stateList.value.map { DropDownItem(it.name, it.isoCode) },
            placeholder = stringResource(Res.string.enter_state)
        ) {
            userResponse.state = it.option
            viewModel.selectedState.value = State(name = it.option, isoCode = it.id)
            viewModel.getCityList(stateCode = it.id)
            onChange(userResponse)
        }

        ExposedDropdown(
            isSearchEnabled = selectedState.value?.name?.isNotEmpty() == true,
            enabled = selectedState.value?.name?.isNotEmpty() == true,
            disabledMessage = "Please Select State First",
            selectedOption = DropDownItem(option = selectedCity.value?.name.orEmpty() , selectedCity.value?.isoCode.orEmpty()),
            options = cityList.value.map { DropDownItem(it.name, it.isoCode) },
            placeholder = stringResource(Res.string.enter_city)
        ) {
            userResponse.city = it.option
            viewModel.selectedCity.value = City(name = it.option, isoCode = it.id)
            viewModel.getCityList(stateCode = it.id)
            onChange(userResponse)
        }

        TextInputField(
            initialValue = userResponse.address.toString(), keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
            ), placeholder = stringResource(Res.string.enter_address)
        ) {
            userResponse.address = it
            onChange(userResponse)
        }

        Box(
            modifier = Modifier.fillMaxWidth().clickable { datePickerState.value = true }) {

            val initialValue = if (userResponse.dateOfBirth.contains("T")) userResponse.dateOfBirth.convertIsoToDate()?.toLocalDateTime()?.date else userResponse.dateOfBirth

            TextInputField(
                readOnly = true,
                enabled = false,
                initialValue = initialValue?.toString() ?: "",
                placeholder = stringResource(Res.string.date_of_birth),
                trailingIcon = {
                    Image(
                        painter = painterResource(Res.drawable.calendar_linear),
                        contentDescription = stringResource(Res.string.calendar_icon_description),
                        modifier = Modifier.size(24.dp)
                    )
                }
            )
        }

        DatePickerDialog(
            maxDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            onDismiss = { datePickerState.value = false },
            onDateSelected = {
                datePickerState.value = false
                userResponse.dateOfBirth = it.toIsoDateTimeString()
                onChange(userResponse)
            },
            showPicker = datePickerState.value,
        )
    }
}
