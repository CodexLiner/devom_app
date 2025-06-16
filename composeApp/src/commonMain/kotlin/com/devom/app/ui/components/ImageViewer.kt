package com.devom.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.devom.app.theme.backgroundColor
import com.devom.app.theme.primaryColor
import com.devom.app.theme.whiteColor
import com.devom.app.utils.toDevomDocument
import com.devom.models.pandit.Media
import org.jetbrains.compose.resources.painterResource
import pandijtapp.composeapp.generated.resources.Res
import pandijtapp.composeapp.generated.resources.ic_close

@Composable
fun ImageViewer(viewImage: MutableState<Boolean>, selectedMedia: MutableState<Media?>) {
    if (viewImage.value) {
        Dialog(onDismissRequest = { viewImage.value = false }) {
            Box(
                modifier = Modifier.size(400.dp)
                    .background(backgroundColor, shape = RoundedCornerShape(12.dp))

            ) {
                Column(horizontalAlignment = Alignment.End) {
                    // Close button (aligned to top-right)
                    Box(
                        modifier = Modifier.wrapContentSize().background(primaryColor, CircleShape),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        IconButton(onClick = { viewImage.value = false }) {
                            Image(
                                colorFilter = ColorFilter.tint(whiteColor),
                                painter = painterResource(Res.drawable.ic_close),
                                contentDescription = "Close"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Image display
                    selectedMedia.value?.let { media ->
                        AsyncImage(
                            model =  media.documentUrl.toDevomDocument(),
                            modifier = Modifier.fillMaxWidth().wrapContentHeight()
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.FillWidth
                        )
                    }
                }
            }
        }
    }
}