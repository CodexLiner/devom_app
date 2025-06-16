package com.devom.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.devom.app.utils.format
import com.devom.utils.date.MMM_dd_yyyy
import com.devom.utils.date.asDate
import com.devom.utils.date.toEpochMilli
import com.devom.utils.date.toLocalDateTime
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    showPicker: Boolean,
    onDismiss: () -> Unit = {},
    onDateSelected: (LocalDate) -> Unit = {},
    selectedDate: LocalDate? = null,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null
) {
    if (showPicker) {
        val initialDate = selectedDate ?: Clock.System.now().toLocalDateTime().date
        val datePickerState = rememberDatePickerState(
            initialDisplayMode = DisplayMode.Picker,
            initialSelectedDateMillis = initialDate.toEpochMilli(),
            initialDisplayedMonthMillis = initialDate.toEpochMilli(),
            yearRange = (1900..2100)
        )

        val minMillis = minDate?.toEpochMilli()
        val maxMillis = maxDate?.toEpochMilli()

        val selectedMillis = datePickerState.selectedDateMillis
        val isValid = selectedMillis != null &&
                (minMillis?.let { selectedMillis >= it } != false) &&
                (maxMillis?.let { selectedMillis <= it } != false)

        DatePickerDialog(
            onDismissRequest = onDismiss,
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .fillMaxWidth(0.9f),
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 8.dp,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false,
            ),
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedMillis?.let {
                            onDateSelected(it.asDate.toLocalDateTime().date)
                        }
                        onDismiss()
                    },
                    enabled = isValid
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            },
            content = {
                Column {
                    DatePicker(
                        showModeToggle = false,
                        title = null,
                        headline = null,
                        state = datePickerState,
                    )
                    if (!isValid) {
                        val minText = minDate?.toString()
                        val maxText = maxDate?.toString()
                        Text(
                            buildString {
                                append("Please select a valid date")
                                if (minText != null) append(" after $minText")
                                if (maxText != null) append(" and before $maxText")
                            },
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        )
    }
}