package com.devom.app.ui.screens.booking.details

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.devom.app.theme.backgroundColor
import com.devom.app.theme.greyColor
import com.devom.app.theme.primaryColor
import com.devom.app.theme.textBlackShade
import com.devom.app.theme.text_style_h5
import com.devom.app.theme.text_style_lead_body_1
import com.devom.app.theme.text_style_lead_text
import com.devom.app.theme.whiteColor
import com.devom.app.ui.components.AppBar
import com.devom.app.ui.components.ButtonPrimary
import com.devom.app.ui.components.DropDownItem
import com.devom.app.ui.screens.booking.BookingViewModel
import com.devom.app.ui.screens.booking.components.BookingCard
import com.devom.app.ui.screens.booking.components.SelectPoojaItemBottomSheet
import com.devom.app.ui.screens.booking.components.StartEndPoojaSheet
import com.devom.models.slots.BookingItem
import com.devom.models.slots.GetBookingsResponse
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pandijtapp.composeapp.generated.resources.Res
import pandijtapp.composeapp.generated.resources.enter_pin_visible_on_customer_app
import pandijtapp.composeapp.generated.resources.ic_arrow_left
import pandijtapp.composeapp.generated.resources.ic_check
import pandijtapp.composeapp.generated.resources.ic_plus
import pandijtapp.composeapp.generated.resources.pooja_samgri_list
import pandijtapp.composeapp.generated.resources.select_pooja_item
import pandijtapp.composeapp.generated.resources.start_pooja
import pandijtapp.composeapp.generated.resources.submit
import pandijtapp.composeapp.generated.resources.verification_pooja_start

@Composable
fun BookingDetailScreen(navController: NavController, bookingId: String?) {
    val viewModel: BookingViewModel = viewModel { BookingViewModel() }
    val booking = viewModel.bookingDetailItem.collectAsState()
    val showSheet = remember { mutableStateOf(false) }

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
            BookingDetailScreenContent(navController, it, viewModel)
            if (booking.value?.status !in listOf(
                    ApplicationStatus.COMPLETED.status,
                    ApplicationStatus.REJECTED.status,
                    ApplicationStatus.CANCELLED.status,
                    ApplicationStatus.PENDING.status
                )
            ) ButtonPrimary(
                modifier = Modifier.fillMaxWidth().navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp).height(58.dp),
                buttonText = stringResource(Res.string.start_pooja)
            ) {
                showSheet.value = true
            }

            if (showSheet.value) {
                StartEndPoojaSheet(
                    showSheet = showSheet.value,
                    title = stringResource(Res.string.verification_pooja_start),
                    message = stringResource(Res.string.enter_pin_visible_on_customer_app),
                    buttonText = stringResource(Res.string.submit),
                    onDismiss = {
                        showSheet.value = false
                    },
                    onOtpEntered = {}
                )
            }
        }
    }
}

@Composable
fun ColumnScope.BookingDetailScreenContent(
    navController: NavController,
    booking: GetBookingsResponse,
    viewModel: BookingViewModel,
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
    ) {
        item {
            BookingCard(
                booking = booking,
                onBookingUpdate = { viewModel.updateBookingStatus(booking.bookingId, it) }
            )
        }

        item {
            BookingSamagriHeader(booking, viewModel)
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
            ) {
                viewModel.removePoojaItem(item.id.toString(), booking)
            }
            if (index < booking.bookingItems.size - 1) HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = greyColor.copy(.24f),
                thickness = 1.dp
            )
        }
    }
}

@Composable
fun BookingSamagriHeader(booking: GetBookingsResponse, viewModel: BookingViewModel) {
    val poojaItems = viewModel.poojaItems.collectAsState()
    val dropDownState = remember { mutableStateOf(false) }
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
        if (booking.status !in listOf(
                ApplicationStatus.COMPLETED.status,
                ApplicationStatus.REJECTED.status,
                ApplicationStatus.CANCELLED.status,
            )
        ) Image(
            modifier = Modifier.size(24.dp).clickable {
                dropDownState.value = true
            },
            painter = painterResource(Res.drawable.ic_plus),
            contentDescription = null
        )
    }

    SelectPoojaItemBottomSheet(
        showSheet = dropDownState.value,
        title = stringResource(Res.string.select_pooja_item),
        items = poojaItems.value.map { DropDownItem(it.name, it.id.toString()) },
        onDismiss = {
            dropDownState.value = false
        },
        onClick = {
            viewModel.addPoojaItem(it.id, booking)
        }
    )
}

@Composable
fun SamagriItemRow(modifier: Modifier = Modifier, item: BookingItem, onItemClick: () -> Unit = {}) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SamagriCheckbox(onClick = onItemClick)
        Text(
            style = text_style_lead_body_1,
            text = item.name,
            fontWeight = FontWeight.W500,
            color = textBlackShade,
            modifier = Modifier.padding(start = 12.dp).weight(1f)
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
fun SamagriCheckbox(
    modifier: Modifier = Modifier,
    borderColor: Color = primaryColor,
    checkmarkColor: Color = primaryColor,
    size: Dp = 20.dp,
    cornerRadius: Dp = 4.dp,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .size(size)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(cornerRadius))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_check),
            contentDescription = null,
            tint = checkmarkColor,
            modifier = Modifier.size(size * 0.6f)
        )
    }
}


