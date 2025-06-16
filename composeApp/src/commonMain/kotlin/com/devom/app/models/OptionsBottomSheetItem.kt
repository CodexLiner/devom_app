package com.devom.app.models

import androidx.compose.ui.graphics.painter.Painter

data class OptionsBottomSheetItem(
    val icon: Painter? = null,
    val title: String,
    val description: String? = null,
)