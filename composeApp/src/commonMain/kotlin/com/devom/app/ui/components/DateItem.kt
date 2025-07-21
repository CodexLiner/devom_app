package com.devom.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.devom.app.theme.text_style_lead_body_1
import kotlinx.datetime.LocalDate

@Composable
fun DateItem(
    modifier: Modifier = Modifier.padding(13.dp),
    date: LocalDate,
    dayLength: Int = 3,
    shape: Shape = RoundedCornerShape(16.dp),
    dateTextStyle: androidx.compose.ui.text.TextStyle = text_style_lead_body_1,
    weekDayTextStyle: androidx.compose.ui.text.TextStyle = text_style_lead_body_1,
    isSelected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val backgroundColor =
        remember(isSelected) { if (isSelected) Color(0xFFFF6A00) else Color.White }
    val borderColor =
        remember(isSelected) { if (isSelected) Color(0xFFFF6A00) else Color.Transparent }
    val dayCircleColor =
        remember(isSelected) { if (isSelected) backgroundColor else Color(0xFFF2F3F7) }
    val dayTextColor = remember(isSelected) { if (isSelected) Color.White else Color.Gray }
    val weekDayTextColor = remember(isSelected) { if (isSelected) Color.White else Color.Gray }

    val finalModifier = Modifier
        .then(
            if (enabled) Modifier.clickable(onClick = onClick)
            else Modifier
        )
        .border(1.dp, borderColor, shape = shape)
        .background(backgroundColor, shape = shape)
        .alpha(if (enabled) 1f else 0.4f)
        .then(modifier)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = finalModifier
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(dayCircleColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                style = dateTextStyle,
                modifier = Modifier.padding(2.dp),
                text = date.dayOfMonth.toString(),
                color = dayTextColor,
            )
        }
        val day = if (dayLength > 0) date.dayOfWeek.name.take(dayLength)
            .lowercase() else date.dayOfWeek.name.lowercase()

        Text(
            style = weekDayTextStyle,
            text = day.replaceFirstChar { it.uppercase() },
            color = weekDayTextColor,
        )
    }
}
