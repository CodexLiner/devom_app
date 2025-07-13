package com.devom.app.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devom.app.theme.greenColor
import com.devom.app.theme.greyColor
import com.devom.app.theme.textBlackShade
import com.devom.app.theme.whiteColor
import com.devom.app.ui.components.AsyncImage
import com.devom.app.utils.toDevomImage
import com.devom.models.pooja.GetPoojaResponse

@Composable
fun PoojaList(poojaList: List<GetPoojaResponse>, onClick: (GetPoojaResponse) -> Unit = {}) {
    LazyVerticalGrid(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(poojaList) {
            PoojaCard(it) {
                onClick(it)
            }
        }
    }
}

@Composable
fun PoojaCard(pooja: GetPoojaResponse, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        border = BorderStroke(1.dp, color = greyColor.copy(.24f)),
        colors = CardDefaults.cardColors(containerColor = whiteColor)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = pooja.image.toDevomImage(),
                    modifier = Modifier.size(84.dp),
                )
            }
            Text(
                text = pooja.name,
                color = textBlackShade,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.W500
            )
        }
    }
}
