package com.devom.app.ui.screens.helpandsupport

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.devom.app.theme.backgroundColor
import com.devom.app.theme.greenColor
import com.devom.app.theme.greyColor
import com.devom.app.theme.secondaryColor
import com.devom.app.theme.textBlackShade
import com.devom.app.theme.text_style_lead_text
import com.devom.app.theme.warningColor
import com.devom.app.theme.whiteColor
import com.devom.app.ui.components.AppBar
import com.devom.app.ui.components.AsyncImage
import com.devom.app.utils.toDevomImage
import com.devom.utils.date.convertIsoToDate
import com.devom.utils.date.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import pandijtapp.composeapp.generated.resources.Res
import pandijtapp.composeapp.generated.resources.ic_arrow_left

@Composable
fun HelpAndSupportDetailScreen(navController: NavHostController, ticketId: String) {
    val viewModel: HelpAndSupportViewModel = viewModel {
        HelpAndSupportViewModel()
    }
    LaunchedEffect(Unit) {
        viewModel.getTicketDetails(ticketId)
    }
    Column(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        AppBar(
            navigationIcon = painterResource(Res.drawable.ic_arrow_left),
            title = "Ticket details",
            onNavigationIconClick = { navController.popBackStack() })
        HelpAndSupportDetailScreenContent(viewModel)
    }
}

@Composable
fun HelpAndSupportDetailScreenContent(viewModel: HelpAndSupportViewModel) {
    val ticket = viewModel.ticketDetails.collectAsState().value
    ticket?.let {
        val createdAt = ticket.createdAt.convertIsoToDate()?.toLocalDateTime()
        val date = createdAt?.date.toString()
        val chipColor = when (ticket.status.lowercase()) {
            "in_progress" -> greenColor
            "open", "pending" -> warningColor
            else -> secondaryColor
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 16.dp , end = 16.dp, top = 24.dp , bottom = 200.dp)
        ) {
            item {
                TicketHeader(ticketCode = ticket.ticketCode, date = date)
            }
            item {
                TicketDetailsCard(
                    subject = ticket.subject,
                    status = ticket.status,
                    description = ticket.message,
                    response = ticket.response,
                    chipColor = chipColor,
                    imageUrl = ticket.image.toDevomImage()
                )
            }
        }
    }
}

@Composable
fun TicketHeader(ticketCode: String, date: String) {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "#$ticketCode", fontWeight = FontWeight.W600, fontSize = 16.sp
        )
        Text(
            text = date, fontWeight = FontWeight.Medium, fontSize = 12.sp, color = greyColor
        )
    }
}

@Composable
fun TicketDetailsCard(
    subject: String?,
    status: String,
    description: String?,
    chipColor: Color,
    imageUrl: String?,
    response: String?,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp).background(whiteColor).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TicketInformationFields(title = "Subject", value = subject)
            StatusChip(text = status, color = chipColor)
        }

        TicketInformationFields(title = "Description", value = description)
        TicketInformationFields(title = "Response by Devom", value = response?:"".ifBlank { "-" })
        if (imageUrl.isNullOrEmpty().not()) TicketImage(imageUrl)
    }
}

@Composable
fun TicketImage(imageUrl: Any?) {
    Text(
        text = "Image", fontWeight = FontWeight.W500, fontSize = 14.sp, color = greyColor
    )

    AsyncImage(
        model = imageUrl,
        modifier = Modifier.fillMaxWidth().height(228.dp)
    )
}

@Composable
fun TicketInformationFields(title: String, value: String?) {
    Column {
        Text(
            text = title, fontWeight = FontWeight.W500, fontSize = 14.sp, color = greyColor
        )
        Text(
            text = value.orEmpty(), style = text_style_lead_text, color = textBlackShade
        )
    }
}
