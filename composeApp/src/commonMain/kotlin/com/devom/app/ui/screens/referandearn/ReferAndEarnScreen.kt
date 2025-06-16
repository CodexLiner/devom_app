package com.devom.app.ui.screens.referandearn

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.devom.app.ASSET_LINK_BASE_URL
import com.devom.app.theme.blackColor
import com.devom.app.theme.greyColor
import com.devom.app.theme.primaryColor
import com.devom.app.theme.text_style_h3
import com.devom.app.theme.whiteColor
import com.devom.app.ui.components.AppBar
import com.devom.app.ui.components.NoContentView
import com.devom.app.ui.components.ShapedScreen
import com.devom.models.auth.UserRequestResponse
import com.devom.utils.Contact
import com.devom.utils.share.ShareServiceProvider
import com.devom.utils.share.shareContent
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.copy
import devom_app.composeapp.generated.resources.ic_arrow_left
import devom_app.composeapp.generated.resources.ic_search
import devom_app.composeapp.generated.resources.img_social_friends
import devom_app.composeapp.generated.resources.invite
import devom_app.composeapp.generated.resources.invite_friends
import devom_app.composeapp.generated.resources.referral_message
import devom_app.composeapp.generated.resources.share

@Composable
fun ReferAndEarnScreen(navController: NavHostController) {
    val viewModel: ReferAndEarnViewModel = viewModel {
        ReferAndEarnViewModel()
    }
    LaunchedEffect(Unit) {
        viewModel.getContactList()
    }
    val user = viewModel.user.collectAsState()
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        AppBar(
            navigationIcon = painterResource(Res.drawable.ic_arrow_left),
            title = "Refer & Earn",
            onNavigationIconClick = { navController.popBackStack() })
        ReferAndEarnScreenContent(user , viewModel)
    }

}

@Composable
fun ReferAndEarnScreenContent(user: State<UserRequestResponse?>, viewModel: ReferAndEarnViewModel) {
    ShapedScreen(
        modifier = Modifier.fillMaxSize().background(primaryColor),
        headerContent = {
            ReferHeaderContent(user)
        }, mainContent = {
            ReferMainContent(user , viewModel)
        }
    )
}

@Composable
fun ReferMainContent(user: State<UserRequestResponse?>, viewModel: ReferAndEarnViewModel) {
    val contacts = viewModel.contacts.collectAsState()
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = stringResource(Res.string.invite_friends),
                style = text_style_h3,
                color = blackColor
            )
            Image(
                painter = painterResource(Res.drawable.ic_search),
                contentDescription = null,
                modifier = Modifier.padding(end = 6.dp).size(24.dp)
            )
        }
        ReferContactList(user , contacts.value)

    }
}

@Composable
fun ReferContactList(user: State<UserRequestResponse?>, contacts: List<Contact>) {
    if (contacts.isNotEmpty())LazyColumn(
        contentPadding = PaddingValues(vertical = 41.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(contacts) {
            ReferContactItem(it, user)
        }
    }else {
        NoContentView(
            image = null,
            title = null,
            message = "Please Allow Contact Permission\n from settings to view contacts"
        )
    }
}

@Composable
fun ReferContactItem(contact: Contact, user: State<UserRequestResponse?>) {
    Row {
        ContactInitials(contact)
        ContactDetails(contact)
        InviteButton(user.value, contact)
    }
}

@Composable
fun InviteButton(user: UserRequestResponse?, contact: Contact) {
    val referralMessage = stringResource(Res.string.referral_message, user?.referralCode.orEmpty())
    Text(
        text = stringResource(Res.string.invite),
        color = whiteColor,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.W700,
        fontSize = 12.sp,
        modifier = Modifier
            .background(primaryColor, RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp, horizontal = 18.dp).clickable {
                shareContent(
                    ShareServiceProvider(),
                    referralMessage,
                    ASSET_LINK_BASE_URL.plus(
                        "referral?phone=${
                            contact.phoneNumber.trim().replace("\\s+".toRegex(), "")
                        }&code=${user?.referralCode}"
                    )
                )
            }
    )
}

@Composable
fun RowScope.ContactDetails(contact: Contact) {
    Column(
        modifier = Modifier
            .padding(start = 16.dp)
            .weight(1f)
    ) {
        Text(
            text = contact.name,
            color = blackColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
        Text(
            text = contact.phoneNumber,
            color = greyColor,
            fontSize = 14.sp
        )
    }
}

@Composable
fun ContactInitials(contact: Contact) {
    Box(modifier = Modifier.size(48.dp).background(blackColor, CircleShape)) {
        val initials = contact.name
            .split(" ")
            .filter { it.isNotEmpty() }
            .take(2)
            .map { it.first().uppercaseChar() }
            .joinToString("")

        Text(
            text = initials,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun ReferHeaderContent(user: State<UserRequestResponse?>) {
    val referralMessage =
        stringResource(Res.string.referral_message, user.value?.referralCode.orEmpty())

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(Res.drawable.img_social_friends),
            contentDescription = null
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(0.7f)
                    .background(whiteColor, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = user.value?.referralCode.orEmpty(),
                    color = greyColor
                )
                Text(
                    text = stringResource(Res.string.copy),
                    color = greyColor
                )
            }

            Text(
                text = stringResource(Res.string.share),
                color = whiteColor,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier
                    .weight(0.3f)
                    .background(blackColor, RoundedCornerShape(16.dp))
                    .padding(vertical = 14.dp, horizontal = 16.dp).clickable {
                        shareContent(
                            ShareServiceProvider(),
                            referralMessage,
                            ASSET_LINK_BASE_URL.plus("referral?code=${user.value?.referralCode}")
                        )
                    }
            )
        }
    }
}
