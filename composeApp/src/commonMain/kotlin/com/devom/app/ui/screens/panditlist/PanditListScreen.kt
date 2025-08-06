package com.devom.app.ui.screens.panditlist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import co.touchlab.kermit.Logger
import com.devom.app.models.SupportedFiles
import com.devom.app.theme.backgroundColor
import com.devom.app.theme.blackColor
import com.devom.app.theme.greyColor
import com.devom.app.theme.primaryColor
import com.devom.app.theme.textBlackShade
import com.devom.app.theme.text_style_h4
import com.devom.app.theme.text_style_lead_body_1
import com.devom.app.theme.whiteColor
import com.devom.app.ui.components.AppBar
import com.devom.app.ui.components.AsyncImage
import com.devom.app.ui.components.ButtonPrimary
import com.devom.app.ui.components.ImageViewer
import com.devom.app.ui.components.NoContentView
import com.devom.app.ui.components.RatingStars
import com.devom.app.ui.components.StatusTabRow
import com.devom.app.ui.components.TabRowItem
import com.devom.app.ui.components.TextInputField
import com.devom.app.ui.navigation.Screens
import com.devom.app.ui.screens.biography.MediaItem
import com.devom.app.ui.screens.reviews.ReviewItem
import com.devom.app.utils.toDevomImage
import com.devom.app.utils.toJsonString
import com.devom.app.utils.toRupay
import com.devom.app.utils.urlEncode
import com.devom.models.pandit.Media
import com.devom.models.pandit.Review
import com.devom.models.pooja.GetPoojaResponse
import com.devom.models.slots.GetAllPanditByPoojaIdResponse
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.book_now
import devom_app.composeapp.generated.resources.choose_pandit
import devom_app.composeapp.generated.resources.experience_years
import devom_app.composeapp.generated.resources.ic_arrow_left
import devom_app.composeapp.generated.resources.ic_close
import devom_app.composeapp.generated.resources.ic_filters
import devom_app.composeapp.generated.resources.ic_search
import devom_app.composeapp.generated.resources.search
import kotlinx.coroutines.launch
import network.chaintech.cmpcharts.common.extensions.formatToSinglePrecision
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.round

@Composable

fun PanditListScreen(navController: NavController, pooja: GetPoojaResponse?, isUrgent: Boolean) {
    val viewModel: PanditListScreenViewModel = viewModel {
        PanditListScreenViewModel()
    }
    LaunchedEffect(Unit) {
        viewModel.getAllPanditByPoojaId(pooja?.id ?: 0)
    }

    Column(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        AppBar(
            navigationIcon = painterResource(Res.drawable.ic_arrow_left),
            title = stringResource(Res.string.choose_pandit),
            onNavigationIconClick = { navController.popBackStack() },
        )
        PanditListScreenContent(viewModel, navController, pooja, isUrgent)
    }
}

@Composable
fun ColumnScope.PanditListScreenContent(
    viewModel: PanditListScreenViewModel,
    navController: NavController,
    pooja: GetPoojaResponse?,
    isUrgent: Boolean,
) {
    val panditList = viewModel.allPanditList.collectAsState()
    val filteredPanditList = remember(panditList.value) { mutableStateOf(panditList.value) }
    var selectedPandit by remember { mutableStateOf<GetAllPanditByPoojaIdResponse?>(null) }
    val showSheet = remember { mutableStateOf(false) }
    val filterSheet = remember { mutableStateOf(false) }
    val searchText = remember { mutableStateOf("") }

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextInputField(
            modifier = Modifier.weight(1f).height(56.dp).border(
                width = 1.dp,
                color = greyColor.copy(0.24f),
                shape = RoundedCornerShape(12.dp)
            ),
            placeholder = stringResource(Res.string.search),
            backgroundColor = whiteColor, leadingIcon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_search),
                    contentDescription = null,
                    tint = greyColor.copy(.8f)
                )
            }
        ) {
            searchText.value = it
        }

        IconButton(
            modifier = Modifier.background(whiteColor, RoundedCornerShape(12.dp)).border(
                width = 1.dp,
                color = greyColor.copy(0.24f),
                shape = RoundedCornerShape(12.dp)
            ).size(56.dp),
            onClick = {
                filterSheet.value = true
            }
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_filters),
                contentDescription = null,
                tint = greyColor.copy(.8f)
            )
        }
    }

    if (filteredPanditList.value.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(
                top = 12.dp,
                start = 16.dp,
                end = 12.dp,
                bottom = 200.dp
            )
        ) {
            items(filteredPanditList.value.filter { it.fullName.contains(searchText.value, true) }) { pandit ->
                PanditDetailsCard(
                    pandit = pandit,
                    isSelected = pandit == selectedPandit,
                    onLongClick = {
                        selectedPandit = pandit
                        showSheet.value = true
                    },
                    onClick = {
                        selectedPandit = pandit
                    }
                )
            }
        }
    } else NoContentView(title = null, image = null, message = "No pandits found")


    if (selectedPandit != null) {
        ButtonPrimary(
            modifier = Modifier.navigationBarsPadding()
                .padding(horizontal = 16.dp).fillMaxWidth()
                .height(48.dp),
            buttonText = stringResource(Res.string.book_now),
            onClick = {
                navController.navigate(
                    Screens.SelectSlot.path.plus(
                        "/${pooja.toJsonString().urlEncode()}/${
                            selectedPandit.toJsonString().urlEncode()
                        }/$isUrgent"
                    )
                )
            }
        )
    }

    PanditListFilters(
        title = "Filters",
        showSheet = filterSheet.value,
        onDismiss = { filterSheet.value = false },
        pandits = panditList.value,
        onClick = {
            filteredPanditList.value = it.orEmpty()
        }
    )

    PanditDetailsSheet(
        showSheet = showSheet.value,
        onDismiss = {
            showSheet.value = false
        },
        onClick = {
            navController.navigate(
                Screens.SelectSlot.path.plus(
                    "/${
                        pooja.toJsonString().urlEncode()
                    }/${selectedPandit.toJsonString().urlEncode()}/$isUrgent"
                )
            )
        },
        pandit = selectedPandit,
        title = "Panditji  Details"
    )
}

