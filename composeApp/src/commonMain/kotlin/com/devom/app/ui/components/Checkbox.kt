package com.devom.app.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.devom.app.theme.primaryColor
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.ic_check
import org.jetbrains.compose.resources.painterResource

@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    checkedBorderColor: Color = primaryColor,
    uncheckedBorderColor: Color = Color.Gray,
    checkmarkColor: Color = primaryColor,
    size: Dp = 20.dp,
    cornerRadius: Dp = 4.dp,
    enabled: Boolean = true,
) {
    val borderColor = if (checked) checkedBorderColor else uncheckedBorderColor

    Box(
        modifier = modifier
            .size(size)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(cornerRadius)
            )
            .background(Color.Transparent, shape = RoundedCornerShape(cornerRadius))
            .then(
                if (enabled) Modifier.clickable { onCheckedChange(!checked) } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                painter = painterResource(Res.drawable.ic_check),
                contentDescription = "Checked",
                tint = checkmarkColor,
                modifier = Modifier.size(size * 0.6f)
            )
        }
    }
}