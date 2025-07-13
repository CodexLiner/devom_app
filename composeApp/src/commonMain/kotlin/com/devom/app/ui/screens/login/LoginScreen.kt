package com.devom.app.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.devom.utils.Application
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.devom_logo
import devom_app.composeapp.generated.resources.ic_google
import devom_app.composeapp.generated.resources.ic_phone
import devom_app.composeapp.generated.resources.sign_in_with_google
import devom_app.composeapp.generated.resources.text_dont_have_account
import devom_app.composeapp.generated.resources.text_login
import devom_app.composeapp.generated.resources.text_login_description
import devom_app.composeapp.generated.resources.text_phone_number
import devom_app.composeapp.generated.resources.text_sign_up
import com.devom.app.EMPTY
import com.devom.app.theme.greyColor
import com.devom.app.theme.orangeShadow
import com.devom.app.theme.text_style_h2
import com.devom.app.theme.text_style_lead_body_1
import com.devom.app.theme.text_style_lead_text
import com.devom.app.theme.whiteColor
import com.devom.app.ui.components.ButtonPrimary
import com.devom.app.ui.components.ShapedScreen
import com.devom.app.ui.components.TextInputField
import com.devom.app.ui.navigation.Screens
import com.devom.app.utils.toColor
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginScreen(navController: NavController , viewModel: LoginViewModel = viewModel { LoginViewModel() }) {
    ShapedScreen(headerContent = {
        LoginHeaderContent()
    }, mainContent = {
        LoginMainContent(navController , viewModel)
    })
}

@Composable
fun LoginMainContent(navController: NavController , viewModel: LoginViewModel) {

    val mobileNumber = remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        Application.hideLoader()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // --- Login Texts ---
        Text(
            text = stringResource(Res.string.text_login),
            style = text_style_h2,
        )
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(Res.string.text_login_description),
            style = text_style_lead_text,
        )

        // --- Phone Input ---
        Text(
            modifier = Modifier.padding(top = 32.dp, bottom = 3.dp),
            text = stringResource(Res.string.text_phone_number),
            style = text_style_lead_text,
            color = "#32343E".toColor()
        )
        TextInputField(
            maxLength = 10,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 3.dp),
            leadingIcon = {
                Image(
                    painter = painterResource(Res.drawable.ic_phone),
                    contentDescription = EMPTY,
                    modifier = Modifier.size(24.dp)
                )
            },
            onValueChange = {
                mobileNumber.value = it
            }
        )

        // --- Primary Button (Phone Login) ---
        ButtonPrimary(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .height(52.dp),
            onClick = {
                if(mobileNumber.value.isNotBlank()) {
                    viewModel.sendOtp(mobileNumber.value) {
                        navController.navigate(Screens.OtpScreen.path.plus("/${mobileNumber.value}"))
                    }
                } else Application.showToast("please enter mobile number.")
            },
            fontStyle = text_style_lead_text,
        )

        // --- Divider with OR ---
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(vertical = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(greyColor)
            )
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .background(whiteColor)
                    .padding(horizontal = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "OR", color = greyColor, style = text_style_lead_text)
            }
        }

        // --- Google Login Button ---
        ButtonPrimary(
            fontStyle = text_style_lead_text,
            buttonText = stringResource(Res.string.sign_in_with_google),
            textColor = Color.Black,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
            ),
            leadingIcon = {
                Image(
                    painter = painterResource(Res.drawable.ic_google),
                    contentDescription = EMPTY,
                    modifier = Modifier.size(24.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp) // ➔ Consistent button height
                .border(1.dp, color = greyColor.copy(0.2f), shape = RoundedCornerShape(12.dp))
        ) {}

        // --- Sign Up link ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .clickable {
                    navController.navigate(Screens.Register.path)
                },
            contentAlignment = Alignment.Center
        ) {
            val span = SpanStyle(color = orangeShadow, textDecoration = TextDecoration.Underline)
            val spannedText = buildAnnotatedString {
                append(stringResource(Res.string.text_dont_have_account))
                withStyle(span) {
                    append(stringResource(Res.string.text_sign_up))
                }
            }
            Text(spannedText, color = greyColor, style = text_style_lead_body_1)
        }
    }
}


@Composable
private fun LoginHeaderContent() {
    Box(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.devom_logo),
            modifier = Modifier.size(188.dp),
            contentDescription = EMPTY
        )
    }
}
