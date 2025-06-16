package com.devom.app.ui.screens.wallet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
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
import com.devom.app.theme.textStyleBody2
import com.devom.app.theme.text_style_h4
import com.devom.app.theme.text_style_lead_text
import com.devom.app.theme.whiteColor
import com.devom.app.ui.components.AppBar
import com.devom.app.ui.navigation.Screens
import com.devom.app.utils.toColor
import com.devom.models.payment.WalletBalance
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pandijtapp.composeapp.generated.resources.Add_Account
import pandijtapp.composeapp.generated.resources.Res
import pandijtapp.composeapp.generated.resources.Withdraw
import pandijtapp.composeapp.generated.resources.arrow_drop_down_right
import pandijtapp.composeapp.generated.resources.bring_your_friends_on_devom_and_earn_rewards
import pandijtapp.composeapp.generated.resources.current_balance
import pandijtapp.composeapp.generated.resources.ic_nav_wallet
import pandijtapp.composeapp.generated.resources.ic_refer
import pandijtapp.composeapp.generated.resources.ic_transactions
import pandijtapp.composeapp.generated.resources.invite_and_collect
import pandijtapp.composeapp.generated.resources.my_transactions
import pandijtapp.composeapp.generated.resources.my_wallet
import pandijtapp.composeapp.generated.resources.view_and_track_your_payments_and_transactions
import pandijtapp.composeapp.generated.resources.withdrawals

@Composable
fun WalletScreen(navHostController: NavHostController, onNavigationIconClick: () -> Unit) {
    val viewModel: WalletViewModel = viewModel {
        WalletViewModel()
    }
    Column(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        AppBar(
            title = stringResource(Res.string.my_wallet),
            onNavigationIconClick = onNavigationIconClick
        )
        WalletScreenContent(navHostController, viewModel)
    }
}

@Composable
fun WalletScreenContent(navHostController: NavHostController, viewModel: WalletViewModel) {
    WalletDetailsContent(navHostController, viewModel)
}

@Composable
fun WalletDetailsContent(navController: NavHostController, viewModel: WalletViewModel) {
    val balance = viewModel.walletBalances.collectAsState()
    val bankDetails = viewModel.bankDetails.collectAsState()
    Box(modifier = Modifier.fillMaxWidth().background(primaryColor)) {
        WalletHeader(
            balance.value.balance, if (bankDetails.value == null) stringResource(Res.string.Add_Account)
            else stringResource(Res.string.Withdraw)
        ) {
            navController.navigate(Screens.BankAccountScreen.path)
        }
    }
    WalletBreakdownRow(balance.value.balance)
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        WalletActionItem(
            painter = painterResource(Res.drawable.ic_transactions),
            text = stringResource(Res.string.my_transactions),
            description = stringResource(Res.string.view_and_track_your_payments_and_transactions)
        ) {
            navController.navigate(Screens.Transactions.path)
        }

        WalletActionItem(
            painter = painterResource(Res.drawable.ic_refer),
            text = stringResource(Res.string.invite_and_collect),
            description = stringResource(Res.string.bring_your_friends_on_devom_and_earn_rewards)
        ) {
            navController.navigate(Screens.ReferAndEarn.path)
        }
    }
}

@Composable
private fun WalletHeader(
    balance: WalletBalance,
    buttonText: String = stringResource(Res.string.Add_Account),
    onClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp)
            .background(bgColor, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .border(
                width = 1.dp,
                color = whiteColor,
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            )
            .padding(start = 16.dp , top = 16.dp , bottom = 16.dp)
    ) {
        WalletIcon()
        WalletBalanceInfo(balance)
        WithdrawButton(buttonText, onClick)
    }
}

@Composable
private fun WalletIcon() {
    Image(
        painter = painterResource(Res.drawable.ic_nav_wallet),
        contentDescription = null,
        modifier = Modifier
            .background(whiteColor, RoundedCornerShape(12.dp))
            .padding(10.dp)
    )
}

@Composable
private fun RowScope.WalletBalanceInfo(balance: WalletBalance) {
    Column(modifier = Modifier.weight(1f)) {
        Text(
            text = stringResource(Res.string.current_balance),
            color = greyColor,
            fontWeight = FontWeight.W400,
            fontSize = 14.sp
        )
        val currentBalance =(balance.cashWallet.toFloatOrNull() ?: 0f) + (balance.bonusWallet.toFloatOrNull() ?: 0f)

        Text(
            text = "₹${currentBalance}",
            color = blackColor,
            style = text_style_h4
        )
    }
}

@Composable
private fun WithdrawButton(
    buttonText: String = stringResource(Res.string.Add_Account),
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        content = {
            Text(
                modifier = Modifier.background(blackColor, RoundedCornerShape(12.dp)).padding(vertical = 10.dp, horizontal = 8.dp),
                text = buttonText,
                color = whiteColor,
                style = text_style_lead_text,
            )
        }
    )
}

@Composable
private fun WalletBreakdownRow(balance: WalletBalance) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(whiteColor, RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
            .border(
                width = 0.5f.dp,
                color = greyColor.copy(.24f),
                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
            )
    ) {
        val cashBalance = if (balance.cashWallet.isEmpty()) "-" else "₹${balance.cashWallet}"
        val bonusBalance = if (balance.bonusWallet.isEmpty()) "-" else "₹${balance.bonusWallet}"
        AvailableCashTypeItem(title = "Cash Amount", amount = cashBalance)
        AvailableCashTypeItem(title = "Cash Bonus ", amount = bonusBalance)
    }
}


@Composable
fun RowScope.AvailableCashTypeItem(title: String, amount: String = "") {
    Column(
        modifier = Modifier.weight(1f).padding(vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.W500,
            fontSize = 14.sp,
            color = greyColor
        )

        Text(
            text = amount,
            fontWeight = FontWeight.W600,
            fontSize = 16.sp,
            color = blackColor
        )
    }
}

@Composable
fun WalletActionItem(
    painter: Painter,
    text: String,
    description: String = "",
    onClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(18.dp),
        modifier = Modifier.fillMaxWidth()
            .background(whiteColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                colorFilter = ColorFilter.tint(greyColor)
            )
            Column(
                modifier = Modifier.padding(start = 16.dp).weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(text = text, style = text_style_lead_text, color = textBlackShade)
                Text(text = description, style = textStyleBody2, color = greyColor)
            }
            Image(
                painter = painterResource(Res.drawable.arrow_drop_down_right),
                contentDescription = null,
            )
        }
    }
}