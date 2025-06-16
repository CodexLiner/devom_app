package com.devom.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devom.app.theme.greyColor
import com.devom.app.theme.primaryColor


@Composable
fun OtpView(
    otpLength: Int = 4,
    modifier: Modifier = Modifier,
    boxColor: Color = Color.White,
    borderColor: Color = greyColor,
    selectedBorderColor: Color = primaryColor,
    cornerRadius: Dp = 24.dp,
    textColor: Color = Color.Black,
    textSize: TextUnit = 24.sp,
    itemSpacing: Dp = 16.dp,
    onOtpEntered: (String) -> Unit
) {
    var otp by remember { mutableStateOf(TextFieldValue()) }

    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }

    var parentWidth by remember { mutableStateOf(0) }

    val focusManager  = LocalFocusManager.current
    LaunchedEffect(Unit) {
        focusManager.clearFocus()
    }

    Box(
        modifier = modifier.fillMaxWidth().wrapContentHeight().onSizeChanged { size ->
            parentWidth = size.width
        }.clickable(
            interactionSource = interactionSource, indication = null
        ) {
            focusRequester.requestFocus()
        }, contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = otp,
            onValueChange = { newValue ->
                if (newValue.text.length <= otpLength && newValue.text.all { it.isDigit() }) {
                    otp = newValue.copy(
                        selection = TextRange(newValue.text.length)
                    )
                    if (otp.text.length == otpLength) {
                        onOtpEntered(otp.text)
                    }
                }
            },
            interactionSource = interactionSource,
            cursorBrush = SolidColor(Color.Transparent),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = TextStyle.Default.copy(color = Color.Transparent),
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight()
                        .padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(
                        itemSpacing, Alignment.CenterHorizontally
                    ), verticalAlignment = Alignment.CenterVertically
                ) {
                    if (parentWidth > 0) {
                        val totalSpacingPx =
                            with(LocalDensity.current) { itemSpacing.toPx() * (otpLength - 1) }
                        val sidePaddingPx = with(LocalDensity.current) { 16.dp.toPx() * 2 }
                        val boxSizePx =
                            (parentWidth.toFloat() - totalSpacingPx - sidePaddingPx) / otpLength
                        val boxSizeDp = with(LocalDensity.current) { boxSizePx.toDp() }

                        repeat(otpLength) { index ->
                            Box(
                                modifier = Modifier.size(boxSizeDp)
                                    .background(boxColor, RoundedCornerShape(cornerRadius)).border(
                                        width = 1.dp,
                                        color = if (otp.text.length == index) selectedBorderColor else borderColor,
                                        shape = RoundedCornerShape(cornerRadius)
                                    ), contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = otp.text.getOrNull(index)?.toString() ?: "",
                                    style = TextStyle(
                                        fontSize = textSize,
                                        color = textColor,
                                        textAlign = TextAlign.Center
                                    )
                                )
                            }
                        }
                    }
                    innerTextField()
                }
            })
    }
}
