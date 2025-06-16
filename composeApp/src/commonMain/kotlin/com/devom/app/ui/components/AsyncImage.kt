package com.devom.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter.State
import org.jetbrains.compose.resources.painterResource
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.placeholder

@Composable
fun AsyncImage(
    error: Painter = painterResource(Res.drawable.placeholder),
    placeholder: Painter = painterResource(Res.drawable.placeholder),
    model: Any?,
    contentDescription: String = "image",
    contentScale: ContentScale = ContentScale.Crop,
    modifier: Modifier = Modifier,
    fallback: Painter? = error,
    onLoading: ((State.Loading) -> Unit)? = null,
    onSuccess: ((State.Success) -> Unit)? = null,
    onError: ((State.Error) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DefaultFilterQuality,
    clipToBounds: Boolean = true,

    ) {
    AsyncImage(
        modifier = modifier,
        model = model.toString(),
        contentDescription = contentDescription,
        placeholder = placeholder,
        error = error,
        contentScale = contentScale,
        fallback = fallback,
        onLoading = onLoading,
        onSuccess = onSuccess,
        onError = onError,
        alignment = alignment,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
        clipToBounds = clipToBounds
    )
}