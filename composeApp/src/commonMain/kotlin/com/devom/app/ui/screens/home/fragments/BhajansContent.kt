package com.devom.app.ui.screens.home.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.devom.app.theme.primaryColor
import com.devom.app.theme.text_style_h4
import com.devom.app.theme.whiteColor
import com.devom.app.ui.screens.home.PatternDesign

@Composable
fun BhajansContent() {
    Column {
        Row(
            modifier = Modifier.background(primaryColor).padding(top = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PatternDesign(modifier = Modifier)
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = "Bhajan\nBooking",
                style = text_style_h4,
                color = whiteColor
            )
            PatternDesign(modifier = Modifier)
        }
    }
}
