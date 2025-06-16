package com.devom.app.ui.screens.booking.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devom.app.models.ApplicationStatus
import com.devom.app.theme.greenColor
import com.devom.app.theme.greyColor
import com.devom.app.theme.primaryColor
import com.devom.app.theme.secondaryColor
import com.devom.app.theme.textBlackShade
import com.devom.app.theme.text_style_h2
import com.devom.app.theme.text_style_lead_text
import com.devom.app.theme.whiteColor
import com.devom.app.ui.components.AsyncImage
import com.devom.app.utils.to12HourTime
import com.devom.app.utils.toDevomImage
import com.devom.models.slots.GetBookingsResponse
import com.devom.utils.date.convertIsoToDate
import com.devom.utils.date.toLocalDateTime
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.ic_check
import devom_app.composeapp.generated.resources.ic_close

@Composable
fun BookingCard(
    booking: GetBookingsResponse,
    onBookingUpdate: (ApplicationStatus) -> Unit ={},
    onClick: () -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().background(whiteColor, shape = RoundedCornerShape(12.dp)).clickable { onClick() }
    ) {

        AsyncImage(
            onError = {
                co.touchlab.kermit.Logger.d("KermitLogger $it")
            },
            model = booking.userImage.toDevomImage(),
            modifier = Modifier.size(112.dp, 139.dp).clip(RoundedCornerShape(12.dp)),
        )
        Column(modifier = Modifier.weight(1f).padding(vertical = 12.dp)) {
            BookingUserDetail(booking, onBookingUpdate)
            BookingUserContactDetail(booking = booking)
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = greyColor.copy(.24f), thickness = 1.dp)
            BookingPoojaDetails(booking = booking)
        }
    }
}

@Composable
fun BookingUserDetail(booking: GetBookingsResponse ,onBookingUpdate : (ApplicationStatus) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            color = Color.Black,
            text = booking.userName,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f)
        )

        if (booking.status == ApplicationStatus.PENDING.status) Row(modifier = Modifier.padding(end = 8.dp) , horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BookingConfirmationButton(onBookingUpdate)
        }
        else {
            val contentColor = if (booking.status == ApplicationStatus.CONFIRMED.status) greenColor else secondaryColor
            Box(modifier = Modifier.padding(end = 8.dp).background(contentColor.copy(0.08f), shape = RoundedCornerShape(50))) {
                Text(
                    modifier = Modifier.padding(vertical = 4.dp , horizontal = 8.dp),
                    text = booking.status.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                    color = contentColor,
                    fontWeight = FontWeight.W600,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                )
            }
        }
    }
}

@Composable
fun RowScope.BookingConfirmationButton(onBookingUpdate : (ApplicationStatus) -> Unit) {
    ConfirmationIcon(
        iconRes = Res.drawable.ic_check,
        tintColor = greenColor,
        backgroundColor = greenColor.copy(alpha = 0.08f)
    ) {
        onBookingUpdate(ApplicationStatus.CONFIRMED)
    }
    ConfirmationIcon(
        iconRes = Res.drawable.ic_close,
        tintColor = secondaryColor,
        backgroundColor = secondaryColor.copy(alpha = 0.08f)
    ) {
        onBookingUpdate(ApplicationStatus.REJECTED)
    }
}

@Composable
private fun RowScope.ConfirmationIcon(
    iconRes: DrawableResource,
    tintColor: Color,
    backgroundColor: Color,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .padding(start = 6.dp).clickable(onClick = onClick)
            .background(backgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(6.dp)
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            colorFilter = ColorFilter.tint(tintColor),
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
fun BookingUserContactDetail(booking: GetBookingsResponse) {
    Text(
        text = booking.mobileNo,
        fontWeight = FontWeight.W500,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        color = greyColor,
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier.weight(1f),
            text = booking.address.ifEmpty { "N/A" },
            fontWeight = FontWeight.W500,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            color = greyColor,
        )

        if (booking.status == ApplicationStatus.COMPLETED.status) Text(
            modifier = Modifier,
            text = "₹1101",
            fontSize = 14.sp,
            style = text_style_h2,
            color = primaryColor
        )

    }
}

@Composable
fun BookingPoojaDetails(booking: GetBookingsResponse) {
    Row {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "POOJA TYPE",
                fontWeight = FontWeight.W600,
                fontSize = 12.sp,
                style = text_style_lead_text,
                color = textBlackShade
            )
            Text(
                booking.poojaName.ifEmpty { "N/A" },
                fontWeight = FontWeight.W500,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = greyColor
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "DATE & TIME",
                fontWeight = FontWeight.W600,
                fontSize = 12.sp,
                style = text_style_lead_text,
                color = textBlackShade
            )

            val date =  booking.bookingDate.convertIsoToDate()?.toLocalDateTime()?.date.toString()
            val time = booking.bookingDate.convertIsoToDate()?.toLocalDateTime()?.time?.to12HourTime()
            Text(
                text = date.plus(" $time"),
                fontWeight = FontWeight.W500,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = greyColor
            )
        }
    }
}