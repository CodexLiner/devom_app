package com.devom.app.ui.screens.booking

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.devom.app.models.ApplicationStatus
import com.devom.app.theme.backgroundColor
import com.devom.app.theme.blackColor
import com.devom.app.theme.primaryColor
import com.devom.app.theme.text_style_h4
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
import com.devom.models.slots.GetBookingsResponse
import com.devom.utils.date.convertIsoToDate
import com.devom.utils.date.toLocalDateTime
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
        val sheetState = remember { mutableStateOf(false) }
        val selectedBooking = remember { mutableStateOf<GetBookingsResponse?>(null) }


        val today = Clock.System.now().toLocalDateTime().date

        val filteredBookings = when (selectedTabIndex.value) {
            0 -> bookings.value.filter {
                val bookingDate = it.bookingDate.convertIsoToDate()?.toLocalDateTime()?.date
                bookingDate != null && (bookingDate >= today)
            }

            1 -> bookings.value.filter {
                val bookingDate = it.bookingDate.convertIsoToDate()?.toLocalDateTime()?.date
                bookingDate != null && bookingDate < today
            }

            else -> bookings.value
        }


        if (filteredBookings.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 200.dp
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredBookings) { booking ->
                    BookingCard(
                        booking = booking,
                        onBookingUpdate = {
                            viewModel.updateBookingStatus(booking.bookingId, it)
                        },
                        onClick = {
                            if (selectedTabIndex.value == 1) {
                                sheetState.value = true
                                selectedBooking.value = booking
                            } else {
                                navHostController.navigate(Screens.BookingDetails.path + "/${booking.bookingId}")
                                selectedBooking.value = null
                            }
                        }
                    )
                }
            }
        } else NoContentView(
            message = "You haven’t made any bookings yet. Once you do, they’ll appear here.",
            title = "No Bookings Available",
            image = Res.drawable.ic_no_bookings
        )

        selectedBooking.value?.let {
            AddBookingReviewSheet(
                showSheet = sheetState.value,
                onDismiss = { sheetState.value = false }
            ) { rating, reviewText ->
                viewModel.addBookingReview(it, rating = rating, reviewText = reviewText)
            }
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