package com.devom.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.devom.app.theme.greyColor
import com.devom.app.theme.text_style_h3
import com.devom.app.theme.text_style_lead_body_1
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import pandijtapp.composeapp.generated.resources.Res
import pandijtapp.composeapp.generated.resources.img_bell

@Composable
fun NoContentView(
    modifier: Modifier = Modifier,
    title: String? = "No Notifications",
    message: String? = "You haven't received any notifications yet.",
    image: DrawableResource? = Res.drawable.img_bell,
    titleTextStyle : TextStyle = text_style_h3
) {
    Column(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        image?.let {
            Image(
                painter = painterResource(image),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
        }

        title?.let {
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = title,
                style = titleTextStyle
            )
        }
        message?.let {
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = message,
                color = greyColor,
                textAlign = TextAlign.Center,
                style = text_style_lead_body_1
            )
        }
    }
}