@Composable
fun PanditDetailsCard(
    padding: Dp = 8.dp,
    pandit: GetAllPanditByPoojaIdResponse,
    isSelected: Boolean = false,
    onLongClick: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    val borderColor = if (isSelected) primaryColor.copy(0.5f) else greyColor.copy(0.24f)
    val backgroundColor = if (isSelected) primaryColor.copy(0.08f) else whiteColor

    Row(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(padding)
            .fillMaxWidth()
            .combinedClickable(
                onClick = onLongClick,
                onLongClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            onError = {
                Logger.d("KermitLogger $it")
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
        rounded.toFloat().formatToSinglePrecision()
    } catch (_: Exception) {
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
            text = pandit.withoutItemPrice.toRupay(),
            fontSize = 18.sp,
            fontWeight = FontWeight.W600,
            color = textBlackShade
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanditDetailsSheet(
    showSheet: Boolean,
    title: String? = null,
    onDismiss: () -> Unit,
    pandit: GetAllPanditByPoojaIdResponse?,
    onClick: () -> Unit = {},
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val selectedIndex = remember { mutableStateOf(0) }
    val viewImage = remember { mutableStateOf(false) }
    val selectedMedia = remember { mutableStateOf<Media?>(null) }

    if (showSheet) {
        ModalBottomSheet(
            containerColor = whiteColor,
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    onDismiss()
                }
            },
            sheetState = sheetState
        ) {
            Box {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    title?.let {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = it, style = text_style_h4, color = blackColor)
                            IconButton(onDismiss) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_close),
                                    contentDescription = null,
                                    tint = blackColor
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        pandit?.let {
                            PanditDetailsCard(
                                padding = 0.dp,
                                pandit = pandit,
                                isSelected = false,
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "About ${pandit?.fullName}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W600,
                                color = textBlackShade
                            )
                            Text(
                                text = pandit?.aboutUs.orEmpty().ifBlank { "N/A" },
                                style = text_style_lead_body_1,
                                color = greyColor
                            )
                        }

                        StatusTabRow(
                            calculateSize = false,
                            modifier = Modifier.wrapContentWidth(),
                            selectedTabIndex = selectedIndex,
                            tabs = listOf(
                                TabRowItem(title = "Reviews", icon = null),
                                TabRowItem(title = "Media", icon = null)
                            )
                        )

                        when (selectedIndex.value) {
                            0 -> {
                                Column {
                                    pandit?.reviews.orEmpty().forEach {
                                        ReviewItem(review = it.toReview())
                                    }
                                }
                            }

                            1 -> {
                                LazyVerticalGrid(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 300.dp),
                                    columns = GridCells.Fixed(3),
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    items(pandit?.videoUrls.orEmpty()) {
                                        MediaItem(model = it.url, SupportedFiles.VIDEO.type) {
                                            viewImage.value = true
                                            selectedMedia.value = Media(
                                                documentUrl = it.url,
                                                documentType = SupportedFiles.VIDEO.type
                                            )
                                        }
                                    }

                                    items(pandit?.photoUrls.orEmpty()) {
                                        MediaItem(model = it, SupportedFiles.IMAGE.type) {
                                            viewImage.value = true
                                            selectedMedia.value = Media(
                                                documentUrl = it,
                                                documentType = SupportedFiles.IMAGE.type
                                            )
                                        }
                                    }

                                }
                            }
                        }
                    }
                }

                ButtonPrimary(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 16.dp)
                        .height(48.dp),
                    buttonText = stringResource(Res.string.book_now),
                    onClick = onClick
                )
            }
        }
    }
    ImageViewer(viewImage, selectedMedia)

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanditListFilters(
    showSheet: Boolean,
    title: String? = null,
    onDismiss: () -> Unit,
    pandits: List<GetAllPanditByPoojaIdResponse>?,
    onClick: (List<GetAllPanditByPoojaIdResponse>?) -> Unit = {},
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val filteredList = remember { mutableStateOf(pandits) }
    if (showSheet) {
        ModalBottomSheet(
            containerColor = whiteColor,
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    onDismiss()
                }
            },
            sheetState = sheetState
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                title?.let {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = it, style = text_style_h4, color = blackColor)
                    }
                }
                FilterScreen(pandits = pandits.orEmpty()) {
                    filteredList.value = it
                }
                ButtonPrimary(
                    modifier = Modifier.navigationBarsPadding().padding(horizontal = 24.dp)
                        .fillMaxWidth().height(48.dp),
                    buttonText = "Apply",
                    onClick = {
                        onDismiss()
                        onClick(filteredList.value)
                    }
                )
            }
        }
    }
}

private fun com.devom.models.slots.Review.toReview(): Review {
    return Review(
        userId = panditId.toString(),
        userName = reviewerName,
        userImage = reviewerImage,
        rating = rating,
        reviewText = reviewText,
        reviewId = "",
        bookingId = "",
        panditId = "",
        poojaId = "",
        createdAt = createdAt,
        updatedAt = "",
    )
}

