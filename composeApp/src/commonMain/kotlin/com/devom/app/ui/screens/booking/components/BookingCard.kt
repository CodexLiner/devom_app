package com.devom.app.ui.screens.booking.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devom.app.models.ApplicationStatus
import com.devom.app.models.getColor
import com.devom.app.theme.greyColor
import com.devom.app.theme.textBlackShade
import com.devom.app.theme.text_style_lead_text
import com.devom.app.theme.whiteColor
import com.devom.app.ui.components.AsyncImage
import com.devom.app.utils.toDevomImage
import com.devom.models.slots.GetBookingsResponse
import com.devom.utils.date.convertToAmPm
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.ic_invoice
import devom_app.composeapp.generated.resources.vertical_ellipsis
import org.jetbrains.compose.resources.painterResource

@Composable
fun BookingCard(
    isDetails : Boolean = false,
    showStatus : Boolean = true,
    booking: GetBookingsResponse,
    onReviewClick : () -> Unit = {},
    onCancelBooking: () -> Unit = {},
    onClick: () -> Unit = {},
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().background(whiteColor, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {

        AsyncImage(
            onError = {
                co.touchlab.kermit.Logger.d("KermitLogger $it")
            },
            model = booking.poojaImage.toDevomImage(),
            modifier = Modifier.size(104.dp, 115.dp).clip(RoundedCornerShape(12.dp)),
        )
        Column(modifier = Modifier.weight(1f).padding(vertical = 12.dp)) {
            BookingUserDetail(
                isDetails = isDetails,
                booking = booking,
                showStatus = showStatus,
                onReviewClick = onReviewClick,
                onCancelBooking = onCancelBooking
            )
            BookingId(booking = booking)
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = greyColor.copy(.24f),
                thickness = 1.dp
            )
            BookingPoojaDetails(booking = booking)
        }
    }
}

@Composable
fun BookingUserDetail(
    booking: GetBookingsResponse,
    showStatus: Boolean,
    onCancelBooking: () -> Unit = {},
    onReviewClick: () -> Unit = {},
    isDetails: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            maxLines = 1,
            overflow = TextOverflow.Visible,
            color = Color.Black,
            text = booking.poojaName.ifEmpty { "N/A" },
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f)
        )
        val contentColor = booking.status.getColor()

        if (showStatus) {
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

        val excludedStatuses = listOf(
            ApplicationStatus.REJECTED.status,
            ApplicationStatus.CANCELLED.status,
            ApplicationStatus.STARTED.status
        )

        val (icon, onClick) = when {
            booking.status == ApplicationStatus.COMPLETED.status -> {
                Res.drawable.ic_invoice to onReviewClick
            }
            booking.status !in excludedStatuses -> {
                Res.drawable.vertical_ellipsis to {
                    expanded = true
                }
            }
            else -> null to {}
        }

       if (isDetails.not()) {
           icon?.let {
               Image(
                   modifier = Modifier
                       .clickable(onClick = onClick)
                       .padding(end = 8.dp),
                   contentDescription = "",
                   painter = painterResource(icon),
               )
           }
       }

        BookingMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            onCancelClick = {
                onCancelBooking()
            }
        )

    }
}

@Composable
fun BookingId(booking: GetBookingsResponse) {
    Text(
        text = "#${booking.bookingCode}",
        fontWeight = FontWeight.W500,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        color = greyColor,
    )
}

@Composable
fun BookingPoojaDetails(booking: GetBookingsResponse) {
    Row {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "PANDIT NAME",
                fontWeight = FontWeight.W600,
                fontSize = 12.sp,
                style = text_style_lead_text,
                color = textBlackShade
            )
            Text(
                booking.panditName.ifEmpty { "N/A" },
                fontWeight = FontWeight.W500,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = greyColor
            )
        }
        Column(modifier = Modifier.padding(end = 16.dp)) {
            Text(
                "TIME",
                fontWeight = FontWeight.W600,
                fontSize = 12.sp,
                style = text_style_lead_text,
                color = textBlackShade
            )

            val time = booking.startTime.convertToAmPm()
            Text(
                text = time,
                fontWeight = FontWeight.W500,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = greyColor
            )
        }
    }
}

@Composable
fun BookingMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onCancelClick: () -> Unit,
) {
    DropdownMenu(
        shadowElevation = 5.dp,
        expanded = expanded,
        offset = DpOffset(x = (120).dp, y = 0.dp),
        onDismissRequest = onDismissRequest,
        modifier = Modifier.background(Color.White)
    ) {
        DropdownMenuItem(
            text = { Text("Cancel Booking") },
            onClick = {
                onCancelClick()
                onDismissRequest()
            }
        )
    }
}
