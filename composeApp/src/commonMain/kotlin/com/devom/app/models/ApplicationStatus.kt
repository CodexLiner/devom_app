package com.devom.app.models

import androidx.compose.ui.graphics.Color
import com.devom.app.theme.errorColor
import com.devom.app.theme.greenColor
import com.devom.app.theme.infoColor
import com.devom.app.theme.neutralColor
import com.devom.app.theme.secondaryColor
import com.devom.app.theme.warningColor

enum class ApplicationStatus(val status: String) {
    PENDING("pending"),
    ACCEPTED("accepted"),
    REJECTED("rejected"),
    CANCELLED("cancelled"),
    COMPLETED("completed"),
    UPCOMING("upcoming"),
    CONFIRMED("confirmed"),
    VERIFIED("verified"),
    STARTED("started"),
    PAST("past"),
}

fun String.getColor(): Color = when (this) {
    ApplicationStatus.CONFIRMED.status,
    ApplicationStatus.VERIFIED.status,
    ApplicationStatus.COMPLETED.status,
    ApplicationStatus.ACCEPTED.status, -> greenColor
    ApplicationStatus.STARTED.status -> infoColor
    ApplicationStatus.PENDING.status,
    ApplicationStatus.UPCOMING.status, -> warningColor

    ApplicationStatus.REJECTED.status,
    ApplicationStatus.CANCELLED.status,
        -> errorColor

    ApplicationStatus.PAST.status -> neutralColor
    else -> secondaryColor
}
