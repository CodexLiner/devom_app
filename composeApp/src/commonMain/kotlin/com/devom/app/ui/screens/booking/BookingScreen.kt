package com.devom.app.ui.screens.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.devom.app.models.ApplicationStatus
import com.devom.app.theme.backgroundColor
import com.devom.app.ui.components.AppBar
import com.devom.app.ui.components.NoContentView
import com.devom.app.ui.components.StatusTabRow
import com.devom.app.ui.components.TabRowItem
import com.devom.app.ui.navigation.Screens
import com.devom.app.ui.screens.booking.components.BookingCard
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.ic_no_bookings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(navHostController: NavHostController , onNavigationIconClick: () -> Unit) {
    val viewModel: BookingViewModel = viewModel { BookingViewModel() }
    val bookings = viewModel.bookings.collectAsState()
    val tabs = listOf(TabRowItem("Pending"), TabRowItem("Completed"), TabRowItem("Rejected"))
    var selectedTabIndex = remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.getBookings()
    }

    Column(
        modifier = Modifier.fillMaxSize().background(backgroundColor)
    ) {
        AppBar(title = "Bookings" , onNavigationIconClick = onNavigationIconClick)
        StatusTabRow(selectedTabIndex = selectedTabIndex, tabs = tabs)

        val filteredBookings = when (selectedTabIndex.value) {
            0 -> bookings.value.filter { it.status.lowercase() != ApplicationStatus.COMPLETED.status && it.status.lowercase() != ApplicationStatus.REJECTED.status }
            1 -> bookings.value.filter { it.status.lowercase() == ApplicationStatus.COMPLETED.status }
            2 -> bookings.value.filter { it.status.lowercase() == ApplicationStatus.REJECTED.status }
            else -> bookings.value
        }

        if (filteredBookings.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp , bottom = 200.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredBookings) { booking ->
                    BookingCard(
                        booking = booking,
                        onBookingUpdate = {
                            viewModel.updateBookingStatus(booking.bookingId, it)
                        },
                        onClick = {
                            navHostController.navigate(Screens.BookingDetails.path + "/${booking.bookingId}")
                        }
                    )
                }
            }
        } else NoContentView(message = "You haven’t made any bookings yet. Once you do, they’ll appear here.", title = "No Bookings Available", image = Res.drawable.ic_no_bookings)
    }
}