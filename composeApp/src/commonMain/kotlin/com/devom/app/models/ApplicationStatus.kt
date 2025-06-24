package com.devom.app.models

enum class ApplicationStatus(val status: String) {
    PENDING("pending"),
    ACCEPTED("accepted"),
    REJECTED("rejected"),
    CANCELLED("cancelled"),
    COMPLETED("completed"),
    UPCOMING("upcoming"),
    CONFIRMED("confirmed"),
    VERIFIED("verified"),
    PAST("past"),
}