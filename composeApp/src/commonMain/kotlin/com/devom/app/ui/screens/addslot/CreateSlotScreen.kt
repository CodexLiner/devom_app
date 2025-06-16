package com.devom.app.ui.screens.addslot

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.devom.app.theme.backgroundColor
import com.devom.app.theme.bgColor
import com.devom.app.theme.inputColor
import com.devom.app.ui.components.AppBar
import com.devom.app.ui.components.ButtonPrimary
import com.devom.app.ui.components.DateItem
import com.devom.app.ui.components.NoContentView
import com.devom.app.utils.dashedBorder
import com.devom.app.utils.format
import com.devom.app.utils.to12HourTime
import com.devom.models.slots.Slot
import com.devom.utils.date.formatIsoTo
import com.devom.utils.date.yyyy_MM_DD
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pandijtapp.composeapp.generated.resources.Res
import pandijtapp.composeapp.generated.resources.add_time_slot
import pandijtapp.composeapp.generated.resources.ic_arrow_left
import pandijtapp.composeapp.generated.resources.ic_no_slots
import pandijtapp.composeapp.generated.resources.set_availablity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSlotScreen(
    navController: NavController,
    initialSelectedDate: LocalDate = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date,
) {
    val viewModel = viewModel { CreateSlotViewModel() }

    val buttonText = remember { mutableStateOf("Update Time Slot") }

    LaunchedEffect(Unit) {
        viewModel.getAvailableSlots()
    }

    val sheetState = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {

        AppBar(
            navigationIcon = painterResource(Res.drawable.ic_arrow_left),
            title = stringResource(Res.string.set_availablity),
            onNavigationIconClick = { navController.popBackStack() }
        )

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            CreateSlotScreenContent(
                innerPadding = PaddingValues(0.dp),
                initialSelectedDate = initialSelectedDate,
                viewModel = viewModel,
                sheetState = sheetState
            ) {
                buttonText.value = it
            }
        }

        ButtonPrimary(
            buttonText = stringResource(Res.string.add_time_slot),
            modifier = Modifier.fillMaxWidth().navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp).height(58.dp)
        ) {
            scope.launch {
                sheetState.value = true
            }
        }
    }
}


@Composable
fun CreateSlotScreenContent(
    innerPadding: PaddingValues,
    initialSelectedDate: LocalDate,
    viewModel: CreateSlotViewModel,
    sheetState: MutableState<Boolean>,
    buttonTextChange: (String) -> Unit = {},

    ) {

    val availableSlots = viewModel.slots.collectAsState()
    var selectedDate by remember { mutableStateOf(initialSelectedDate) }
    val formattedMonthYear = remember(selectedDate) {
        """${
            selectedDate.month.name.lowercase().replaceFirstChar(Char::uppercaseChar)
        } ${selectedDate.year}"""
    }
    val startOfList = initialSelectedDate
    val dates = remember(startOfList) {
        List(7) { index -> startOfList.plus(index, DateTimeUnit.DAY) }
    }
    LaunchedEffect(selectedDate) {
        val isSlotsAvailable = availableSlots.value.any {
            it.availableDate.formatIsoTo(yyyy_MM_DD) == selectedDate.format(yyyy_MM_DD)
        }
        val buttonText = if (isSlotsAvailable) "Update Time Slot" else "Add Time Slot"
        buttonTextChange(buttonText)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp)
    ) {
        HeaderContent(formattedMonthYear)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(dates) { date ->
                DateItem(
                    date = date,
                    isSelected = date == selectedDate,
                    onClick = { selectedDate = date })
            }
        }

        Spacer(Modifier.height(24.dp))
        SlotsSections(availableSlots, selectedDate, sheetState, viewModel)
    }
}

@Composable
fun ColumnScope.SlotsSections(
    availableSlots: State<List<Slot>>,
    selectedDate: LocalDate,
    sheetState: MutableState<Boolean>,
    viewModel: CreateSlotViewModel,
) {
    Text(text = "Slots Available", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
    val temporarySelectedSlots = remember { mutableStateOf(listOf<Slot>()) }
    val slotsConfirmationSheet = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.weight(1f).fillMaxWidth().border(
            width = 1.dp,
            color = Color.LightGray.copy(alpha = 0.5f),
            shape = RoundedCornerShape(16.dp)
        ).dashedBorder(
            dashLength = 3.dp,
            gapLength = 1.dp,
            color = inputColor,
            shape = RoundedCornerShape(16.dp)
        ).background(bgColor, shape = RoundedCornerShape(16.dp)).padding(16.dp)
    ) {
        val filteredSlots = availableSlots.value.filter {
            it.availableDate.formatIsoTo(yyyy_MM_DD) == selectedDate.format(yyyy_MM_DD)
        }
        if (filteredSlots.isNotEmpty()) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(filteredSlots) { slot ->
                    TimeSlotItem(
                        datePickerEnable = false,
                        modifier = Modifier.fillMaxWidth().animateItem(),
                        slot = slot.copy(
                            startTime = slot.startTime.to12HourTime(),
                            endTime = slot.endTime.to12HourTime()
                        ),
                        onRemove = {
                            viewModel.removePanditSlot(slot)
                        }
                    )
                }
            }
        } else NoContentView(
            message = "No slots have been added yet. Please add time slots for your availability.",
            image = Res.drawable.ic_no_slots,
            title = "No Slots Added"
        )

        TimeSlotBottomSheet(
            initialSelectedSlots = availableSlots.value,
            initialSelectedDate = selectedDate,
            showSheet = sheetState.value,
            onDismiss = { sheetState.value = false }
        ) {
            temporarySelectedSlots.value = it
            slotsConfirmationSheet.value = true
        }

        TimeSlotConfirmationBottomSheet(
            selectedSlots = temporarySelectedSlots.value,
            showSheet = slotsConfirmationSheet.value,
            onDismiss = {
                temporarySelectedSlots.value = listOf()
                slotsConfirmationSheet.value = false
            }
        ) { selectedSlots, selectedRepeatOption ->
            slotsConfirmationSheet.value = false
            sheetState.value = false
            viewModel.createPanditSlot(selectedSlots)
            temporarySelectedSlots.value = listOf()
        }
    }
}

@Composable
fun HeaderContent(formattedMonthYear: String) {
    Spacer(Modifier.height(16.dp))

    Text(
        text = formattedMonthYear, fontSize = 22.sp, fontWeight = FontWeight.Bold
    )

    Spacer(Modifier.height(12.dp))
}