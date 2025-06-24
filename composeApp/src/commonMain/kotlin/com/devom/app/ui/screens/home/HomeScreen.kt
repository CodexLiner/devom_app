package com.devom.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.devom.app.theme.backgroundColor
import com.devom.app.theme.blackColor
import com.devom.app.theme.primaryColor
import com.devom.app.theme.text_style_h4
import com.devom.app.theme.text_style_h5
import com.devom.app.theme.text_style_lead_text
import com.devom.app.theme.whiteColor
import com.devom.app.ui.components.AppBar
import com.devom.app.ui.components.StatusTabRow
import com.devom.app.ui.components.TabRowItem
import com.devom.app.ui.components.TextInputField
import com.devom.app.ui.navigation.Screens
import com.devom.app.ui.screens.home.fragments.BhajansContent
import com.devom.app.ui.screens.home.fragments.PoojaContent
import com.devom.app.utils.urlEncode
import com.devom.models.pooja.GetPoojaResponse
import com.devom.network.NetworkClient
import com.devom.network.getUser
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.ic_grid_cells
import devom_app.composeapp.generated.resources.ic_music
import devom_app.composeapp.generated.resources.ic_notification
import devom_app.composeapp.generated.resources.ic_pray
import devom_app.composeapp.generated.resources.ic_search
import devom_app.composeapp.generated.resources.search_for_pooja
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(navHostController: NavHostController, onNavigationIconClick: () -> Unit) {
    val viewModel: HomeScreenViewModel = viewModel {
        HomeScreenViewModel()
    }
    LaunchedEffect(Unit) {
        viewModel.getPoojaList()
    }
    Column(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        AppBar(
            title = "Hii ${getUser().fullName}",
            onNavigationIconClick = onNavigationIconClick,
            actions = {
                IconButton(onClick = {
                    navHostController.navigate(Screens.Notifications.path)
                }) {
                    Icon(
                        painterResource(Res.drawable.ic_notification),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        )
        HomeScreenContent(viewModel, navHostController)
    }
}

@Composable
fun HomeScreenContent(viewModel: HomeScreenViewModel, navHostController: NavHostController) {
    val poojaList = viewModel.poojaList.collectAsState()
    val tabs = listOf(
        TabRowItem("All", Res.drawable.ic_grid_cells),
        TabRowItem("Pooja", Res.drawable.ic_pray),
        TabRowItem("Bhajan", Res.drawable.ic_music)
    )
    val selectedTabIndex = remember { mutableStateOf(0) }
    Column {
        Column(modifier = Modifier.fillMaxWidth().background(primaryColor)) {
            TextInputField(
                modifier = Modifier.padding(horizontal = 16.dp).padding(top = 4.dp),
                placeholder = stringResource(Res.string.search_for_pooja),
                backgroundColor = whiteColor, leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_search),
                        contentDescription = null,
                        tint = primaryColor
                    )
                }
            )
            StatusTabRow(
                modifier = Modifier.padding(top = 18.dp),
                divider = {},
                containerColor = primaryColor,
                selectedTabIndex = selectedTabIndex,
                tabs = tabs,
                selectedTextColor = whiteColor,
                unselectedTextColor = whiteColor.copy(.8f),
                indicatorColor = whiteColor
            )
        }
        when (selectedTabIndex.value) {
            0 -> HomeScreenAllContent(poojaList , navHostController)
            1 -> PoojaContent(poojaList.value)
            2 -> BhajansContent()
        }
    }
}

@Composable
fun HomeScreenAllContent(
    poojaList: State<List<GetPoojaResponse>>,
    navHostController: NavHostController
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.fillMaxWidth().padding(16.dp).background(primaryColor, RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.weight(1f).padding(vertical = 24.dp, horizontal = 16.dp),
        ) {
            Text(style = text_style_h4, text = "Urgent Today’s Booking", color = whiteColor)
            Text(
                style = text_style_lead_text,
                text = "Book Now",
                color = primaryColor,
                modifier = Modifier.padding(top = 16.dp)
                    .background(whiteColor, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
        PatternDesign(modifier = Modifier)
    }

    if (poojaList.value.isNotEmpty()) {
        Text(
            text = "Pooja Listing",
            style = text_style_h5,
            color = blackColor,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        PoojaList(poojaList.value) {
            navHostController.navigate(
                Screens.PanditListScreen.path + "/${it.id}"
            )
        }
    }
}