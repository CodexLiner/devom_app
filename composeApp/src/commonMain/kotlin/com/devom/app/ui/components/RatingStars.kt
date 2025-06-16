package com.devom.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.devom.app.utils.toColor
import org.jetbrains.compose.resources.painterResource
import pandijtapp.composeapp.generated.resources.Res
import pandijtapp.composeapp.generated.resources.ic_star

@Composable
fun RatingStars(modifier: Modifier = Modifier, rating: Float = 0f, tint: Color = Color(0xFF4CAF50)) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically , horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        val fullStars = rating.toInt()
        val emptyStars = 5 - fullStars

        repeat(fullStars) {
            Image(
                modifier = Modifier.size(20.dp),
                painter = painterResource(Res.drawable.ic_star),
                contentDescription = null,
                colorFilter = ColorFilter.tint(tint)
            )
        }

        repeat(emptyStars) {
            Image(
                modifier = Modifier.size(20.dp),
                painter = painterResource(Res.drawable.ic_star),
                contentDescription = null,
                colorFilter = ColorFilter.tint("#DDDDDD".toColor())
            )
        }
    }
}