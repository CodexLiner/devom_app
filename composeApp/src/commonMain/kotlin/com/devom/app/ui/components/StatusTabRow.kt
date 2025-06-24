package com.devom.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.devom.app.theme.blackColor
import com.devom.app.theme.primaryColor
import com.devom.app.theme.text_style_lead_text
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

data class TabRowItem(val title: String, val icon: DrawableResource? = null)

@Composable
fun StatusTabRow(
    modifier: Modifier = Modifier.fillMaxWidth(),
    containerColor: Color = Color.White,
    selectedTabIndex: MutableState<Int>,
    tabs: List<TabRowItem>,
    contentColor: Color = Color.Black,
    selectedTextColor: Color = primaryColor,
    unselectedTextColor: Color = blackColor,
    indicatorColor: Color = Color(0xFFFF6F00),
    divider: @Composable () -> Unit = @Composable { HorizontalDivider() },
) {
    TabRow(
        modifier = modifier.fillMaxWidth(),
        selectedTabIndex = selectedTabIndex.value,
        contentColor = contentColor,
        divider = divider,
        indicator = { tabPositions ->
            SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex.value]).height(3.dp),
                color = indicatorColor
            )
        },
        containerColor = containerColor
    ) {
        tabs.forEachIndexed { index, (title, icon) ->
            Tab(
                selected = selectedTabIndex.value == index,
                onClick = { selectedTabIndex.value = index },
                text = {
                    Row(horizontalArrangement = Arrangement.Center) {
                        icon?.let {
                            Image(
                                colorFilter = ColorFilter.tint(if (selectedTabIndex.value == index) selectedTextColor else unselectedTextColor),
                                painter = painterResource(icon),
                                contentDescription = null,
                                modifier = Modifier.height(18.dp)
                            )
                        }
                        Text(
                            text = title,
                            textAlign = TextAlign.Center,
                            style = text_style_lead_text,
                            color = if (selectedTabIndex.value == index) selectedTextColor else unselectedTextColor,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                })
        }
    }
}