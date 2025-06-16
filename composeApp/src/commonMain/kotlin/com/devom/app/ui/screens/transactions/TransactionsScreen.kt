package com.devom.app.ui.screens.transactions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.devom.app.theme.backgroundColor
import com.devom.app.theme.blackColor
import com.devom.app.theme.greenColor
import com.devom.app.theme.greyColor
import com.devom.app.theme.primaryColor
import com.devom.app.theme.textBlackShade
import com.devom.app.theme.text_style_lead_text
import com.devom.app.theme.whiteColor
import com.devom.app.ui.components.AppBar
import com.devom.app.ui.components.NoContentView
import com.devom.app.ui.components.StatusTabRow
import com.devom.app.utils.to12HourTime
import com.devom.app.utils.toRupay
import com.devom.models.payment.TransactionSource
import com.devom.models.payment.TransactionType
import com.devom.models.payment.WalletTransaction
import com.devom.utils.date.convertIsoToDate
import com.devom.utils.date.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pandijtapp.composeapp.generated.resources.Res
import pandijtapp.composeapp.generated.resources.bonus_added
import pandijtapp.composeapp.generated.resources.earnings
import pandijtapp.composeapp.generated.resources.ic_arrow_left
import pandijtapp.composeapp.generated.resources.ic_wallet_bonus
import pandijtapp.composeapp.generated.resources.ic_wallet_credit
import pandijtapp.composeapp.generated.resources.ic_wallet_debit
import pandijtapp.composeapp.generated.resources.my_transactions
import pandijtapp.composeapp.generated.resources.no_transactions_found
import pandijtapp.composeapp.generated.resources.payment_received
import pandijtapp.composeapp.generated.resources.successful
import pandijtapp.composeapp.generated.resources.withdrawals

@Composable
fun TransactionsScreen(navController: NavController) {
    val viewModel: TransactionsScreenViewModel = viewModel {
        TransactionsScreenViewModel()
    }
    Column(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        AppBar(
            navigationIcon = painterResource(Res.drawable.ic_arrow_left),
            title = stringResource(Res.string.my_transactions),
            onNavigationIconClick = { navController.popBackStack() }
        )
        TransactionsScreenContent(navController, viewModel)
    }
}

@Composable
fun TransactionsScreenContent(
    navController: NavController,
    viewModel: TransactionsScreenViewModel,
) {
    val transactions = viewModel.transactions.collectAsState()
    val tabs = listOf(stringResource(Res.string.earnings), stringResource(Res.string.withdrawals))
    var selectedTabIndex = remember { mutableStateOf(0) }

    val filteredTransaction = when(selectedTabIndex.value) {
        0 -> transactions.value.transactions.filter { it.purpose != TransactionType.WITHDRAWAL.status }
        1 -> transactions.value.transactions.filter { it.purpose == TransactionType.WITHDRAWAL.status }
        else -> transactions.value.transactions
    }

    StatusTabRow(selectedTabIndex, tabs)
    if (filteredTransaction.isNotEmpty()) TransactionDetailContent(filteredTransaction) else NoContentView(
        message = stringResource(Res.string.no_transactions_found),
        image = null,
        title = null
    )
}

@Composable
fun TransactionDetailContent(transactions: List<WalletTransaction>) {
    val groupedTransactions = transactions.groupBy { it.createdAt }
    LazyColumn(
        contentPadding = PaddingValues(bottom = 200.dp)
    ) {
        groupedTransactions.forEach { (date, transactions) ->
            item {
                TransactionDateHeader(date)
            }
            items(transactions) { transaction ->
                TransactionItem(transaction)
            }

        }

    }
}

@Composable
fun TransactionItem(transaction: WalletTransaction) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().background(whiteColor)
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        TransactionTypeIcon(transaction)
        TransactionInfoCard(transaction)
        Text(text = transaction.amount.toRupay(), color = blackColor, style = text_style_lead_text)
    }
}

@Composable
fun TransactionDateHeader(title: String) {
    Text(
        modifier = Modifier.padding(top = 16.dp, start = 16.dp),
        text = title.convertIsoToDate()?.toLocalDateTime()?.date.toString(),
        color = greyColor,
        fontSize = 14.sp,
        fontWeight = FontWeight.W500
    )
}

@Composable
private fun RowScope.TransactionInfoCard(transaction: WalletTransaction) {
    val transactionTime =
        transaction.createdAt.convertIsoToDate()?.toLocalDateTime()?.time?.to12HourTime().orEmpty()

    val isCredit = transaction.type == TransactionType.CREDIT.status
    val isBonus = transaction.source == TransactionSource.BONUS_WALLET.source

    val title = when {
        isCredit && isBonus -> Res.string.bonus_added
        isCredit -> Res.string.payment_received
        else -> Res.string.successful
    }

    Column(modifier = Modifier.weight(1f)) {
        Text(
            text = stringResource(title),
            color = textBlackShade,
            fontWeight = FontWeight.W600,
            fontSize = 14.sp
        )

        Text(
            text = transactionTime,
            color = greyColor.copy(.6f),
            fontWeight = FontWeight.W600,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun TransactionTypeIcon(
    transaction: WalletTransaction,
) {
    val isCredit = transaction.type == TransactionType.CREDIT.status
    val isBonus = transaction.source == TransactionSource.BONUS_WALLET.source

    val (icon, cardColor) = when {
        isCredit && isBonus -> Res.drawable.ic_wallet_bonus to greenColor
        isCredit -> Res.drawable.ic_wallet_credit to primaryColor
        isCredit.not() && isBonus.not() -> Res.drawable.ic_wallet_debit to greenColor
        else -> Res.drawable.ic_wallet_debit to greenColor
    }

    Image(
        painter = painterResource(icon),
        contentDescription = null,
        colorFilter = ColorFilter.tint(cardColor),
        modifier = Modifier
            .background(cardColor.copy(alpha = 0.08f), shape = RoundedCornerShape(12.dp))
            .padding(10.dp)
    )
}
