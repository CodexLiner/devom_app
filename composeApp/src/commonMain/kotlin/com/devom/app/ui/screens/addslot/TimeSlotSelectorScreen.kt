package com.devom.app.ui.screens.addslot

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devom.app.theme.inputColor
import com.devom.app.theme.text_style_h4
import com.devom.app.theme.text_style_lead_text
import com.devom.app.ui.components.ButtonPrimary
import com.devom.app.ui.components.DateItem
import com.devom.app.utils.dashedBorder
import com.devom.app.utils.format12HourTime
import com.devom.app.utils.parse12HourTime
import com.devom.app.utils.to12HourTime
import com.devom.app.utils.toTimeParts
import com.devom.app.utils.updateSlotTimeAndShiftFollowingSlots
import com.devom.models.slots.Slot
import com.devom.utils.Application
import com.devom.utils.date.convertIsoToDate
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pandijtapp.composeapp.generated.resources.Res
import pandijtapp.composeapp.generated.resources.ic_close
import pandijtapp.composeapp.generated.resources.ic_dual_dropdown
import pandijtapp.composeapp.generated.resources.ic_plus
import pandijtapp.composeapp.generated.resources.text_select_time_slot

@Composable
fun TimeSlotSelectorScreen(
    initialSelectedSlots: List<Slot> = listOf(),
    initialSelectedDate: LocalDate = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date.plus(1, DateTimeUnit.DAY),
    onSlotSelected: (List<Slot>) -> Unit,
) {
    var selectedDate by remember { mutableStateOf(initialSelectedDate) }
    val dateSlotMap = remember { mutableStateMapOf<LocalDate, MutableList<Slot>>() }

    val startOfList = initialSelectedDate
    val dates = remember(startOfList) {
        List(7) { index -> startOfList.plus(index, DateTimeUnit.DAY) }
    }

    LaunchedEffect(Unit) {
        val groupedSlots = initialSelectedSlots.groupBy {
            it.availableDate.convertIsoToDate()
                ?.toLocalDateTime(TimeZone.currentSystemDefault())?.date.toString()
        }
        dates.forEach { date ->
            val slotsForDate = groupedSlots[date.toString()] ?: emptyList()
            dateSlotMap[date] = slotsForDate.map {
                it.copy(
                    startTime = it.startTime.to12HourTime(), endTime = it.endTime.to12HourTime()
                )
            }.toMutableList()
        }
    }

    val currentSlots = dateSlotMap[selectedDate] ?: mutableListOf()

    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(dates) { date ->
                DateItem(
                    dateTextStyle = text_style_h4,
                    modifier = Modifier.width(88.dp).aspectRatio(1f),
                    date = date,
                    isSelected = date == selectedDate,
                    onClick = { selectedDate = date })
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = stringResource(Res.string.text_select_time_slot),
                style = text_style_h4
            )

            TimeSlotListCard(
                timeSlots = currentSlots, onTimeSlotsUpdated = { updatedList ->
                    dateSlotMap[selectedDate] = updatedList
                })

            ButtonPrimary(
                buttonText = "Confirm & Save",
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(58.dp),
                enabled = dateSlotMap.values.any { it.isNotEmpty() }) {
                val allSlots = dateSlotMap.flatMap { (date, slots) ->
                    slots.map { slot ->
                        slot.copy(availableDate = date.toString())
                    }
                }
                onSlotSelected(allSlots)
            }
        }
    }
}


@Composable
fun TimeSlotListCard(
    timeSlots: List<Slot>,
    onTimeSlotsUpdated: (MutableList<Slot>) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).dashedBorder(
            dashLength = 3.dp,
            gapLength = 1.dp,
            color = inputColor,
            shape = RoundedCornerShape(16.dp)
        ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F7FF)),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth().heightIn(max = 500.dp, min = 500.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            items(timeSlots.size) { index ->
                val slot = timeSlots[index]
                TimeSlotItem(slot = slot, onStartTimeSelected = { newStartTime ->
                    val updated = timeSlots.updateSlotTimeAndShiftFollowingSlots(
                        indexToUpdate = index, newStartTime = newStartTime
                    )
                    onTimeSlotsUpdated(updated)
                }, onEndTimeSelected = { newEndTime ->
                    val updated = timeSlots.updateSlotTimeAndShiftFollowingSlots(
                        indexToUpdate = index, newEndTime = newEndTime
                    )
                    onTimeSlotsUpdated(updated)
                }, onRemove = {
                    onTimeSlotsUpdated(timeSlots.filterIndexed { i, _ -> i != index }
                        .toMutableList())
                })
            }

            stickyHeader {
                AddTimeSlotButton(
                    timeSlots = timeSlots, onAdd = { onTimeSlotsUpdated(it) })
            }
        }
    }
}


