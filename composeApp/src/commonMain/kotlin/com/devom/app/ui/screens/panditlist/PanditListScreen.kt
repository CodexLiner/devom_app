package com.devom.app.ui.screens.panditlist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.devom.app.theme.backgroundColor
import com.devom.app.theme.blackColor
import com.devom.app.theme.greyColor
import com.devom.app.theme.primaryColor
import com.devom.app.theme.textBlackShade
import com.devom.app.theme.whiteColor
import com.devom.app.ui.components.AppBar
import com.devom.app.ui.components.AsyncImage
import com.devom.app.ui.components.RatingStars
import com.devom.app.ui.components.TextInputField
import com.devom.app.utils.toDevomImage
import com.devom.models.slots.GetAllPanditByPoojaIdResponse
import com.devom.network.getUser
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.choose_pandit
import devom_app.composeapp.generated.resources.experience_years
import devom_app.composeapp.generated.resources.ic_arrow_left
import devom_app.composeapp.generated.resources.ic_search
import devom_app.composeapp.generated.resources.search
import devom_app.composeapp.generated.resources.search_for_pooja
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.round

@Composable

fun PanditListScreen(navController: NavController, poojaId: Int = 18) {
    val viewModel: PanditListScreenViewModel = viewModel {
        PanditListScreenViewModel()
    }
    LaunchedEffect(Unit) {
        viewModel.getAllPanditByPoojaId(poojaId)
    }
    Column(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        AppBar(
            navigationIcon = painterResource(Res.drawable.ic_arrow_left),
            title = stringResource(Res.string.choose_pandit),
            onNavigationIconClick = { navController.popBackStack() },
        )
        PanditListScreenContent(viewModel, navController)
    }
}

@Composable
fun ColumnScope.PanditListScreenContent(
    viewModel: PanditListScreenViewModel,
    navController: NavController,
) {
    val panditList = viewModel.allPanditList.collectAsState()
    var selectedPandit by remember { mutableStateOf<GetAllPanditByPoojaIdResponse?>(null) }

    TextInputField(
        modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp),
        placeholder = stringResource(Res.string.search),
        backgroundColor = whiteColor, leadingIcon = {
            Icon(
                painter = painterResource(Res.drawable.ic_search),
                contentDescription = null,
                tint = primaryColor
            )
        }
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(
            top = 12.dp,
            start = 16.dp,
            end = 12.dp,
            bottom = 200.dp
        )
    ) {
        items(panditList.value) { pandit ->
            PanditDetailsCard(
                pandit = pandit,
                isSelected = pandit == selectedPandit,
                onClick = { selectedPandit = pandit }
            )
        }
    }
}

@Composable
fun PanditDetailsCard(
    pandit: GetAllPanditByPoojaIdResponse,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) primaryColor.copy(0.5f) else greyColor.copy(0.24f)
    val backgroundColor = if (isSelected) primaryColor.copy(0.08f) else whiteColor

    Row(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            onError = {
                co.touchlab.kermit.Logger.d("KermitLogger $it")
            },
            model = pandit.profilePictureUrl.toDevomImage(),
            modifier = Modifier
                .size(71.dp, 79.dp)
                .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp)),
        )
        PanditInformation(pandit)
        PanditRatingsAndPricing(pandit)
    }
}


@Composable
fun RowScope.PanditInformation(pandit: GetAllPanditByPoojaIdResponse) {
    Column(
        modifier = Modifier.padding(start = 12.dp).weight(1f)
    ) {
        Text(
            text = pandit.fullName,
            fontSize = 16.sp,
            fontWeight = FontWeight.W700,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = blackColor
        )
        Text(
            text = pandit.specialty,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis, fontSize = 13.sp,
            fontWeight = FontWeight.W400,
            color = greyColor
        )
        Text(
            text = pandit.languages,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis, fontSize = 13.sp,
            fontWeight = FontWeight.W400,
            color = greyColor
        )
        Text(
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            text = stringResource(Res.string.experience_years, pandit.experienceYears),
            fontSize = 13.sp,
            fontWeight = FontWeight.W400,
            color = greyColor
        )
    }
}

@Composable
fun PanditRatingsAndPricing(pandit: GetAllPanditByPoojaIdResponse) {

    val formattedRating = try {
        val raw = pandit.averageRating.toDouble()
        val rounded = (round(raw * 10) / 10)
        rounded.toString()
    } catch (e: Exception) {
        "0.0"
    }

    Column(verticalArrangement = Arrangement.Center, modifier = Modifier.padding(start = 12.dp)) {
        Row(horizontalArrangement = Arrangement.End) {
            RatingStars(
                spacing = 0.dp,
                iconModifier = Modifier.size(16.dp),
                rating = pandit.averageRating.toFloatOrNull() ?: 0f
            )
            Text(
                text = "(${formattedRating}/5)",
                fontSize = 12.sp,
                fontWeight = FontWeight.W600,
                color = greyColor
            )
        }
        Text(
            text = "₹${pandit.withoutItemPrice}/hr",
            fontSize = 18.sp,
            fontWeight = FontWeight.W600,
            color = textBlackShade
        )
    }
}
