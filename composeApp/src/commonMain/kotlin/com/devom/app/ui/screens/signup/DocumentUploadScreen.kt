package com.devom.app.ui.screens.signup


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.devom.app.theme.text_style_lead_text
import com.devom.app.ui.components.ButtonPrimary
import com.devom.app.ui.components.DocumentPicker
import com.devom.app.ui.components.ShapedScreen
import com.devom.app.ui.components.Stepper
import com.devom.app.ui.navigation.Screens


@Composable
fun DocumentUploadScreen(navHostController: NavHostController) {
    val steps = listOf("General", "Skills", "Document")
    val currentStep = 2
    ShapedScreen(
        headerContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Stepper(steps = steps, currentStep = currentStep)
            }
        },
        mainContent = {
            Box(modifier = Modifier.fillMaxSize().padding(top = 40.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DocumentPicker(title = "Aadhaar Card")
                    DocumentPicker(title = "PAN Card")
                    DocumentPicker(title = "Certificates")
                }

                // Bottom Submit Button
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    ButtonPrimary(
                        modifier = Modifier.padding(top = 48.dp).fillMaxWidth().height(58.dp),
                        buttonText = "Submit",
                        onClick = {
                            navHostController.navigate(Screens.BookingSuccess.path)
                        },
                        fontStyle = text_style_lead_text
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("I have already an account?", color = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Login",
                            color = Color(0xFFFF6A00),
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }
            }
        }
    )
}