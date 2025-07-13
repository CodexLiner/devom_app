package com.devom.app.ui.screens.booking.urgent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.devom.app.theme.backgroundColor
import com.devom.app.theme.blackColor
import com.devom.app.theme.text_style_h5
import com.devom.app.ui.components.AppBar
import com.devom.app.ui.navigation.Screens
import com.devom.app.ui.screens.home.PoojaList
import com.devom.app.utils.toJsonString
import com.devom.app.utils.urlEncode
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.ic_arrow_left
import org.jetbrains.compose.resources.painterResource

@Composable
fun UrgentBookingScreen(navController: NavHostController, isUrgent: Boolean) {

    val viewModel: UrgentBookingViewModel = viewModel { UrgentBookingViewModel() }
    val poojaList by viewModel.poojaList.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().background(backgroundColor)
    ) {

        AppBar(
            title = "Add Booking",
            navigationIcon = painterResource(Res.drawable.ic_arrow_left),
            onNavigationIconClick = { navController.popBackStack() }
        )
        if (poojaList.isNotEmpty()) {
            Text(

                text = "Choose Pooja Type",
                style = text_style_h5,
                color = blackColor,
                modifier = Modifier.padding(horizontal = 16.dp).padding(top = 24.dp)
            )
            PoojaList(poojaList) {
                navController.navigate(
                    Screens.PanditListScreen.path + "/${it.toJsonString().urlEncode()}/true"
                )
            }
        }
    }

}