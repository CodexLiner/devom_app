package com.devom.app.ui.screens.home
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.ic_patterns_home
import org.jetbrains.compose.resources.painterResource

@Composable
fun PatternDesign(modifier: Modifier) {
    Image(painter = painterResource(Res.drawable.ic_patterns_home), contentDescription = null, modifier = modifier)
}