package com.devom.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.touchlab.kermit.Logger
import com.devom.app.theme.blackColor
import com.devom.app.theme.greenColor
import com.devom.app.theme.greyColor
import com.devom.app.theme.inputColor
import com.devom.app.theme.textStyleBody2
import com.devom.app.theme.text_style_h4
import com.devom.app.theme.text_style_lead_body_1
import com.devom.app.theme.whiteColor
import com.devom.models.payment.WalletTransaction
import com.devom.utils.date.convertIsoToDate
import com.devom.utils.date.toLocalDateTime
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.painterResource
import pandijtapp.composeapp.generated.resources.Res
import pandijtapp.composeapp.generated.resources.ic_arrow_drop_down

val monthNames = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "June", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"
)

@Composable
fun EarningsBarChart(
    transactions: List<WalletTransaction>,
) {
    var selectedOption by remember { mutableStateOf("Week") }
    var expanded by remember { mutableStateOf(false) }
    val timeZone = TimeZone.currentSystemDefault()
    val totalEarning = remember { mutableStateOf("") }


    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp).background(whiteColor, RoundedCornerShape(12.dp)).padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Total Earning", style = text_style_lead_body_1, color = greyColor)
                Text("₹${totalEarning.value}", style = text_style_h4, color = blackColor)
            }

            OutlinedButton(
                contentPadding = PaddingValues(horizontal = 8.dp),
                border = BorderStroke(width = 1.dp, color = inputColor.copy(0.24f)),
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    expanded = true
                }) {
                Text(text = selectedOption, color = greyColor, style = text_style_lead_body_1)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_drop_down),
                    contentDescription = "Drop down arrow",
                    tint = inputColor.copy(0.54f)
                )

                DropdownMenu(
                    expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(text = { Text("Week") }, onClick = {
                        selectedOption = "Week"
                        expanded = false
                    })
                    DropdownMenuItem(text = { Text("Year") }, onClick = {
                        selectedOption = "Year"
                        expanded = false
                    })
                }
            }
        }
        when (selectedOption) {
            "Week" -> DisplayCurrentWeekChart(transactions, timeZone) {
                totalEarning.value = it.toString()
            }

            "Year" -> DisplayCurrentYearChart(transactions, timeZone) {
                totalEarning.value = it.toString()
            }
        }
    }

}


@Composable
fun DisplayCurrentYearChart(
    transactions: List<WalletTransaction>,
    timeZone: TimeZone = TimeZone.UTC,
    onSum: (Int) -> Unit = {},
) {
    val today = Clock.System.now().toLocalDateTime(timeZone).date
    val currentYear = today.year
    val currentYearTransactions = filterCurrentYear(transactions, timeZone)
    val monthlySums = sumByMonth(currentYearTransactions, currentYear)
    val months = monthlySums.map { it.first }
    val values = monthlySums.map { it.second }
    val sum = remember { values.sum() }
    LaunchedEffect(Unit , values) {
        onSum(sum)
    }
    Box(
        modifier = Modifier, contentAlignment = Alignment.Center
    ) {
        BarChart(labels = months, values = values)
    }
}

@Composable
fun DisplayCurrentWeekChart(
    transactions: List<WalletTransaction>,
    timeZone: TimeZone = TimeZone.UTC,
    onSum: (Int) -> Unit = {},
) {
    val today = Clock.System.now().toLocalDateTime(timeZone).date
    val startOfWeek = today.previousOrSame(DayOfWeek.MONDAY)
    val currentWeekTransactions = filterCurrentWeek(transactions, timeZone)
    val dailySums = sumByDay(currentWeekTransactions, startOfWeek)
    val weeks = dailySums.map { it.first }
    val values = dailySums.map { it.second }
    val sum = remember { values.sum() }
    LaunchedEffect(Unit , values) {
        onSum(sum)
    }
    Box(
        modifier = Modifier, contentAlignment = Alignment.Center
    ) {
        BarChart(labels = weeks, values = values)
    }
}


