package com.devom.app.ui.navigation

/**
 * Sealed class representing the different screens in the application.
 * Each screen is defined as a data object inheriting from this class.
 * This class provides a type-safe way to navigate between screens
 * by using the screen's path as a unique identifier.
 *
 * @property path The unique string identifier (path) for the screen.
 */
sealed class Screens(val path: String) {
    data object Login : Screens(path = "login")
    data object Home : Screens(path = "home")
    data object BookingDetails : Screens(path = "details")
    data object Register : Screens(path = "register")
    data object Document : Screens(path = "document")
    data object SignUpSuccess : Screens(path = "signup_success")
    data object Bookings : Screens(path = "bookings")
    data object OtpScreen : Screens(path = "otpScreen")
    data object Profile : Screens(path = "profile")
    data object EditProfile : Screens(path = "edit_profile")
    data object Dashboard : Screens(path = "dashboard")
    data object SelectSlot : Screens(path = "create_slot")
    data object Notifications : Screens(path = "notifications")
    data object UploadDocument : Screens(path = "upload_document")
    data object UploadDocumentSuccess : Screens(path = "upload_document_success")
    data object ReviewsAndRatings : Screens(path = "reviews_and_ratings")
    data object Biography : Screens(path = "biography")
    data object Rituals : Screens(path = "rituals")
    data object HelpAndSupport : Screens(path = "help_and_support")
    data object ReferAndEarn : Screens(path = "refer_and_earn")
    data object Transactions : Screens(path = "transactions")
    data object TransactionsDetails : Screens(path = "transactions_details")
    data object HelpAndSupportDetailScreen : Screens(path = "help_and_support_detail_screen")
    data object AddWalletBalanceScreen : Screens(path = "bank_account_screen")
    data object BookingPaymentScreen : Screens(path = "booking_payment_screen")
    data object PanditListScreen : Screens(path = "pandit_list_screen")


}