package com.devom.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devom.app.theme.bgColor
import com.devom.app.theme.greyColor
import com.devom.app.theme.textBlackShade
import com.devom.app.theme.text_style_lead_text
import com.devom.app.theme.whiteColor
import org.jetbrains.compose.resources.painterResource
import pandijtapp.composeapp.generated.resources.Res
import pandijtapp.composeapp.generated.resources.ic_close

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagInputField(
    modifier: Modifier = Modifier.fillMaxWidth(),
    initialTags: List<String> = emptyList(),
    backgroundColor: Color = bgColor,
    placeholderColor: Color = com.devom.app.theme.inputColor,
    inputColor: Color = textBlackShade,
    cornerRadius: Dp = 12.dp,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    colors: TextFieldColors = defaultTagInputColors(backgroundColor, inputColor),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    placeholder: String = "Enter tag",
    label: String = placeholder,
    onTagsChanged: (List<String>) -> Unit = {},
) {
    var textValue by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf(initialTags.toList()) }
    val interactionSource = remember { MutableInteractionSource() }
    val density = LocalDensity.current
    val mergedTextStyle = LocalTextStyle.current.merge(TextStyle(color = inputColor))

    LaunchedEffect(initialTags) {
        tags = initialTags.map { it.trim() }.filter { it.isNotEmpty() }
        onTagsChanged(tags)
    }


    BasicTextField(
        value = textValue,
        onValueChange = { value ->
            if (value.contains(",") || value.contains("\n")) {
                val newTags = value
                    .split(",", "\n")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                tags = tags + newTags
                textValue = ""
                onTagsChanged(tags.map { it.trim() })
            } else {
                textValue = value
            }
        },
        modifier = modifier
            .semantics(mergeDescendants = true) {}
            .padding(top = with(density) { 8.toDp() })
            .defaultMinSize(
                minWidth = OutlinedTextFieldDefaults.MinWidth,
                minHeight = OutlinedTextFieldDefaults.MinHeight
            ),
        textStyle = mergedTextStyle,
        singleLine = false,
        minLines = 1,
        maxLines = 4,
        enabled = enabled,
        readOnly = readOnly,
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(
            onDone = {
                if (textValue.isNotBlank()) {
                    tags = tags + textValue.trim()
                    textValue = ""
                    onTagsChanged(tags.map { it.trim() })
                }
            }
        ),
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            TagDecorationBox(
                tags = tags.map { it.trim() },
                textValue = if (tags.isNotEmpty()) " " else textValue,
                onTagRemoved = {
                    tags = tags - it
                    onTagsChanged(tags.map { it.trim() })
                },
                innerTextField = innerTextField,
                placeholder = placeholder,
                label = label,
                enabled = enabled,
                colors = colors,
                placeholderColor = placeholderColor,
                interactionSource = interactionSource,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                cornerRadius = cornerRadius
            )
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TagDecorationBox(
    tags: List<String>,
    textValue: String,
    onTagRemoved: (String) -> Unit,
    innerTextField: @Composable () -> Unit,
    placeholder: String,
    label: String,
    enabled: Boolean,
    colors: TextFieldColors,
    placeholderColor: Color,
    interactionSource: InteractionSource,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    cornerRadius: Dp
) {
    TextFieldDefaults.DecorationBox(
        value = textValue,
        innerTextField = {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tags.forEach { tag ->
                    TagItem(tag = tag, onRemove = { onTagRemoved(tag) })
                }

                Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                    innerTextField()
                }
            }
        },
        placeholder = {
            if (tags.isEmpty()) {
                Text(text = placeholder, color = placeholderColor)
            }
        },
        label = {
            Text(
                text = label,
                style = text_style_lead_text,
                color = placeholderColor,
                modifier = Modifier.background(Color.Transparent)
            )
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        enabled = enabled,
        isError = false,
        singleLine = false,
        interactionSource = interactionSource,
        colors = colors,
        visualTransformation = VisualTransformation.None,
        container = {
            OutlinedTextFieldDefaults.Container(
                enabled = enabled,
                isError = false,
                interactionSource = interactionSource,
                colors = colors,
                shape = RoundedCornerShape(cornerRadius)
            )
        }
    )
}


@Composable
private fun TagItem(tag: String, onRemove: () -> Unit) {
    Box(
        modifier = Modifier
            .background(greyColor.copy(.16f), RoundedCornerShape(4.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = tag, color = textBlackShade, fontWeight = FontWeight.W600 , fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Image(
                painter = painterResource(Res.drawable.ic_close),
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(14.dp)
                    .background(color = whiteColor , CircleShape)
                    .clickable(onClick = onRemove),
                colorFilter = ColorFilter.tint(greyColor),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun defaultTagInputColors(backgroundColor: Color, inputColor: Color): TextFieldColors {
    return TextFieldDefaults.colors(
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
    )
}
