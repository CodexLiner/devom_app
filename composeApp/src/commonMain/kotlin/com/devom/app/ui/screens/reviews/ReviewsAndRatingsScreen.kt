package com.devom.app.ui.screens.reviews

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.devom.app.RatingColors
import com.devom.app.theme.bgColor
import com.devom.app.theme.greenColor
import com.devom.app.theme.greenColorLight
import com.devom.app.theme.greyColor
import com.devom.app.theme.primaryColor
import com.devom.app.theme.secondaryColor
import com.devom.app.theme.text_style_h4
import com.devom.app.theme.text_style_lead_body_1
import com.devom.app.theme.yellowColor
import com.devom.app.ui.components.AppBar
import com.devom.app.ui.components.AsyncImage
import com.devom.app.ui.components.NoContentView
import com.devom.app.ui.components.RatingStars
import com.devom.app.utils.toDevomImage
import com.devom.models.pandit.Review
import org.jetbrains.compose.resources.painterResource
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.ic_arrow_left
import devom_app.composeapp.generated.resources.ic_no_reviews
import devom_app.composeapp.generated.resources.ic_star
import devom_app.composeapp.generated.resources.vertical_ellipsis
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToInt

@Composable
fun ReviewsAndRatingsScreen(navController: NavController) {
    val viewModel = viewModel { ReviewsAndRatingsViewModel() }
    LaunchedEffect(Unit) {
        viewModel.getReviews()
    }
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        AppBar(
            navigationIcon = painterResource(Res.drawable.ic_arrow_left),
            title = "Review & Ratings",
            onNavigationIconClick = { navController.popBackStack() }
        )
        ReviewsAndRatingsContent(viewModel)
    }
}

@Composable
fun ReviewsAndRatingsContent(viewModel: ReviewsAndRatingsViewModel) {
    val reviews = viewModel.reviews.collectAsState()
    ReviewListContent(list = reviews)
}

@Composable
fun ReviewListContent(list: State<List<Review>>) {
    if (list.value.isEmpty()) {
        NoContentView(
            image = Res.drawable.ic_no_reviews,
            title = "No Reviews yet",
            message = "No reviews yet for this booking. Be the first to share your experience!"
        )
        return
    }

    LazyColumn(contentPadding = PaddingValues(start = 16.dp , end = 16.dp , top = 16.dp , bottom = 200.dp)) {
        item {
            ReviewsGraph(list.value)
        }
        items(list.value.size) { index ->
            ReviewItem(review = list.value[index])
        }
    }
}

@Composable
fun ReviewsGraph(reviews: List<Review>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxWidth().background(bgColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 12.dp)
    ) {
        GraphPoints(Modifier.weight(1f), reviews)
        OverAllProfileStates(reviews)
    }
}

@Composable
fun RowScope.OverAllProfileStates(reviews: List<Review>) {
    val averageRating = remember(reviews) {
        if (reviews.isNotEmpty()) {
            reviews.sumOf { it.rating.toFloat().roundToInt() }.toFloat() / reviews.size
        } else {
            0f
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp) , horizontalAlignment = Alignment.End) {
        Text("${averageRating.roundToInt()}", style = text_style_h4)
        RatingStars(modifier = Modifier.padding(top = 4.dp), rating = 4.0f)
        Text("${reviews.size} Reviews", style = text_style_lead_body_1)
    }
}

@Composable
fun GraphPoints(modifier: Modifier, reviews: List<Review>) {
    val totalCount = reviews.size.coerceAtLeast(1)
    val groups = remember(reviews) {
        reviews.groupBy { it.rating.toFloat().toInt() }
    }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        (5 downTo 1).forEach { star ->
            val count = groups[star]?.size ?: 0
            val progress = count.toFloat() / totalCount
            RatingProgress(
                modifier = Modifier.height(18.dp),
                progress = progress,
                star = star,
                RatingColors[star - 1]
            )
        }
    }
}

@Composable
fun RatingProgress(
    modifier: Modifier,
    progress: Float,
    star: Int,
    color: Color,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$star", style = text_style_lead_body_1, color = greyColor)

        Image(
            painter = painterResource(Res.drawable.ic_star),
            contentDescription = "Star Icon",
            colorFilter = ColorFilter.tint(primaryColor),
            modifier = Modifier.size(16.dp)
        )

        Box(
            modifier = Modifier.height(8.dp).weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(color, RoundedCornerShape(12.dp))
            )
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Column(modifier = Modifier.padding(top = 24.dp)) {
        ReviewerDetailRow(review)
        if (review.reviewText.isNotEmpty()) {
            Text(
                style = text_style_lead_body_1,
                text = review.reviewText,
                color = greyColor,
                modifier = Modifier.padding(top = 12.dp, end = 24.dp)
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = 24.dp),
            color = greyColor.copy(alpha = 0.24f),
            thickness = 1.dp
        )
    }
}

@Composable
fun ReviewerDetailRow(review: Review) {
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
    ) {
        AsyncImage(
            modifier = Modifier.size(38.dp).clip(CircleShape),
            model = review.userImage.toDevomImage(),
        )
        Column(
            modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(text = review.userName.ifEmpty { "user_${review.userId}" }.toString())
            RatingStars(modifier = Modifier.padding(top = 4.dp), rating = review.rating.toFloat())
        }

        Image(
            painter = painterResource(Res.drawable.vertical_ellipsis),
            contentDescription = "Options",
        )
    }
}

