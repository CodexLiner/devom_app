package com.devom.app.ui.screens.booking

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.devom.app.models.ApplicationStatus
import com.devom.app.models.RepeatOption
import com.devom.app.theme.backgroundColor
import com.devom.app.theme.blackColor
import com.devom.app.theme.greyColor
import com.devom.app.theme.inputColor
import com.devom.app.theme.primaryColor
import com.devom.app.theme.text_style_h3
import com.devom.app.theme.text_style_h4
import com.devom.app.theme.text_style_lead_body_1
import com.devom.app.theme.text_style_lead_text
import com.devom.app.theme.whiteColor
import com.devom.app.ui.components.AppBar
import com.devom.app.ui.components.ButtonPrimary
import com.devom.app.ui.components.NoContentView
import com.devom.app.ui.components.StatusTabRow
import com.devom.app.ui.components.TabRowItem
import com.devom.app.ui.components.TextInputField
import com.devom.app.ui.navigation.Screens
import com.devom.app.ui.screens.booking.components.BookingCard
import com.devom.app.utils.toColor
import com.devom.models.slots.CancelBookingInput
import com.devom.models.slots.GetBookingsResponse
import com.devom.models.slots.Slot
import com.devom.utils.date.convertIsoToDate
import com.devom.utils.date.toLocalDateTime
import devom_app.composeapp.generated.resources.Confirmation_Alert
import devom_app.composeapp.generated.resources.Confirmation_Alert_Message
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.ic_no_bookings
import devom_app.composeapp.generated.resources.ic_star
import devom_app.composeapp.generated.resources.submit
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(navHostController: NavHostController, onNavigationIconClick: () -> Unit) {
    val viewModel: BookingViewModel = viewModel { BookingViewModel() }
    val bookings = viewModel.bookings.collectAsState()
    val tabs = listOf(
        TabRowItem(ApplicationStatus.UPCOMING.status.capitalize(Locale.current)),
        TabRowItem(ApplicationStatus.PAST.status.capitalize(Locale.current))
    )
    var selectedTabIndex = remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.getBookings()
    }

    Column(
        modifier = Modifier.fillMaxSize().background(backgroundColor)
    ) {
        AppBar(title = "Bookings", onNavigationIconClick = onNavigationIconClick)
        StatusTabRow(selectedTabIndex = selectedTabIndex, tabs = tabs)
        val reviewSheetState = remember { mutableStateOf(false) }
        val cancelSheetState = remember { mutableStateOf(false) }
        val selectedBooking = remember { mutableStateOf<GetBookingsResponse?>(null) }


        val today = Clock.System.now().toLocalDateTime().date

        val pastStatuses = setOf(
            ApplicationStatus.COMPLETED.status,
            ApplicationStatus.CANCELLED.status,
            ApplicationStatus.REJECTED.status,
            ApplicationStatus.PAST.status
        )

        val upcomingStatuses = setOf(
            ApplicationStatus.PENDING.status,
            ApplicationStatus.ACCEPTED.status,
            ApplicationStatus.UPCOMING.status,
            ApplicationStatus.CONFIRMED.status,
            ApplicationStatus.VERIFIED.status,
            ApplicationStatus.STARTED.status
        )

        val filteredBookings = bookings.value.filter { booking ->
            val bookingDate = booking.bookingDate.convertIsoToDate()?.toLocalDateTime()?.date
            when (selectedTabIndex.value) {
                0 -> {
                    bookingDate != null && (booking.status in upcomingStatuses || (bookingDate >= today && booking.status !in pastStatuses))
                }

                1 -> {
                    bookingDate != null && (booking.status in pastStatuses || (bookingDate < today && booking.status !in upcomingStatuses))
                }

                else -> true
            }
        }

        if (filteredBookings.isNotEmpty()) {
            val grouped = filteredBookings.groupBy { it.bookingDate.convertIsoToDate()?.toLocalDateTime()?.date.toString() }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 100.dp
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                grouped.forEach { (date, bookings) ->
                    item {
                        Text(
                            text = date,
                            color = greyColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500
                        )
                    }
                    items(bookings) { booking ->
                        BookingCard(
                            booking = booking,
                            onReviewClick = {
                                reviewSheetState.value = true
                                selectedBooking.value = booking
                            },
                            onCancelBooking = {
                                selectedBooking.value = booking
                                cancelSheetState.value = true
                            },
                            onClick = {
                                navHostController.navigate(Screens.BookingDetails.path + "/${booking.bookingId}")
                                selectedBooking.value = null
                            }
                        )
                    }
                }
            }
        } else NoContentView(
            message = "You haven’t made any bookings yet. Once you do, they’ll appear here.",
            title = "No Bookings Available",
            image = Res.drawable.ic_no_bookings
        )

        selectedBooking.value?.let {
            AddBookingReviewSheet(
                showSheet = reviewSheetState.value,
                onDismiss = { reviewSheetState.value = false }
            ) { rating, reviewText ->
                viewModel.addBookingReview(it, rating = rating, reviewText = reviewText)
            }

            CancelBookingBottomSheet(
                booking = it,
                showSheet = cancelSheetState.value,
                onDismiss = { cancelSheetState.value = false },
                onClick = {
                    viewModel.cancelBooking(it)
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookingReviewSheet(
    showSheet: Boolean,
    onDismiss: () -> Unit,
    onClick: (Int , String) -> Unit ,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val selectedStar = remember { mutableStateOf(0) }
    val title = remember { mutableStateOf("Select Star Rating") }
    val description = remember { mutableStateOf("") }
    if (showSheet) {
        ModalBottomSheet(
            containerColor = whiteColor,
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    onDismiss()
                }
            }, sheetState = sheetState
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Text(text = title.value, style = text_style_h4, color = blackColor)

                Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(5) { index ->
                            val starColor =
                                if (index < selectedStar.value) primaryColor else "#DDDDDD".toColor()
                            Image(
                                modifier = Modifier.size(32.dp).clickable {
                                    selectedStar.value = index + 1
                                    title.value = when (selectedStar.value) {
                                        1 -> "Average!"
                                        2 -> "Good!"
                                        3 -> "Very Good!"
                                        4 -> "Excellent!"
                                        else -> "Outstanding!"
                                    }
                                },
                                painter = painterResource(Res.drawable.ic_star),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(starColor)
                            )
                        }
                    }
                    TextInputField(
                        minLines = 5,
                        singleLine = false,
                        placeholder = "Say something about service?",
                        modifier = Modifier.fillMaxWidth()) {
                        description.value = it
                    }
                }

                ButtonPrimary(
                    modifier = Modifier.navigationBarsPadding().fillMaxWidth()
                        .height(48.dp),
                    buttonText = stringResource(Res.string.submit),
                    onClick = {
                        onDismiss()
                        onClick(selectedStar.value , description.value)
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CancelBookingBottomSheet(
    booking: GetBookingsResponse,
    showSheet: Boolean,
    onDismiss: () -> Unit,
    onClick: (CancelBookingInput) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val reason = remember { mutableStateOf("") }

    if (showSheet) {
        ModalBottomSheet(
            containerColor = whiteColor,
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    onDismiss()
                }
            },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    text = stringResource(Res.string.Confirmation_Alert),
                    style = text_style_h3
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    textAlign = TextAlign.Center,
                    text = stringResource(Res.string.Confirmation_Alert_Message),
                    style = text_style_lead_text
                )
                Spacer(modifier = Modifier.height(12.dp))

                TextInputField(
                    minLines = 5,
                    singleLine = false,
                    placeholder = "Say something about service?",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    reason.value = it
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    ActionButton(
                        text = "Cancel",
                        backgroundColor = whiteColor,
                        contentColor = greyColor,
                        borderColor = inputColor
                    ) {
                        scope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    ActionButton(
                        text = "Confirm"
                    ) {
                        scope.launch {
                            sheetState.hide()
                            onDismiss()
                            onClick(
                                CancelBookingInput(
                                    bookingId = booking.bookingId.toString(),
                                    name = booking.poojaName,
                                    reason = reason.value,
                                    description = reason.value
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.ActionButton(
    text: String,
    backgroundColor: Color = primaryColor,
    contentColor: Color = whiteColor,
    borderColor: Color? = null,
    onClick: () -> Unit
) {
    ButtonPrimary(
        buttonText = text,
        fontStyle = text_style_lead_body_1,
        textColor = contentColor,
        colors = ButtonDefaults.buttonColors()
            .copy(containerColor = backgroundColor, contentColor = contentColor),
        modifier = Modifier
            .padding(vertical = 2.dp)
            .height(48.dp)
            .weight(1f)
            .then(
                if (borderColor != null)
                    Modifier.border(1.dp, borderColor, RoundedCornerShape(12.dp))
                else Modifier
            ),
        onClick = onClick
    )
}
