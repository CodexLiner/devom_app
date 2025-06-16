package com.devom.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devom.app.models.OptionsBottomSheetItem
import com.devom.app.theme.primaryColor
import com.devom.app.theme.text_style_h3
import com.devom.app.theme.text_style_lead_body_1
import com.devom.app.theme.whiteColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsBottomSheet(
    showSheet: Boolean,
    title: String = "Select Option",
    options: List<OptionsBottomSheetItem> = listOf(),
    onDismiss: () -> Unit,
    onSelect: (OptionsBottomSheetItem) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    onDismiss()
                }
            }, sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(text = title, style = text_style_h3)
                Spacer(modifier = Modifier.height(12.dp))
                options.forEach { option ->
                    ButtonPrimary(
                        colors = ButtonDefaults.buttonColors().copy(
                            containerColor = primaryColor, contentColor = whiteColor
                        ),
                        fontStyle = text_style_lead_body_1,
                        buttonText = option.title,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp).height(48.dp),
                        onClick = {
                            scope.launch {
                                onSelect(option)
                            }
                        })
                }
            }
        }

    }
}