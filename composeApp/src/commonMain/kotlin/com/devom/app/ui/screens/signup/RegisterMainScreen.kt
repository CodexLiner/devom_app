package com.devom.app.ui.screens.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.devom.app.theme.backgroundColor
import com.devom.app.theme.greyColor
import com.devom.app.theme.orangeShadow
import com.devom.app.theme.text_style_lead_body_1
import com.devom.app.theme.text_style_lead_text
import com.devom.app.ui.components.AppBar
import com.devom.app.ui.components.ButtonPrimary
import com.devom.app.ui.components.ShapedScreen
import com.devom.app.ui.components.TextInputField
import com.devom.app.ui.navigation.Screens
import com.devom.app.ui.screens.booking.details.Checkbox
import com.devom.app.ui.screens.signup.fragments.UserDetailsScreenMainContent
import com.devom.app.utils.toColor
import com.devom.app.utils.urlEncode
import com.devom.models.auth.UserRequestResponse
import com.devom.utils.Application
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.all_field_required
import devom_app.composeapp.generated.resources.ic_arrow_left
import devom_app.composeapp.generated.resources.referral_code

@Composable
fun RegisterMainScreen(
    navController: NavHostController,
    phone: String,
    viewModel: SignUpViewModel = SignUpViewModel(),
    code: String,
) {
    var checked by remember { mutableStateOf(false) }

    val createUserStatus by viewModel.signUpState.collectAsStateWithLifecycle()
    var createUserRequest by remember {
        mutableStateOf(UserRequestResponse(mobileNo = phone.replace("-", ""), referralCode = code))
    }

    LaunchedEffect(createUserStatus) {
        if (createUserStatus == true) {
            navController.navigate(Screens.SignUpSuccess.path) {
                popUpTo(Screens.Register.path) {
                    inclusive = true
                }
            }
        }
    }

    ShapedScreen(
        headerContent = {
            RegisterScreenHeader(navController)
        },
        mainContent = {
            Column {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize().background(backgroundColor).weight(1f),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 40.dp)
                ) {

                    item {
                        UserDetailsScreenMainContent(createUserRequest) {
                            createUserRequest = it.copy()
                        }
                    }
                    item {
                        Text(
                            text = stringResource(Res.string.referral_code),
                            style = text_style_lead_text,
                            color = "#32343E".toColor()
                        )
                        TextInputField(
                            initialValue = createUserRequest.referralCode,
                            modifier = Modifier.padding(top = 4.dp),
                            placeholder = "Referral Code"
                        ) {
                            createUserRequest.referralCode = it
                        }

                        TermsAndConditionsCheckbox(
                            checked = checked,
                            onTermsClick = {
                                val encodedUrl = "https://devom.co.in/terms-conditions".urlEncode()
                                navController.navigate("${Screens.WebView.path}/$encodedUrl")
                            },
                            onCheckedChange = {
                                checked = !checked
                    }
                        )
                }
                }
                RegisterButtonContent(viewModel, createUserRequest, navController , checked)
            }
        }
    )
}

@Composable
fun TermsAndConditionsCheckbox(
    checked: Boolean,
    onCheckedChange: () -> Unit,
    onTermsClick: () -> Unit,
) {
    val annotatedText = buildAnnotatedString {
        append("I accept the ")

        pushStringAnnotation(tag = "TERMS", annotation = "terms")
        withStyle(
            style = SpanStyle(
                color = Color.Black,
                fontWeight = FontWeight.Normal,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append("Terms & Conditions")
        }
        pop()
    }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 24.dp)) {
        Checkbox (
            isChecked = checked,
            onClick = onCheckedChange
        )

        Spacer(modifier = Modifier.width(8.dp))

        ClickableText(
            text = annotatedText,
            style = TextStyle(color = Color.Black, fontSize = 16.sp),
            onClick = { offset ->
                annotatedText
                    .getStringAnnotations(tag = "TERMS", start = offset, end = offset)
                    .firstOrNull()
                    ?.let { onTermsClick() }
            }
        )
    }
}


@Composable
fun RegisterButtonContent(
    viewModel: SignUpViewModel,
    createUserRequest: UserRequestResponse,
    navController: NavHostController,
    checked: Boolean,
) {
    val requiredText = stringResource(Res.string.all_field_required)
    Column(modifier = Modifier.navigationBarsPadding().padding(horizontal = 16.dp)) {
        ButtonPrimary(
            enabled = checked,
            fontStyle = text_style_lead_text,
            modifier = Modifier.fillMaxWidth().height(58.dp),
            onClick = {
                val isValid = createUserRequest.isValid()
                if (isValid.first)
                    viewModel.signUp(createUserRequest)
                else Application.showToast(isValid.second ?: requiredText)
            }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            val span = SpanStyle(
                color = orangeShadow,
                textDecoration = TextDecoration.Underline
            )
            val spannedText = buildAnnotatedString {
                append("I already have an account?")
                withStyle(span) {
                    append(" Login")
                }
            }

            Text(
                spannedText,
                color = greyColor,
                style = text_style_lead_body_1,
                modifier = Modifier.clickable {
                    navController.popBackStack(Screens.Login.path, false)
                }
            )
        }
    }
}

@Composable
fun RegisterScreenHeader(navController: NavHostController) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()

    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp)) {
            AppBar(
                navigationIcon = painterResource(Res.drawable.ic_arrow_left),
                title = "SignUp",
                onNavigationIconClick = { navController.popBackStack() }
            )
        }
    }
}
