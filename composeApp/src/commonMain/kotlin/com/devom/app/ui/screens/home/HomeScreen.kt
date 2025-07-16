package com.devom.app.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import com.devom.app.ui.components.AsyncImage
import com.devom.app.ui.components.StatusTabRow
import com.devom.app.ui.components.TabRowItem
import com.devom.app.ui.components.TextInputField
import com.devom.app.ui.navigation.Screens
import com.devom.app.ui.screens.home.fragments.PoojaContent
import com.devom.app.utils.toDevomImage
import com.devom.app.utils.toJsonString
import com.devom.app.utils.urlEncode
import com.devom.models.other.BannersResponse
import com.devom.models.pooja.GetPoojaResponse
import com.devom.network.getUser
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.ic_grid_cells
import devom_app.composeapp.generated.resources.ic_music
import devom_app.composeapp.generated.resources.ic_notification
import devom_app.composeapp.generated.resources.ic_pray
import devom_app.composeapp.generated.resources.ic_search
import devom_app.composeapp.generated.resources.search_for_pooja
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
            title = "Hi ${getUser().fullName}",
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
    val poojaList by viewModel.poojaList.collectAsState()
    val searchText = remember { mutableStateOf("") }
    val selectedTabIndex = remember { mutableStateOf(0) }
    val banners = viewModel.banners.collectAsState()

    val tabs = remember(poojaList) {
        buildList {
            add(TabRowItem("All", Res.drawable.ic_grid_cells))
            addAll(
                poojaList.distinctBy { it.category.lowercase() }.map {
                    TabRowItem(
                        it.category,
                        if (it.category.lowercase() == "pooja") Res.drawable.ic_pray else Res.drawable.ic_music
                    )
                }
            )
        }
    }

    val filteredList by remember(poojaList, searchText.value, selectedTabIndex.value) {
        derivedStateOf {
            val query = searchText.value.lowercase()
            if (selectedTabIndex.value == 0) {
                poojaList.filter { it.name.lowercase().contains(query) }
            } else {
                val selectedCategory = tabs.getOrNull(selectedTabIndex.value)?.title?.lowercase()
                poojaList.filter {
                    it.category.lowercase() == selectedCategory &&
                            it.name.lowercase().contains(query)
                }
            }
        }
    }

    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(primaryColor)
        ) {
            TextInputField(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 4.dp , bottom = 18.dp),
                placeholder = stringResource(Res.string.search_for_pooja),
                backgroundColor = whiteColor,
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_search),
                        contentDescription = null,
                        tint = primaryColor
                    )
                }
            ) {
                searchText.value = it
            }

           AnimatedVisibility(tabs.size > 1 , modifier = Modifier.fillMaxWidth()) {
               StatusTabRow(
                   modifier = Modifier,
                   divider = {},
                   containerColor = primaryColor,
                   selectedTabIndex = selectedTabIndex,
                   tabs = tabs,
                   selectedTextColor = whiteColor,
                   unselectedTextColor = whiteColor.copy(alpha = 0.8f),
                   indicatorColor = whiteColor
               )
           }
        }

        if (selectedTabIndex.value == 0) {
            HomeScreenAllContent(filteredList, navHostController)
        } else {
            val selectedTitle = tabs.getOrNull(selectedTabIndex.value)?.title.orEmpty()
            val banner = poojaList.find {
                it.category.equals(selectedTitle, ignoreCase = true)
            }?.categoryImage.orEmpty()

            PoojaContent(
                poojaList = filteredList,
                title = selectedTitle
            ) {
                navHostController.navigate(
                    Screens.PanditListScreen.path + "/${it.toJsonString().urlEncode()}/false"
                )
            }
        }
        HomeScreenBanner(banners.value)
    }
}

@Composable
fun HomeScreenBanner(banners: List<BannersResponse>) {
    LazyRow {
        items(banners.size) {
            BannerItem(banners[it])
        }
    }
}

@Composable
fun BannerItem(banner: BannersResponse) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(113.dp),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            modifier = Modifier.matchParentSize(),
            model = banner.imageUrl.toDevomImage()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Red)
        )
    }
}


@Composable
fun HomeScreenAllContent(
    poojaList: List<GetPoojaResponse>,
    navHostController: NavHostController,
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
                    .padding(horizontal = 12.dp, vertical = 8.dp).clickable {
                        navHostController.navigate(
                            Screens.UrgentBooking.path + "/true"
                        )
                    }
            )
        }
        PatternDesign(modifier = Modifier)
    }

    if (poojaList.isNotEmpty()) {
        Text(
            text = "Pooja Listing",
            style = text_style_h5,
            color = blackColor,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        PoojaList(poojaList) {
            navHostController.navigate(
                Screens.PanditListScreen.path + "/${it.toJsonString().urlEncode()}/false"
            )
        }
    }
}