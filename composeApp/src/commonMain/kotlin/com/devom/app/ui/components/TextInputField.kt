package com.devom.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.devom.app.theme.bgColor
import com.devom.app.theme.textBlackShade
import com.devom.app.theme.text_style_lead_text
@Composable
fun TextInputField(
    initialValue: String = "",
    maxLength: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier.fillMaxWidth(),
    backgroundColor: Color = bgColor,
    placeholderColor: Color = com.devom.app.theme.inputColor,
    inputColor: Color = textBlackShade,
    cornerRadius: Dp = 12.dp,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = backgroundColor,
        unfocusedContainerColor = backgroundColor,
        disabledTextColor = inputColor,
        focusedTextColor = inputColor,
        unfocusedTextColor = inputColor,
        disabledContainerColor = backgroundColor,
        errorContainerColor = backgroundColor,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
    ),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    placeholder: String = "Enter phone number",
    onValueChange: (String) -> Unit = {}
) {
    var input by remember { mutableStateOf(initialValue) }

    LaunchedEffect(initialValue) {
        input = initialValue
        onValueChange(initialValue)
    }
    TextField(
        value = input,
        keyboardOptions = keyboardOptions,
        onValueChange = {
            val trimmed = it.take(maxLength)
            input = trimmed
            onValueChange(trimmed)
        },
        readOnly = readOnly,
        label = {
            Text(
                text = placeholder,
                style = text_style_lead_text,
                color = placeholderColor,
                modifier = Modifier.background(Color.Transparent)
            )
        },
        enabled = enabled,
        singleLine = true,
        shape = RoundedCornerShape(cornerRadius),
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        colors = colors,
        modifier = modifier.fillMaxWidth()
    )
}
