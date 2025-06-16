package com.devom.app.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import com.devom.app.theme.primaryColor
import com.devom.app.theme.text_style_h5
import com.devom.app.theme.whiteColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.ic_menu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String = "",
    navigationIcon: Painter = painterResource(Res.drawable.ic_menu),
    onNavigationIconClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    val isClickAllowed = remember { androidx.compose.runtime.mutableStateOf(true) }

    TopAppBar(
        title = { Text(title, color = whiteColor, style = text_style_h5) },
        navigationIcon = {
            IconButton(onClick = {
                if (isClickAllowed.value) {
                    isClickAllowed.value = false
                    onNavigationIconClick()
                    coroutineScope.launch {
                        delay(1000)
                        isClickAllowed.value = true
                    }
                }
            }) {
                Icon(navigationIcon, contentDescription = null, tint = Color.White)
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor)
    )
}
