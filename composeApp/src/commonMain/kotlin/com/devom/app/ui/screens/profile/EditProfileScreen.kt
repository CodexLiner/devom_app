package com.devom.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import co.touchlab.kermit.Logger
import com.devom.app.models.SupportedFiles
import com.devom.app.theme.text_style_lead_text
import com.devom.app.ui.components.AppBar
import com.devom.app.ui.components.ButtonPrimary
import com.devom.app.ui.components.DatePickerDialog
import com.devom.app.ui.components.FilePickerBottomSheetHost
import com.devom.app.ui.components.UserProfilePicture
import com.devom.app.ui.screens.signup.fragments.UserDetailsScreenMainContent
import com.devom.models.auth.UserRequestResponse
import com.devom.utils.Application
import com.devom.utils.date.convertIsoToDate
import com.devom.utils.date.toIsoDateTimeString
import com.devom.utils.date.toLocalDateTime
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.source
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.io.buffered
import kotlinx.io.readByteArray
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pandijtapp.composeapp.generated.resources.Res
import pandijtapp.composeapp.generated.resources.all_field_required
import pandijtapp.composeapp.generated.resources.ic_arrow_left

@Composable
fun EditProfileScreen(navHostController: NavHostController) {

    val viewModel = viewModel<ProfileViewModel> {
        ProfileViewModel()
    }
    val user by viewModel.user.collectAsState()
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        AppBar(
            navigationIcon = painterResource(Res.drawable.ic_arrow_left),
            title = "Edit Profile",
            onNavigationIconClick = { navHostController.popBackStack() }
        )

        EditProfileScreenContent(viewModel, user)

        LaunchedEffect(user) {
            Logger.d("DATE_OF_ BIRTH $user")
        }
    }
}

@Composable
fun ColumnScope.EditProfileScreenContent(viewModel: ProfileViewModel, user: UserRequestResponse) {
    val focus = LocalFocusManager.current
    val requiredText = stringResource(Res.string.all_field_required)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.weight(1f)
    ) {
        EditProfileFormContent(user, viewModel)
    }
    ButtonPrimary(
        fontStyle = text_style_lead_text,
        modifier = Modifier.navigationBarsPadding().fillMaxWidth().padding(horizontal = 16.dp)
            .height(58.dp),
        buttonText = "Update",
        onClick = {
            focus.clearFocus()
            val isValid = user.isValid()
            if (isValid.first) viewModel.updateUserProfile(user)
            else Application.showToast(isValid.second ?: requiredText)
        },
    )
}

@Composable
fun EditProfileFormContent(userResponse: UserRequestResponse, viewModel: ProfileViewModel) {
    val datePickerState = remember { mutableStateOf(false) }
    val imagePickerState = remember { mutableStateOf(false) }

    var createUserRequest by remember {
        mutableStateOf(userResponse)
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 200.dp)
    ) {
        item {
            UserProfilePicture(userResponse = userResponse) {
                imagePickerState.value = true
            }
        }

        item {
            UserDetailsScreenMainContent(
                userResponse = userResponse,
                onChange = { createUserRequest = it.copy() }
            )
        }
    }

    FilePickerBottomSheetHost(
        showSheet = imagePickerState.value,
        allowedDocs = listOf(SupportedFiles.IMAGE),
        onDismissRequest = {
            imagePickerState.value = false
        },
        onFilePicked = { file, type ->
            val image = file.source().buffered().readByteArray()
            createUserRequest.imageFileName = file.name
            viewModel.updateUserProfile(createUserRequest, image)
            imagePickerState.value = false
        }
    )
}

@Composable
fun showDatePicker(
    state: MutableState<Boolean>,
    viewModel: ProfileViewModel,
    userRequestResponse: UserRequestResponse,
) {

    DatePickerDialog(
        maxDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
        onDismiss = { state.value = false },
        onDateSelected = {
            state.value = false
            val updatedUser = userRequestResponse.copy(dateOfBirth = it.toIsoDateTimeString())
            viewModel.setUserResponse(updatedUser)
        },
        showPicker = state.value,
        selectedDate = userRequestResponse.dateOfBirth.convertIsoToDate()?.toLocalDateTime()?.date,
    )
}
