package com.devom.app.ui.screens.booking.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.devom.app.models.ApplicationStatus
import com.devom.app.models.getColor
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
import devom_app.composeapp.generated.resources.ic_check
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
    }

    Column(
        modifier = Modifier.fillMaxSize().background(backgroundColor)
    ) {
        AppBar(
            title = "Booking Details",
            navigationIcon = painterResource(Res.drawable.ic_arrow_left),
            onNavigationIconClick = { navController.popBackStack() },
            actions = {
                booking.value?.let { booking ->
                    val contentColor = booking.status.getColor()
                    Box(
                        modifier = Modifier.padding(end = 8.dp)
                            .background(contentColor.copy(0.08f), shape = RoundedCornerShape(50))
                    ) {
                        Text(
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                            text = booking.status.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                            color = contentColor,
                            fontWeight = FontWeight.W600,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                        )
                    }
                }
            }
        )
        booking.value?.let {
            BookingDetailScreenContent(it)
        }
    }
}

@Composable
fun ColumnScope.BookingDetailScreenContent(
    booking: GetBookingsResponse,
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
    ) {
        val today = Clock.System.now().toLocalDateTime().date
        val bookingDate = booking.bookingDate.convertIsoToDate()?.toLocalDateTime()?.date
        val isPoojaStarted = booking.status == ApplicationStatus.STARTED.status

        if (bookingDate != null && (bookingDate >= today) && booking.status != ApplicationStatus.COMPLETED.status) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).background(
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
                        text = "Your OTP to ${if (isPoojaStarted.not()) "start" else "complete"} Pooja:",
                        fontWeight = FontWeight.W500,
                        style = text_style_lead_body_1,
                        color = textBlackShade
                    )
                    Text(
                        text = if (isPoojaStarted) booking.endPin else booking.startPin,
                        style = text_style_h4,
                        color = blackColor
                    )
                }
            }
        }

        item {
            BookingCard(
                isDetails = true,
                showStatus = false,
                booking = booking
            )
        }

        item {
            MetaInfo("Urgent Booking" , booking.isUrgent == 1)
        }

        item {
            MetaInfo("Prepaid Booking" , booking.isPaid == 1)
        }

        item {
            BookingSamagriHeader(booking)
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
fun MetaInfo(title : String , isChecked: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 28.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = textBlackShade,
            fontWeight = FontWeight.W600,
            fontSize = 14.sp,
        )
        Checkbox(isChecked = isChecked)
    }
}

@Composable
fun BookingSamagriHeader(booking: GetBookingsResponse) {
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

        Row(
            modifier = Modifier.padding(top = 28.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Checkbox(isChecked = booking.isWithItem == 1)
            Text(
                text = "With Samagri",
                color = textBlackShade,
                fontWeight = FontWeight.W600,
                fontSize = 14.sp,
            )
        }
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


@Composable
fun Checkbox(
    modifier: Modifier = Modifier,
    borderColor: Color = primaryColor,
    checkmarkColor: Color = primaryColor,
    size: Dp = 20.dp,
    cornerRadius: Dp = 4.dp,
    isChecked: Boolean = true,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .size(size)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(cornerRadius))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isChecked) {
            Icon(
                painter = painterResource(Res.drawable.ic_check),
                contentDescription = null,
                tint = checkmarkColor,
                modifier = Modifier.size(size * 0.6f)
            )
        }
    }
}

