package com.devom.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.devom.app.theme.primaryColor
import com.devom.app.ui.providers.LocalLoaderState

@Composable
fun ProgressLoader() {
    val isLoading = LocalLoaderState.current
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize().fillMaxSize()
                .clickable(interactionSource = null, indication = null) { }, contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = primaryColor
            )
        }
    }
}
