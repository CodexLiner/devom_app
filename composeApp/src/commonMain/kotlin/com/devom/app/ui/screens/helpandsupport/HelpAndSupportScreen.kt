package com.devom.app.ui.screens.helpandsupport

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.devom.app.theme.backgroundColor
import com.devom.app.theme.greenColor
import com.devom.app.theme.greyColor
import com.devom.app.theme.inputColor
import com.devom.app.theme.secondaryColor
import com.devom.app.theme.text_style_lead_text
import com.devom.app.theme.warningColor
import com.devom.app.theme.whiteColor
import com.devom.app.ui.components.AppBar
import com.devom.app.ui.components.ButtonPrimary
import com.devom.app.ui.components.NoContentView
import com.devom.app.ui.navigation.Screens
import com.devom.app.utils.formatStatus
import com.devom.app.utils.to12HourTime
import com.devom.models.helpandsupport.GetAllTicketsResponse
import com.devom.utils.date.convertIsoToDate
import com.devom.utils.date.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.create_request
import devom_app.composeapp.generated.resources.create_support_request
import devom_app.composeapp.generated.resources.ic_arrow_left

@Composable
fun HelpAndSupportScreen(navController: NavHostController) {
    val viewModel: HelpAndSupportViewModel = viewModel {
        HelpAndSupportViewModel()
    }
    Column(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        AppBar(
            navigationIcon = painterResource(Res.drawable.ic_arrow_left),
            title = "Help & Support",
            onNavigationIconClick = { navController.popBackStack() }
        )
        HelpAndSupportScreenContent(viewModel, navController)
    }
}

@Composable
fun ColumnScope.HelpAndSupportScreenContent(
    viewModel: HelpAndSupportViewModel,
    navController: NavHostController,
) {
    val tickets = viewModel.tickets.collectAsState()
    val showSheet = remember { mutableStateOf(false) }

    if (tickets.value.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = (PaddingValues(horizontal = 16.dp, vertical = 24.dp))
        ) {
            items(tickets.value) {
                HelpAndSupportTicketItem(it) {
                    navController.navigate(Screens.HelpAndSupportDetailScreen.path.plus("/${it.ticketId}"))
                }
            }
        }
    } else NoContentView(
        image = null,
        title = null,
        message = "No tickets found"
    )

    ButtonPrimary(
        buttonText = stringResource(Res.string.create_request),
        fontStyle = text_style_lead_text,
        modifier = Modifier.fillMaxWidth().navigationBarsPadding().height(58.dp)
            .padding(horizontal = 16.dp),
        onClick = {
            showSheet.value = true
        }
    )

    CreateNewTicketSheet(
        title = stringResource(Res.string.create_support_request),
        onDismiss = {
            showSheet.value = false
        },
        showSheet = showSheet.value,
        onClick = {
            viewModel.createTicket(it)
            showSheet.value = false

        }
    )
}

@Composable
fun HelpAndSupportTicketItem(ticketRequest: GetAllTicketsResponse, onClick: () -> Unit) {
    val chipColor = when (ticketRequest.status.lowercase()) {
        "in_progress" -> greenColor
        "open", "pending" -> warningColor
        else -> secondaryColor
    }

    val statusLabel = ticketRequest.status.formatStatus()
    val ticketSubject = ticketRequest.subject
    val ticketId = "#${ticketRequest.ticketCode.substringAfter("-")}"
    val createdAt = ticketRequest.createdAt.convertIsoToDate()?.toLocalDateTime()
    val time = createdAt?.time?.to12HourTime().orEmpty()
    val date = createdAt?.date.toString()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(whiteColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp).clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TicketInfoSection(subject = ticketSubject, ticketId = ticketId)

        Spacer(modifier = Modifier.width(16.dp))

        TicketStatusSection(
            status = statusLabel,
            chipColor = chipColor,
            date = date
        )
    }
}

@Composable
private fun RowScope.TicketInfoSection(subject: String, ticketId: String) {
    Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = subject,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = Color.Black
        )

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = inputColor)) {
                    append("Ticket ID : ")
                }
                append(ticketId)
            },
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = greyColor
        )
    }
}

@Composable
private fun TicketStatusSection(status: String, chipColor: Color, date: String) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center
    ) {
        StatusChip(text = status, color = chipColor)

        Text(
            text = date,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = greyColor
        )
    }
}

@Composable
fun StatusChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.08f),
                shape = RoundedCornerShape(30.dp)
            )
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            text = text,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = color
        )
    }
}