@Composable
fun BarChart(
    labels: List<String>,
    values: List<Int>,
    maxBarHeight: Dp = 200.dp,
) {
    val max = (values.maxOrNull() ?: 0).takeIf { it > 0 } ?: 1

    Row(
        modifier = Modifier.fillMaxWidth().height(maxBarHeight)
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            val numberOfTicks = 5
            val step = max / numberOfTicks
            val tickLabels = (0..numberOfTicks).map { (it * step) / 1000 }.reversed()
            for (label in tickLabels) {
                Logger.d("KermitLogs $label")
                Text(
                    color = inputColor,
                    style = textStyleBody2,
                    lineHeight = 16.sp,
                    text = "${label}k",
                    fontSize = 12.sp
                )
            }

            Text(
                lineHeight = 16.sp,
                text = "",
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.width(16.dp))

        Box(
            modifier = Modifier.fillMaxWidth().height(maxBarHeight)
                .horizontalScroll(rememberScrollState())
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                for (i in labels.indices) {
                    val barProportion = (values[i].toFloat() / max.toFloat()).coerceAtMost(1f)
                    val barHeight = barProportion * maxBarHeight.value

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.height(maxBarHeight)
                    ) {
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier.width(10.dp).height(barHeight.dp)
                                    .background(greenColor, RoundedCornerShape(4.dp))
                            )
                        }

                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = labels[i],
                            color = inputColor,
                            style = textStyleBody2,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}


fun filterCurrentWeek(
    transactions: List<WalletTransaction>,
    timeZone: TimeZone = TimeZone.UTC,
): List<WalletTransaction> {
    val today = Clock.System.now().toLocalDateTime(timeZone).date
    val startOfWeek = today.previousOrSame(DayOfWeek.MONDAY)
    val endOfWeek = today.nextOrSame(DayOfWeek.SUNDAY)

    return transactions.filter { transaction ->
        val transactionDate =
            transaction.createdAt.convertIsoToDate()?.toLocalDateTime()?.date ?: today
        (transactionDate == startOfWeek || transactionDate > startOfWeek) && (transactionDate == endOfWeek || transactionDate < endOfWeek)
    }
}

fun filterCurrentYear(
    transactions: List<WalletTransaction>,
    timeZone: TimeZone = TimeZone.UTC,
): List<WalletTransaction> {
    val today = Clock.System.now().toLocalDateTime(timeZone).date
    val currentYear = today.year

    return transactions.filter { transaction ->
        val transactionDate = transaction.createdAt.convertIsoToDate()?.toLocalDateTime()?.date
        transactionDate?.year == currentYear
    }
}

fun sumByMonth(transactions: List<WalletTransaction>, currentYear: Int): List<Pair<String, Int>> {
    return (1..12).map { month ->
        val sum = transactions.filter {
            it.createdAt.convertIsoToDate()
                ?.toLocalDateTime()?.month?.number == month && it.createdAt.convertIsoToDate()
                ?.toLocalDateTime()?.year == currentYear
        }.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }.toInt()
        val label = monthNames[month - 1]
        label to sum
    }
}

private fun LocalDate.previousOrSame(dayOfWeek: DayOfWeek): LocalDate {
    var current = this
    while (current.dayOfWeek != dayOfWeek) {
        current = current.minus(1, DateTimeUnit.DAY)
    }
    return current
}

fun sumByDay(
    transactions: List<WalletTransaction>,
    startOfWeek: LocalDate,
): List<Pair<String, Int>> {
    return (0..6).map { offset ->
        val day = startOfWeek.plus(offset.toLong(), DateTimeUnit.DAY) // e.g. Monday
        val sum =
            transactions.filter { it.createdAt.convertIsoToDate()?.toLocalDateTime()?.date == day }
                .sumOf { it.amount.toDoubleOrNull() ?: 0.0 }.toInt()

        day.dayOfWeek.toString().take(3) to sum
    }
}

private fun LocalDate.nextOrSame(dayOfWeek: DayOfWeek): LocalDate {
    var current = this
    while (current.dayOfWeek != dayOfWeek) {
        current = current.plus(1, DateTimeUnit.DAY)
    }
    return current
}
