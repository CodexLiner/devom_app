package com.devom.app.ui.screens.addslot

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devom.app.models.RepeatOption
import com.devom.app.theme.greyColor
import com.devom.app.theme.inputColor
import com.devom.app.theme.primaryColor
import com.devom.app.theme.text_style_h3
import com.devom.app.theme.text_style_lead_body_1
import com.devom.app.theme.whiteColor
import com.devom.app.ui.components.ButtonPrimary
import com.devom.app.utils.to24HourTime
import com.devom.models.slots.Slot
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import devom_app.composeapp.generated.resources.Confirmation_Time_Slots
import devom_app.composeapp.generated.resources.Res

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSlotBottomSheet(
    initialSelectedSlots: List<Slot> = listOf(),
    showSheet: Boolean,
    initialSelectedDate: LocalDate,
    onDismiss: () -> Unit,
    onSlotSelected: (List<Slot>) -> Unit,
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
            },
            sheetState = sheetState
        ) {
            TimeSlotSelectorScreen(
                initialSelectedDate = initialSelectedDate,
                initialSelectedSlots = initialSelectedSlots
            ) {
                scope.launch {
                    onSlotSelected(it.map { slot ->
                        slot.copy(
                            startTime = slot.startTime.to24HourTime(),
                            endTime = slot.endTime.to24HourTime()
                        )
                    }.distinctBy { Pair(it.availableDate, Pair(it.startTime, it.endTime)) })
                    sheetState.hide()
                    onDismiss()
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSlotConfirmationBottomSheet(
    selectedSlots: List<Slot> = listOf(),
    showSheet: Boolean,
    initialSelectedDate: LocalDate,
    onDismiss: () -> Unit,
    onSlotSelected: (List<Slot>) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val options = listOf(
        "Save this slot for the same time upcoming week",
        "Save this slot for the same time upcoming month"
    )
    var selectedOption by remember { mutableStateOf<String?>(null) }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    onDismiss()
                }
            },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Repeat this slot", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(8.dp))

                options.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedOption = option }
                            .padding(vertical = 8.dp)
                    ) {
                        RadioButton(
                            selected = selectedOption == option,
                            onClick = { selectedOption = option }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(option)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {

                    onDismiss()
                }, modifier = Modifier.align(Alignment.End)) {
                    Text("Confirm")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSlotConfirmationBottomSheet(
    selectedSlots: List<Slot> = listOf(),
    showSheet: Boolean,
    onDismiss: () -> Unit,
    onSlotSelected: (List<Slot>, selectedRepeatOption: RepeatOption?) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    if (!showSheet) return

    var selectedOption by rememberSaveable { mutableStateOf<RepeatOption?>(null) }

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch {
                sheetState.hide()
                onDismiss()
            }
        },
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = stringResource(Res.string.Confirmation_Time_Slots), style = text_style_h3)
            Spacer(modifier = Modifier.height(12.dp))

            RepeatOption.entries.forEach { option ->
                val isChecked = selectedOption == option

                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedOption = if (isChecked) null else option
                        }
                        .padding(vertical = 8.dp)
                ) {
                    Checkbox(
                        colors = CheckboxDefaults.colors().copy(
                            checkedBoxColor = primaryColor,
                            uncheckedBoxColor = Color.Transparent
                        ),
                        checked = isChecked,
                        onCheckedChange = {
                            selectedOption = if (isChecked) null else option
                        }
                    )
                    Text(
                        text = option.label,
                        fontWeight = FontWeight.W500,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                }

            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                ButtonPrimary(
                    colors = ButtonDefaults.buttonColors()
                        .copy(containerColor = whiteColor, contentColor = greyColor),
                    textColor = greyColor,
                    fontStyle = text_style_lead_body_1,
                    buttonText = "Cancel",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .height(48.dp)
                        .border(color = inputColor , width = 1.dp , shape = RoundedCornerShape(12.dp))
                        .weight(1f),
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                ButtonPrimary(
                    fontStyle = text_style_lead_body_1,
                    buttonText = "Save",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .height(48.dp)
                        .weight(1f),
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            onDismiss()
                            onSlotSelected(selectedSlots, selectedOption)
                        }
                    }
                )
            }
        }
    }
}
