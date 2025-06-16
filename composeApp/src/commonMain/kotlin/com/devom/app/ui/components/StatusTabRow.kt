package com.devom.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.devom.app.theme.blackColor
import com.devom.app.theme.primaryColor
import com.devom.app.theme.text_style_lead_text

@Composable
fun StatusTabRow(selectedTabIndex: MutableState<Int>, tabs: List<String>) {
    TabRow(
        modifier = Modifier.fillMaxWidth(),
        selectedTabIndex = selectedTabIndex.value,
        contentColor = Color.Black,
        indicator = { tabPositions ->
            SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex.value]).height(3.dp),
                color = Color(0xFFFF6F00)
            )
        },
        containerColor = Color.White
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex.value == index,
                onClick = { selectedTabIndex.value = index },
                text = {
                    Text(
                        text = title,
                        textAlign = TextAlign.Center,
                        style = text_style_lead_text,
                        color = if (selectedTabIndex.value == index) primaryColor else blackColor,
                        modifier = Modifier.fillMaxWidth()
                    )
                })
        }
    }
}