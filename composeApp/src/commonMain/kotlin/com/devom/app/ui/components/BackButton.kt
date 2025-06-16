package com.devom.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import pandijtapp.composeapp.generated.resources.Res
import pandijtapp.composeapp.generated.resources.ic_left

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: Painter = painterResource(Res.drawable.ic_left),
    content: @Composable () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val isClickAllowed = remember { androidx.compose.runtime.mutableStateOf(true) }

    Column(
        modifier = modifier.fillMaxSize().statusBarsPadding(), horizontalAlignment = Alignment.Start
    ) {
        IconButton(
            onClick = {
                if (isClickAllowed.value) {
                    isClickAllowed.value = false
                    onClick()
                    coroutineScope.launch {
                        delay(1000)
                        isClickAllowed.value = true
                    }
                }
            }, modifier = Modifier.wrapContentSize()
        ) {
            Image(
                painter = icon, contentDescription = "Back", modifier = Modifier.size(24.dp)
            )
        }

        Box(
            modifier = Modifier.weight(1f)
        ) {
            content()
        }
    }
}

