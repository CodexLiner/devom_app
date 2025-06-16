package com.devom.app.ui.screens.booking.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.devom.app.theme.*
import com.devom.app.ui.components.ButtonPrimary
import com.devom.app.ui.components.OtpView
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import devom_app.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartEndPoojaSheet(
    title: String? = null,
    message: String? = null,
    buttonText: String? = null,
    showSheet: Boolean,
    onDismiss: () -> Unit,
    onOtpEntered: (String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    if (showSheet) {
        ModalBottomSheet(
            containerColor = whiteColor,
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    onDismiss()
                }
            },
            sheetState = sheetState
        ) {
            BottomSheetContent(
                title = title,
                message = message,
                buttonText = buttonText,
                onClick = { otp ->
                    scope.launch {
                        sheetState.hide()
                        onDismiss()
                        if (otp.isNotEmpty()) onOtpEntered(otp)
                    }
                }
            )
        }
    }
}

@Composable
private fun BottomSheetContent(
    title: String? = null,
    message: String? = null,
    buttonText: String? = null,
    onClick: (String) -> Unit,
) {
    val otpState = remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        title?.let {
            Text(text = it, style = text_style_h3, color = blackColor)
        }

        message?.let {
            Text(
                text = it,
                modifier = Modifier.padding(top = 16.dp),
                style = text_style_lead_text,
                color = greyColor
            )
        }

        OtpView(modifier = Modifier.padding(top = 48.dp)) {
            otpState.value = it
        }

        buttonText?.let {
            ButtonPrimary(
                modifier = Modifier
                    .padding(top = 26.dp)
                    .fillMaxWidth()
                    .height(58.dp),
                buttonText = it
            ) {
                onClick(otpState.value)
            }
        }

        val spannedText = buildAnnotatedString {
            append(stringResource(Res.string.resend_otp_message))
            withStyle(SpanStyle(color = orangeShadow, textDecoration = TextDecoration.Underline)) {
                append(stringResource(Res.string.resend_otp))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp).padding(top = 24.dp),
            contentAlignment = Alignment.Center
        ) {
//            Text(
//                text = spannedText,
//                color = greyColor,
//                style = text_style_lead_body_1,
//                modifier = Modifier.clickable { onResendOtp() }
//            )
        }
    }
}
