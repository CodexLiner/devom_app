package com.devom.app.ui.screens.home.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.devom.app.theme.primaryColor
import com.devom.app.theme.textBlackShade
import com.devom.app.theme.text_style_h4
import com.devom.app.theme.whiteColor
import com.devom.app.ui.components.AsyncImage
import com.devom.app.ui.screens.home.PatternDesign
import com.devom.app.ui.screens.home.PoojaList
import com.devom.app.utils.toDevomImage
import com.devom.models.pooja.GetPoojaResponse

@Composable
fun PoojaContent(
    poojaList: List<GetPoojaResponse>,
    title: String = "",
    banner: String = "",
    onClick: (GetPoojaResponse) -> Unit = {},
) {
    Column {
        Row(
            modifier = Modifier.background(primaryColor).padding(top = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (banner.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(113.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        modifier = Modifier.matchParentSize(),
                        model = banner.toDevomImage()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(textBlackShade.copy(alpha = 0.3f))
                    )
                    Text(
                        text = "$title\nBooking",
                        textAlign = TextAlign.Center,
                        style = text_style_h4,
                        color = whiteColor
                    )
                }
            } else {
                PatternDesign(modifier = Modifier)
                Text(
                    modifier = Modifier.weight(1f),
                    text = "$title\nBooking",
                    textAlign = TextAlign.Center,
                    style = text_style_h4,
                    color = whiteColor
                )
                PatternDesign(modifier = Modifier)
            }
        }

        PoojaList(poojaList, onClick)
    }
}
