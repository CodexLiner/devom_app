package com.devom.app.ui.screens.notification

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.devom.app.theme.greyColor
import com.devom.app.theme.primaryColor
import com.devom.app.theme.textBlackShade
import com.devom.app.ui.components.AppBar
import com.devom.app.ui.components.NoContentView
import com.devom.models.notification.GetNotificationResponse
import org.jetbrains.compose.resources.painterResource
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.ic_arrow_left
import devom_app.composeapp.generated.resources.ic_notification

@Composable
fun NotificationScreen(navHostController: NavHostController) {
    val viewModel: NotificationViewModel = viewModel {
        NotificationViewModel()
    }
    val notifications = viewModel.notifications.collectAsState()
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        AppBar(
            navigationIcon = painterResource(Res.drawable.ic_arrow_left),
            title = "Notifications",
            onNavigationIconClick = { navHostController.popBackStack() }
        )
        NotificationScreenContent(notifications.value)
    }
}

@Composable
private fun NotificationScreenContent(list: List<GetNotificationResponse>) {
    if (list.isNotEmpty()) LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(list) {
            NotificationItem(it)
        }
    } else NoContentView()
}

@Composable
fun NotificationItem(notification: GetNotificationResponse, showDivider: Boolean = true) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        NotificationContent(
            imageUrl = "https://picsum.photos/200",
            title = notification.title,
            message = notification.message,
            timestamp = notification.createdAt
        )

        if (showDivider) {
            HorizontalDivider(color = greyColor.copy(alpha = 0.24f), thickness = 1.dp)
        }
    }
}

@Composable
private fun NotificationContent(
    imageUrl: String,
    title: String,
    message: String,
    timestamp: String,
) {
    Row(
        modifier = Modifier.heightIn(max = 200.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        NotificationImage(imageUrl)
        NotificationTextContent(title, message, timestamp)
    }
}

@Composable
private fun NotificationImage(imageUrl: String) {
    Image(
        painter = painterResource(Res.drawable.ic_notification),
        contentDescription = null,
        modifier = Modifier.size(44.dp)
            .background(color = primaryColor, shape = RoundedCornerShape(16.dp)).padding(10.dp)
    )
}

@Composable
private fun NotificationTextContent(
    title: String,
    message: String,
    timestamp: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.W600, fontSize = 14.sp)) {
                    append(title.capitalize(Locale.current))
                }
                append(": ")
                withStyle(SpanStyle(fontWeight = FontWeight.W500, fontSize = 14.sp)) {
                    append(message)
                }
            }, color = textBlackShade, lineHeight = 18.sp
        )

        Text(
            text = timestamp, color = greyColor, fontSize = 12.sp, fontWeight = FontWeight.W600
        )
    }
}
