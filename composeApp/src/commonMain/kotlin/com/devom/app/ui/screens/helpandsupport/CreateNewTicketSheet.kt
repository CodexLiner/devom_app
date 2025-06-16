package com.devom.app.ui.screens.helpandsupport

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.devom.app.models.SupportedFiles
import com.devom.app.theme.blackColor
import com.devom.app.theme.text_style_h3
import com.devom.app.theme.whiteColor
import com.devom.app.ui.components.ButtonPrimary
import com.devom.app.ui.components.DocumentPicker
import com.devom.app.ui.components.TextInputField
import com.devom.models.helpandsupport.CreateTicketRequest
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.source
import kotlinx.coroutines.launch
import kotlinx.io.buffered
import kotlinx.io.readByteArray
import org.jetbrains.compose.resources.stringResource
import pandijtapp.composeapp.generated.resources.Res
import pandijtapp.composeapp.generated.resources.all_field_required

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewTicketSheet(
    showSheet: Boolean,
    title: String? = null,
    onDismiss: () -> Unit,
    onClick: (CreateTicketRequest) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val errorState = remember { mutableStateOf(false) }

    if (showSheet) {
        ModalBottomSheet(
            containerColor = whiteColor, onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    onDismiss()
                }
            }, sheetState = sheetState
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                title?.let {
                    Text(text = it, style = text_style_h3, color = blackColor)
                }
                CreateNewTicketSheetContent(errorState) {
                    scope.launch {
                        sheetState.hide()
                        onClick(it)
                    }
                }
            }

        }
    }
}

@Composable
fun CreateNewTicketSheetContent(
    errorState: MutableState<Boolean>,
    onClick: (CreateTicketRequest) -> Unit
) {
    val createTicketInput = remember { mutableStateOf(CreateTicketRequest()) }
    val selectedFile = remember { mutableStateOf<String?>(null) }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TextInputField(
            placeholder = "Title *"
        ) {
            createTicketInput.value = createTicketInput.value.copy(subject = it)
            errorState.value = false
        }
        TextInputField(
            placeholder = "Description *"
        ) {
            createTicketInput.value = createTicketInput.value.copy(message = it)
        }

        DocumentPicker(
            addIconOnly = selectedFile.value?.isNotEmpty() == true,
            modifier = Modifier,
            title = "Image",
            message = "Upload your file here",
            allowedDocs = listOf(SupportedFiles.IMAGE)
        ) { file, _ ->
            createTicketInput.value = createTicketInput.value.copy(image = file.source().buffered().readByteArray())
            selectedFile.value = file.name
            errorState.value = false
        }

        if (selectedFile.value?.isNotEmpty() == true) {
            Text(text = selectedFile.value.toString(), color = blackColor)
        }

        if (errorState.value) Text(
            text = stringResource(Res.string.all_field_required), color = Color.Red
        )

        ButtonPrimary(
            modifier = Modifier.padding(top = 26.dp).fillMaxWidth().height(58.dp),
            buttonText = "Submit Request"
        ) {
            if (createTicketInput.value.subject.isEmpty() || createTicketInput.value.message.isEmpty()) {
                errorState.value = true
                return@ButtonPrimary
            }
            onClick(createTicketInput.value)
        }

    }
}
