package com.devom.app.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.devom.app.theme.backgroundColor
import com.devom.app.theme.blackColor
import com.devom.app.theme.primaryColor
import com.devom.app.theme.textBlackShade
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
        viewModel.getBanners()
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
    val banners by viewModel.banners.collectAsState()

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
            val selectedCategory = tabs.getOrNull(selectedTabIndex.value)?.title?.lowercase()

            poojaList.filter {
                (selectedTabIndex.value == 0 || it.category.lowercase() == selectedCategory) &&
                        it.name.lowercase().contains(query)
            }
        }
    }

    val navigateToPanditList: (GetPoojaResponse) -> Unit = {
        navHostController.navigate(
            Screens.PanditListScreen.path + "/${
                it.toJsonString().urlEncode()
            }/false"
        )
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
                    .padding(top = 4.dp, bottom = 18.dp),
                placeholder = stringResource(Res.string.search_for_pooja),
                backgroundColor = whiteColor,
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_search),
                        contentDescription = null,
                        tint = primaryColor
                    )
                }
            ) { searchText.value = it }

            AnimatedVisibility(tabs.size > 1) {
                StatusTabRow(
                    modifier = Modifier.fillMaxWidth(),
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

        LazyColumn(
            contentPadding = PaddingValues(bottom = 200.dp),
            modifier = Modifier.animateContentSize()
        ) {
            item {
                if (selectedTabIndex.value == 0) {
                    HomeScreenAllContent(navHostController)
                } else {
                    PoojaContent(
                        poojaList = filteredList,
                        title = tabs.getOrNull(selectedTabIndex.value)?.title.orEmpty(),
                        onClick = navigateToPanditList
                    )
                }
            }

            if (selectedTabIndex.value == 0) {
                val appBanners = banners.filter { it.bannerType == "app" }

                if (poojaList.isNotEmpty()) {
                    item {
                        Text(
                            text = "Pooja Listing",
                            style = text_style_h5,
                            color = blackColor,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        PoojaList(poojaList.take(6), onClick = navigateToPanditList)
                    }
                }

                if (appBanners.isNotEmpty()) {
                    item {
                        HomeScreenBanner(appBanners)
                    }
                }

                if (poojaList.size > 6) {
                    item {
                        Box(modifier = Modifier.padding(top = 16.dp)) {
                            PoojaList(poojaList.drop(6), onClick = navigateToPanditList)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreenBanner(banners: List<BannersResponse>) {
    BoxWithConstraints {
        val width = maxWidth
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(banners) {
                BannerItem(it, width = width)
            }
        }
    }
}

@Composable
fun BannerItem(banner: BannersResponse, width: Dp) {
    Box(
        modifier = Modifier.padding(top = 16.dp)
            .width(width * .7f)
            .height(135.dp)
            .clip(RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = banner.imageUrl.toDevomImage(),
            contentScale = ContentScale.Crop
        )

        Row(
            modifier = Modifier
                .matchParentSize().background(textBlackShade.copy(.3f)).padding(horizontal = 35.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Spacer(modifier = Modifier)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = banner.title.capitalize(Locale.current),
                    style = text_style_h5,
                    color = whiteColor,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = whiteColor,
                        shape = RoundedCornerShape(12.dp)
                    ).padding(horizontal = 12.dp, vertical = 4.dp),
                    text = banner.buttonText,
                    color = whiteColor,
                    fontWeight = FontWeight.W600,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun HomeScreenAllContent(
    navHostController: NavHostController,
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.fillMaxWidth().padding(16.dp)
            .background(primaryColor, RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.weight(1f).padding(vertical = 24.dp, horizontal = 16.dp),
        ) {
            Text(style = text_style_h4, text = "Urgent Booking", color = whiteColor)
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
}