@Composable
fun TimeSlotItem(
    slot: Slot,
    modifier: Modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
    enabled: Boolean = true,
    datePickerEnable: Boolean = true,
    onStartTimeSelected: (String) -> Unit = {},
    onEndTimeSelected: (String) -> Unit = {},
    onRemove: () -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)
        ) {
            TimePickerDialogButton(
                selectedTime = slot.startTime,
                onTimeSelected = onStartTimeSelected,
                minTime = slot.startTime,
                enabled = enabled,
                modifier = Modifier.weight(1f),
                datePickerEnable = datePickerEnable
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "TO", style = text_style_lead_text, fontSize = 14.sp, color = inputColor
            )

            Spacer(modifier = Modifier.width(8.dp))

            TimePickerDialogButton(
                datePickerEnable = datePickerEnable,
                enabled = enabled,
                selectedTime = slot.endTime,
                onTimeSelected = onEndTimeSelected,
                minTime = slot.startTime,
                modifier = Modifier.weight(1f)
            )
        }
        AnimatedVisibility(visible = enabled) {
            IconButton(onClick = onRemove, modifier = Modifier.padding(start = 12.dp).size(20.dp)) {
                Icon(
                    painter = painterResource(Res.drawable.ic_close),
                    contentDescription = "Remove slot",
                    tint = Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogButton(
    selectedTime: String,
    onTimeSelected: (String) -> Unit,
    minTime: String,
    maxTime: LocalDateTime = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date.atTime(23, 59),
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    datePickerEnable: Boolean,
) {
    val (hour, minute) = selectedTime.toTimeParts()
    val timePickerState = remember(selectedTime) {
        TimePickerState(initialHour = hour, initialMinute = minute, is24Hour = false)
    }

    var showPicker by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.clickable { showPicker = true }.border(
            width = 1.dp,
            color = inputColor,
            shape = RoundedCornerShape(12.dp)
        ).padding(horizontal = 8.dp, vertical = 14.dp),
    ) {
        Text(text = selectedTime, fontSize = 12.sp, fontWeight = FontWeight.W500)
        if (enabled) Icon(
            painter = painterResource(Res.drawable.ic_dual_dropdown),
            contentDescription = "Select time"
        )
    }

    if (showPicker && datePickerEnable) {
        val selectedLocalTime = LocalTime(timePickerState.hour, timePickerState.minute)
        val min = parse12HourTime(minTime)
        val max = maxTime.time

        val isValid = selectedLocalTime >= min && selectedLocalTime <= max

        AlertDialog(onDismissRequest = { showPicker = false }, confirmButton = {
            TextButton(
                onClick = {
                    onTimeSelected(
                        format12HourTime(
                            timePickerState.hour, timePickerState.minute
                        )
                    )
                    showPicker = false
                }, enabled = isValid
            ) { Text("OK") }
        }, dismissButton = {
            TextButton(onClick = { showPicker = false }) { Text("Cancel") }
        }, text = {
            Column {
                TimePicker(state = timePickerState)
                if (!isValid) {
                    Text(
                        "Please select a time between $minTime and ${maxTime.time.to12HourTime()}",
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
            }
        })
    }
}

@Composable
fun AddTimeSlotButton(
    timeSlots: List<Slot>,
    onAdd: (MutableList<Slot>) -> Unit,
) {
    val localTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    TextButton(onClick = {
        val currentStartTime = "12:00 AM"
        val defaultSlotDuration = 2
        val timeZone = TimeZone.currentSystemDefault()
        val maxEndTime = localTime.date.atTime(23, 59)
        val cutOffTime = localTime.date.atTime(23, 30) // 11:30 pm cut-off

        if (timeSlots.isNotEmpty()) {
            val lastSlot = timeSlots.last()
            val lastEnd = localTime.date.atTime(parse12HourTime(lastSlot.endTime))
            if (lastEnd >= cutOffTime) {
                Application.showToast("Last slot cannot be more than 11:30 pm")
                return@TextButton
            }
        }

        if (timeSlots.isEmpty()) {
            onAdd(mutableListOf(Slot(startTime = currentStartTime, endTime = "2:00 AM")))
            return@TextButton
        }

        val newStartTimeStr = timeSlots.lastOrNull()?.endTime ?: currentStartTime
        val newStartTime = localTime.date.atTime(parse12HourTime(newStartTimeStr))

        if (newStartTime >= maxEndTime) return@TextButton

        val potentialEndTime = newStartTime.toInstant(timeZone)
            .plus(defaultSlotDuration, DateTimeUnit.HOUR)
            .toLocalDateTime(timeZone)

        val adjustedEndTime = if (potentialEndTime > maxEndTime) maxEndTime else potentialEndTime
        val endTimeStr = adjustedEndTime.time.to12HourTime()

        onAdd(timeSlots.toMutableList().apply {
            add(Slot(startTime = newStartTimeStr, endTime = endTimeStr))
        })
    }) {
        Image(
            painter = painterResource(Res.drawable.ic_plus),
            contentDescription = "Add Time Slot",
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Add More Time On This Date", fontWeight = FontWeight.Medium)
    }
}
