package com.devom.app.ui.screens.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.devom.app.theme.text_style_lead_text
import com.devom.app.theme.whiteColor
import com.devom.app.ui.components.ButtonPrimary
import com.devom.app.ui.navigation.Screens
import org.jetbrains.compose.resources.painterResource
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.ic_check

@Composable
fun SignupSuccessScreen(navHostController: NavHostController) {
    val steps = listOf("General", "Skills", "Document")
    val currentStep = 1

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = Color(0xFF4CAF50),
                        shape = CircleShape
                    )
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_check),
                    contentDescription = "Success",
                    colorFilter = ColorFilter.tint(whiteColor),
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Signup Successful!",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your account has been created successfully.\nStart exploring personalized puja services,\nastrology guidance, and more.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }

        ButtonPrimary(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 32.dp)
                .height(48.dp),
            buttonText = "Login Now",
            onClick = {
                navHostController.popBackStack(Screens.Login.path , false)
            },
            fontStyle = text_style_lead_text
        )
    }
}
