package com.devom.app.ui.screens.bookingpayment

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.devom.app.theme.backgroundColor
import com.devom.app.theme.bgColor
import com.devom.app.theme.blackColor
import com.devom.app.theme.greyColor
import com.devom.app.theme.primaryColor
import com.devom.app.theme.textBlackShade
import com.devom.app.theme.text_style_h5
import com.devom.app.theme.text_style_lead_text
import com.devom.app.theme.whiteColor
import com.devom.app.ui.components.AppBar
import com.devom.app.ui.components.TextInputField
import com.devom.models.slots.BookPanditSlotInput
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.booking_confirmation
import devom_app.composeapp.generated.resources.ic_arrow_left
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun BookingPaymentScreen(navHostController: NavHostController, input: BookPanditSlotInput?) {
    val viewModel: BookingPaymentScreenViewModel = viewModel {
        BookingPaymentScreenViewModel()
    }
    LaunchedEffect(Unit) {

    }
    Column(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        AppBar(
            navigationIcon = painterResource(Res.drawable.ic_arrow_left),
            title = stringResource(Res.string.booking_confirmation),
            onNavigationIconClick = { navHostController.popBackStack() },
        )
        BookingPaymentScreenContent(viewModel, navHostController)

    }
}

@Composable
fun BookingPaymentScreenContent(
    viewModel: BookingPaymentScreenViewModel,
    navHostController: NavHostController,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        AddressSection()
        PoojaDetailsSection()
        PaymentDetailsSection()
        ApplyPromotionCode()
    }
}

@Composable
fun ApplyPromotionCode() {
    Column(
        modifier = Modifier.padding(top = 24.dp).padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(text = "Promo Code", style = text_style_h5, color = blackColor)
        Box(
            contentAlignment = Alignment.CenterEnd,
            modifier = Modifier.background(whiteColor, RoundedCornerShape(12.dp)),
        ) {
            TextInputField(
                backgroundColor = whiteColor,
                placeholder = "Enter Code",
                inputColor = greyColor
            )

            Text(
                modifier = Modifier.padding(end = 8.dp)
                    .background(blackColor, RoundedCornerShape(12.dp)).padding(
                    horizontal = 12.dp, vertical = 8.dp
                ),
                style = text_style_lead_text,
                text = "Apply",
                color = whiteColor
            )
        }
    }

}

@Composable
fun PaymentDetailsSection() {
    Column(
        modifier = Modifier.padding(top = 24.dp).padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val list = listOf<String>(
            "Online", "Cash", "Wallet"
        )
        Text(text = "Payment Options", style = text_style_h5, color = blackColor)
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(list) {
                PaymentOption(
                    option = it,
                    isSelected = it == "Online",
                    onClick = {}
                )
            }
        }

    }
}

@Composable
fun PoojaDetailsSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(top = 16.dp).background(whiteColor)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            ItemPoojaDetail(
                modifier = Modifier.weight(1f),
                title = "Pooja Type",
                description = "description"
            )
            ItemPoojaDetail(
                modifier = Modifier.weight(1f),
                title = "Chosen Panditji ",
                description = "description"
            )
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            ItemPoojaDetail(
                modifier = Modifier.weight(1f),
                title = "Date & Time ",
                description = "description"
            )
            ItemPoojaDetail(
                modifier = Modifier.weight(1f),
                title = "Service Charges ",
                description = "description"
            )
        }
    }
}

@Composable
fun ItemPoojaDetail(title: String, description: String, modifier: Modifier) {
    Column(modifier.fillMaxWidth()) {
        Text(
            text = title.uppercase(),
            fontWeight = FontWeight.W500,
            fontSize = 12.sp,
            color = greyColor
        )
        Text(
            text = description,
            fontWeight = FontWeight.W600,
            fontSize = 14.sp,
            color = textBlackShade
        )
    }
}

@Composable
fun AddressSection() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().background(
            whiteColor
        ).padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(text = "3891 Tirupati palace, Indore M.P.")
        Text(
            text = "Change",
            modifier = Modifier.background(bgColor, RoundedCornerShape(12.dp)).padding(
                horizontal = 12.dp, vertical = 4.dp
            )
        )
    }
}


@Composable
fun PaymentOption(
    option: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Text(
        text = option,
        textAlign = TextAlign.Center,
        style = text_style_h5,
        color = if (isSelected) primaryColor else greyColor,
        modifier = Modifier
            .border(
                width = 1.dp,
                color = if (isSelected) primaryColor.copy(0.5f) else greyColor.copy(0.5f),
                shape = RoundedCornerShape(24.dp)
            )
            .background(
                color = if (isSelected) primaryColor.copy(0.08f) else Color.Transparent,
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 20.dp)
    )
}


