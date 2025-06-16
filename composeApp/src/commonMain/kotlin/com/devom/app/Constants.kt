package com.devom.app

import com.devom.app.theme.greenColor
import com.devom.app.theme.greenColorLight
import com.devom.app.theme.primaryColor
import com.devom.app.theme.secondaryColor
import com.devom.app.theme.yellowColor

const val EMPTY = ""
const val ACCESS_TOKEN_KEY = "locallySavedAccessTokenKey"
const val REFRESH_TOKEN_KEY = "locallySavedRefreshTokenKey"
const val UUID_KEY = "uuid"
const val IMAGE_BASE_URL = "https://imageserver.devom.co.in/uploads/"
const val DOCUMENT_BASE_URL = "https://imageserver.devom.co.in/uploads/documents/"
const val BASE_URL = "https://devom-api-bold-smoke-8130.fly.dev"
const val ASSET_LINK_BASE_URL = "https://devom.co.in/"

val RatingColors = listOf(
    greenColor,
    greenColorLight,
    yellowColor,
    primaryColor,
    secondaryColor
).reversed()
