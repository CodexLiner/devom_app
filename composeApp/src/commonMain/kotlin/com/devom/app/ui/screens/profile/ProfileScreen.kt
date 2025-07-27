package com.devom.app.ui.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.devom.app.AuthManager
import com.devom.app.NOTIFICATION_PERMISSION_GRANTED
import com.devom.app.models.ApplicationStatus
import com.devom.app.settings
import com.devom.app.theme.backgroundColor
import com.devom.app.theme.blackColor
import com.devom.app.theme.greyColor
import com.devom.app.theme.primaryColor
import com.devom.app.theme.text_style_h5
import com.devom.app.theme.whiteColor
import com.devom.app.ui.components.AppBar
import com.devom.app.ui.components.AsyncImage
import com.devom.app.ui.navigation.Screens
import com.devom.app.utils.toDevomImage
import com.devom.models.auth.UserRequestResponse
import com.devom.utils.Application
import devom_app.composeapp.generated.resources.Notifications
import devom_app.composeapp.generated.resources.Preferences
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.arrow_drop_down_right
import devom_app.composeapp.generated.resources.ic_edit
import devom_app.composeapp.generated.resources.ic_logout
import devom_app.composeapp.generated.resources.ic_verified
import devom_app.composeapp.generated.resources.update_profile
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navHostController: NavHostController,
    onUpdate: () -> Unit = {},
    onNavigationIconClick: () -> Unit = {},
) {
    val viewModel = viewModel<ProfileViewModel> {
        ProfileViewModel()
    }
    val user by viewModel.user.collectAsStateWithLifecycle()

    var notificationsEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getUserProfile()
        onUpdate()
        notificationsEnabled = settings.getBoolean(NOTIFICATION_PERMISSION_GRANTED, false)
    }

    LaunchedEffect(user) {
        onUpdate()
    }

    LaunchedEffect(notificationsEnabled) {
        settings.putBoolean(NOTIFICATION_PERMISSION_GRANTED, notificationsEnabled)
    }

    Column(
        modifier = Modifier.fillMaxSize().background(backgroundColor)
    ) {

        AppBar(
            title = "Profile", onNavigationIconClick = onNavigationIconClick, actions = {
                IconButton(onClick = {
                    AuthManager.logout()
                }) {
                    Icon(
                        painterResource(Res.drawable.ic_logout),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 200.dp)
        ) {

            item {
                ProfileUserImageAndRatingsContent(user, user.reviewRating.toFloat()) {
                    navHostController.navigate(Screens.EditProfile.path)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                ProfileActionOptionsCard(navHostController)
            }
            item {
                SettingPreferencesCard(
                    notificationsEnabled = notificationsEnabled,
                    onNotificationToggle = {
                        notificationsEnabled = it
                    }
                )
            }
        }
    }
}

@Composable
fun SettingPreferencesCard(
    notificationsEnabled: Boolean,
    onNotificationToggle: (Boolean) -> Unit,
) {
    Text(
        text = stringResource(Res.string.Preferences),
        fontWeight = FontWeight.Bold,
        style = text_style_h5,
        color = blackColor,
        modifier = Modifier.padding(top = 24.dp)
    )
    Card(
        colors = CardDefaults.cardColors().copy(containerColor = whiteColor),
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            PreferenceItem(
                stringResource(Res.string.Notifications), notificationsEnabled, onNotificationToggle
            )
        }
    }

}

@Composable
fun ProfileActionOptionsCard(navHostController: NavHostController) {
    Card(
        colors = CardDefaults.cardColors().copy(containerColor = whiteColor),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            ProfileOption(stringResource(Res.string.update_profile)) {
                navHostController.navigate(Screens.EditProfile.path)
            }
        }
    }

}

@Composable
fun ProfileUserImageAndRatingsContent(
    user: UserRequestResponse,
    rating: Float,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            AsyncImage(
                model = user.profilePictureUrl.toDevomImage(),
                modifier = Modifier.padding(top = 32.dp).size(115.dp).clip(CircleShape)
            )

            Box(
                modifier = Modifier.offset(x = (-4).dp, y = (-4).dp).size(24.dp).clip(CircleShape)
                    .background(Color(0xFFFFC107)).clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_edit),
                    contentDescription = "Edit",
                    modifier = Modifier.size(14.dp),
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.width(4.dp))

            AnimatedVisibility(user.verified == ApplicationStatus.VERIFIED.status) {
                Image(
                    painter = painterResource(Res.drawable.ic_verified),
                    contentDescription = "Verified",
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
fun ProfileOption(title: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable {
            onClick()
        }.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title)
        Icon(painterResource(Res.drawable.arrow_drop_down_right), "")
    }
    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
}

@Composable
fun PreferenceItem(title: String, isEnabled: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title)
        Switch(
            colors = SwitchDefaults.colors().copy(
                checkedBorderColor = Color.Transparent,
                uncheckedBorderColor = Color.Transparent,
                uncheckedTrackColor = greyColor,
                checkedThumbColor = whiteColor,
                uncheckedThumbColor = whiteColor,
                checkedTrackColor = primaryColor
            ),
            checked = isEnabled,
            onCheckedChange = onToggle,
        )
    }
    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
}