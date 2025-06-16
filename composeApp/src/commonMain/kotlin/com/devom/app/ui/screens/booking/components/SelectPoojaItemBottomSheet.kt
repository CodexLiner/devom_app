package com.devom.app.ui.screens.booking.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.devom.app.theme.blackColor
import com.devom.app.theme.text_style_h3
import com.devom.app.theme.whiteColor
import com.devom.app.ui.components.ButtonPrimary
import com.devom.app.ui.components.DropDownItem
import com.devom.app.ui.components.ExposedDropdown
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectPoojaItemBottomSheet(
    showSheet: Boolean,
    title: String? = null,
    items: List<DropDownItem>,
    onDismiss: () -> Unit,
    onClick: (DropDownItem) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val selectedDropDownItem = remember { mutableStateOf<DropDownItem?>(null) }
    val errorState = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    if (showSheet) {
        ModalBottomSheet(
            containerColor = whiteColor,
            modifier = Modifier.systemBarsPadding().heightIn(min = 300.dp),
            onDismissRequest = {
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
                ExposedDropdown(
                    selectedOption = selectedDropDownItem.value,
                    placeholder = "Select Pooja Item",
                    options = items,
                ) {
                    selectedDropDownItem.value = it
                    errorState.value = false
                }
                if (errorState.value) Text(
                    text = "please select pooja item",
                    color = Color.Red
                )


                ButtonPrimary(
                    modifier = Modifier.padding(top = 26.dp).fillMaxWidth().height(58.dp),
                    buttonText = "Save"
                ) {
                    selectedDropDownItem.value?.let {
                        scope.launch {
                            sheetState.hide()
                            onClick(it)
                            onDismiss()
                            selectedDropDownItem.value = null
                        }

                    } ?: run {
                        errorState.value = true
                    }
                }
            }
        }
    }
}

