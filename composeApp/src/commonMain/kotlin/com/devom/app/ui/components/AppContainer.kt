package com.devom.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.devom.app.theme.backgroundColor

@Composable
fun AppContainer(modifier: Modifier = Modifier.background(backgroundColor).fillMaxSize() , content: @Composable () -> Unit) {
    Box(modifier = modifier) {
        content()
    }
}