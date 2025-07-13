package com.devom.app.ui.screens.booking.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.devom.app.theme.backgroundColor
import com.devom.app.theme.blackColor
import com.devom.app.theme.greyColor
import com.devom.app.theme.primaryColor
import com.devom.app.theme.textBlackShade
import com.devom.app.theme.text_style_h4
import com.devom.app.theme.text_style_h5
import com.devom.app.theme.text_style_lead_body_1
import com.devom.app.theme.text_style_lead_text
import com.devom.app.theme.whiteColor
import com.devom.app.ui.components.AppBar
import com.devom.app.ui.screens.booking.BookingViewModel
import com.devom.app.ui.screens.booking.components.BookingCard
import com.devom.models.slots.BookingItem
import com.devom.models.slots.GetBookingsResponse
import com.devom.utils.date.convertIsoToDate
import com.devom.utils.date.toLocalDateTime
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.ic_arrow_left
import devom_app.composeapp.generated.resources.pooja_samgri_list
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun BookingDetailScreen(navController: NavController, bookingId: String?) {
    val viewModel: BookingViewModel = viewModel { BookingViewModel() }
    val booking = viewModel.bookingDetailItem.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getBookingById(bookingId.orEmpty())
        viewModel.getPoojaItems()
    }

    Column(
        modifier = Modifier.fillMaxSize().background(backgroundColor)
    ) {
        AppBar(
            title = "Booking Details",
            navigationIcon = painterResource(Res.drawable.ic_arrow_left),
            onNavigationIconClick = { navController.popBackStack() }
        )
        booking.value?.let {
            BookingDetailScreenContent(it, viewModel)
        }
    }
}

@Composable
fun ColumnScope.BookingDetailScreenContent(
    booking: GetBookingsResponse,
    viewModel: BookingViewModel,
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
    ) {
        val today = Clock.System.now().toLocalDateTime().date
        val bookingDate = booking.bookingDate.convertIsoToDate()?.toLocalDateTime()?.date

        if (bookingDate != null && (bookingDate >= today)) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().background(
                        color = primaryColor.copy(.0f),
                        shape = RoundedCornerShape(12.dp)
                    ).border(
                        width = 1.dp,
                        color = primaryColor.copy(.24f),
                        shape = RoundedCornerShape(12.dp)
                    ).padding(
                        horizontal = 12.dp, vertical = 14.dp
                    )
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "Your OTP to Start Pooja:",
                        fontWeight = FontWeight.W500,
                        style = text_style_lead_body_1,
                        color = textBlackShade
                    )
                    Text(
                        text = booking.startPin,
                        style = text_style_h4,
                        color = blackColor
                    )
                }
            }
        }
        item {
            BookingCard(
                booking = booking,
                onBookingUpdate = { viewModel.updateBookingStatus(booking.bookingId, it) }
            )
        }

        item {
            BookingSamagriHeader()
        }

        itemsIndexed(booking.bookingItems) { index, item ->
            val shape = when {
                booking.bookingItems.size == 1 -> RoundedCornerShape(12.dp)
                index == 0 -> RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                index == booking.bookingItems.size - 1 -> RoundedCornerShape(
                    bottomStart = 12.dp,
                    bottomEnd = 12.dp
                )

                else -> RoundedCornerShape(0.dp)
            }
            SamagriItemRow(
                item = item,
                modifier = Modifier.background(color = whiteColor, shape = shape)
                    .padding(horizontal = 16.dp)
            )
            if (index < booking.bookingItems.size - 1) HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = greyColor.copy(.24f),
                thickness = 1.dp
            )
        }
    }
}

@Composable
fun BookingSamagriHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(Res.string.pooja_samgri_list),
            style = text_style_h5,
            color = textBlackShade,
            modifier = Modifier.padding(top = 28.dp, bottom = 16.dp)
        )
    }
}

@Composable
fun SamagriItemRow(modifier: Modifier = Modifier, item: BookingItem) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            style = text_style_lead_body_1,
            text = item.name,
            fontWeight = FontWeight.W500,
            color = textBlackShade,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = item.description,
            fontSize = 12.sp,
            color = greyColor,
            style = text_style_lead_text
        )
    }
}

