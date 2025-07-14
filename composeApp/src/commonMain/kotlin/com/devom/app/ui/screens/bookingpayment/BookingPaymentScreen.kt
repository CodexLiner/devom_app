package com.devom.app.ui.screens.bookingpayment

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import co.touchlab.kermit.Logger
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
import com.devom.app.ui.components.ButtonPrimary
import com.devom.app.ui.components.TextInputField
import com.devom.app.ui.navigation.Screens
import com.devom.models.pooja.GetPoojaResponse
import com.devom.models.slots.BookPanditSlotInput
import com.devom.models.slots.GetAllPanditByPoojaIdResponse
import com.devom.network.getUser
import com.devom.utils.Application
import com.devom.utils.date.convertIsoToDate
import com.devom.utils.date.toLocalDateTime
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.book_now
import devom_app.composeapp.generated.resources.booking_confirmation
import devom_app.composeapp.generated.resources.ic_arrow_left
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import me.meenagopal24.sdk.PaymentSheet
import me.meenagopal24.sdk.models.ConfigOptions
import me.meenagopal24.sdk.models.DisplayOptions
import me.meenagopal24.sdk.models.PreferencesOptions
import me.meenagopal24.sdk.models.PrefillOptions
import me.meenagopal24.sdk.models.RazorpayCheckoutOptions
import me.meenagopal24.sdk.models.ThemeOptions
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun BookingPaymentScreen(
    navHostController: NavHostController,
    input: BookPanditSlotInput?,
    pandit: GetAllPanditByPoojaIdResponse?,
    pooja: GetPoojaResponse?,
) {
    val viewModel: BookingPaymentScreenViewModel = viewModel()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {

        AppBar(
            navigationIcon = painterResource(Res.drawable.ic_arrow_left),
            title = stringResource(Res.string.booking_confirmation),
            onNavigationIconClick = { navHostController.popBackStack() },
        )

        BookingPaymentScreenContent(
            viewModel = viewModel,
            navHostController = navHostController,
            pandit = pandit,
            input = input,
            pooja = pooja
        )
    }
}

@Composable
fun ColumnScope.BookingPaymentScreenContent(
    viewModel: BookingPaymentScreenViewModel,
    navHostController: NavHostController,
    pandit: GetAllPanditByPoojaIdResponse?,
    input: BookPanditSlotInput?,
    pooja: GetPoojaResponse?,
) {
    var selectedPaymentMode by remember { mutableStateOf("cash") }
    val balance by viewModel.walletBalance.collectAsState()

    val user = getUser()
    val bookingDate = input?.bookingDate?.convertIsoToDate()?.toLocalDateTime()?.date
    val today = Clock.System.now().toLocalDateTime().date

    val baseAmount = (pandit?.withItemPrice?.toFloatOrNull() ?: 1f).toInt() * 100

    val amount = if (bookingDate == today) {
        (baseAmount * 1.1).toInt()
    } else {
        baseAmount
    }
    val poojaName = pooja?.name.orEmpty()
    val itemIds = pooja?.items?.map { it.id }.orEmpty()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
    ) {
        PoojaDetailsSection(pooja, pandit, input , (amount / 100f).toString())
        PaymentDetailsSection(
            selectedMethod = selectedPaymentMode,
            onSelectionChanged = { selectedPaymentMode = it.lowercase() }
        )
    }

    ButtonPrimary(
        buttonText = if (selectedPaymentMode == "cash") {
            stringResource(Res.string.book_now)
        } else {
            "Pay ₹${amount / 100}"
        },
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .height(58.dp)
    ) {
        val bookSlot: () -> Unit = {
            input?.let {
                viewModel.bookPanditSlot(
                    input = it.copy(
                        totalAmount = amount / 100f,
                        userId = user.userId,
                        poojaId = pooja?.id ?: 0,
                        panditId = pandit?.userId ?: 0,
                        itemIds = itemIds
                    ),
                    selectedPaymentMode = selectedPaymentMode, poojaPrice = amount / 100f
                ) {
                    navHostController.navigate(Screens.BookingSuccess.path + "/$poojaName") {
                        popUpTo(Screens.PanditListScreen.path) {
                            inclusive = false
                        }
                    }
                }
            }
        }

        if (selectedPaymentMode.equals("wallet", true)) {
            if ((balance?.cashWallet?.toFloatOrNull() ?: 0f) >= (amount / 100)) {
                bookSlot()
                return@ButtonPrimary
            }

            viewModel.createTransaction(amount / 100, poojaName) { order ->
                PaymentSheet.startPayment(
                    RazorpayCheckoutOptions(
                        orderId = order.id,
                        currency = order.currency,
                        description = "Payment for pooja $poojaName",
                        prefill = PrefillOptions(
                            email = user.email,
                            contact = user.mobileNo,
                            name = user.fullName,
                        ),
                        theme = ThemeOptions(color = "#FF762233"),
                    )
                )
            }

            PaymentSheet.onSuccess = { id, data ->
                Logger.d("RazorpayID $id")
                Logger.d("RazorpayData $data")
                viewModel.verifyTransaction(data) {
                    bookSlot()
                }
            }

            PaymentSheet.onError = { _, _, data ->
                Application.showToast("Payment Failed ${data?.paymentId}")
            }

        } else {
            bookSlot()
        }
    }
}

@Composable
fun PaymentDetailsSection(
    selectedMethod: String,
    onSelectionChanged: (String) -> Unit,
) {
    val paymentOptions = listOf("Cash", "Wallet")
    Column(
        modifier = Modifier.padding(top = 24.dp).padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Payment Options", style = text_style_h5, color = blackColor)
        LazyVerticalGrid(
            columns = GridCells.Fixed(paymentOptions.size),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(paymentOptions) { option ->
                PaymentOption(
                    option = option,
                    isSelected = selectedMethod.equals(option, ignoreCase = true),
                    onClick = { onSelectionChanged(option) }
                )
            }
        }

    }
}

@Composable
fun PoojaDetailsSection(
    pooja: GetPoojaResponse?,
    pandit: GetAllPanditByPoojaIdResponse?,
    input: BookPanditSlotInput?,
    amount : String = ""
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(top = 16.dp).background(whiteColor)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            ItemPoojaDetail(
                title = "Pooja Type",
                description = pooja?.name.orEmpty(),
                modifier = Modifier.weight(1f)
            )
            ItemPoojaDetail(
                title = "Chosen Panditji",
                description = pandit?.fullName.orEmpty(),
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            ItemPoojaDetail(
                title = "Date & Time ",
                description = input?.bookingDate
                    ?.convertIsoToDate()
                    ?.toLocalDateTime()
                    ?.date.toString(),
                modifier = Modifier.weight(1f)
            )
            ItemPoojaDetail(
                title = "Service Charges",
                description = "₹${amount}/hr",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ItemPoojaDetail(title: String, description: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
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
fun PaymentOption(option: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = if (isSelected) primaryColor else greyColor,
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = if (isSelected) primaryColor.copy(alpha = 0.1f) else whiteColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = option,
            color = if (isSelected) primaryColor else textBlackShade,
            textAlign = TextAlign.Center
        )
    }
}
