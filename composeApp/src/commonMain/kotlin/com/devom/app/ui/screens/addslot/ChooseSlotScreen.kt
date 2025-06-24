package com.devom.app.ui.screens.addslot

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.devom.app.theme.backgroundColor
import com.devom.app.theme.bgColor
import com.devom.app.theme.greyColor
import com.devom.app.theme.inputColor
import com.devom.app.theme.primaryColor
import com.devom.app.theme.textBlackShade
import com.devom.app.ui.components.AppBar
import com.devom.app.ui.components.ButtonPrimary
import com.devom.app.ui.components.DateItem
import com.devom.app.ui.components.NoContentView
import com.devom.app.utils.dashedBorder
import com.devom.app.utils.format
import com.devom.app.utils.to12HourTime
import com.devom.models.slots.Slot
import com.devom.utils.date.addDays
import com.devom.utils.date.formatIsoTo
import com.devom.utils.date.yyyy_MM_DD
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.add_time_slot
import devom_app.composeapp.generated.resources.book_now
import devom_app.composeapp.generated.resources.ic_arrow_left
import devom_app.composeapp.generated.resources.ic_no_slots
import devom_app.composeapp.generated.resources.set_availablity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseSlotScreen(
    navController: NavController,
    initialSelectedDate: LocalDate = Clock.System.now().addDays(1)
        .toLocalDateTime(TimeZone.currentSystemDefault()).date,
) {
    val viewModel = viewModel { ChooseViewModel() }

    val buttonText = remember { mutableStateOf("Update Time Slot") }

    LaunchedEffect(Unit) {
        viewModel.getAvailableSlots()
    }

    val sheetState = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().background(backgroundColor)
    ) {

        AppBar(
            navigationIcon = painterResource(Res.drawable.ic_arrow_left),
            title = stringResource(Res.string.set_availablity),
            onNavigationIconClick = { navController.popBackStack() })

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            ChooseScreenContent(
                innerPadding = PaddingValues(0.dp),
                initialSelectedDate = initialSelectedDate,
                viewModel = viewModel,
                sheetState = sheetState
            ) {
                buttonText.value = it
            }
        }

        ButtonPrimary(
            buttonText = stringResource(Res.string.book_now),
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
fun ChooseScreenContent(
    innerPadding: PaddingValues,
    initialSelectedDate: LocalDate,
    viewModel: ChooseViewModel,
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
        SlotsSections(availableSlots, selectedDate)
    }
}

@Composable
fun ColumnScope.SlotsSections(
    availableSlots: State<List<Slot>>,
    selectedDate: LocalDate,

    ) {
    Text(text = "Slots Available", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
    var selectedSlot by remember { mutableStateOf<Slot?>(null) }

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
        ).background(bgColor, shape = RoundedCornerShape(16.dp)).padding(12.dp)
    ) {
        val filteredSlots = availableSlots.value.filter {
            it.availableDate.formatIsoTo(yyyy_MM_DD) == selectedDate.format(yyyy_MM_DD)
        }
        if (filteredSlots.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredSlots) { slot ->
                    SlotItem(
                        slot = slot,
                        isSelected = slot == selectedSlot,
                        onClick = { selectedSlot = slot }
                    )
                }
            }
        } else NoContentView(
            message = "No slots available for selected date.",
            image = Res.drawable.ic_no_slots,
            title = null
        )
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

@Composable
fun SlotItem(
    slot: Slot,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Text(
        text = slot.startTime.to12HourTime(),
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        color = if (isSelected) primaryColor else textBlackShade,
        modifier = Modifier
            .border(
                width = 1.dp,
                color = if (isSelected) primaryColor.copy(0.5f) else greyColor.copy(0.5f),
                shape = RoundedCornerShape(30.dp)
            )
            .background(
                color = if (isSelected) primaryColor.copy(0.08f) else Color.Transparent,
                shape = RoundedCornerShape(30.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 14.dp)
    )
}


