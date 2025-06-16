package com.devom.app.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.devom.app.ASSET_LINK_BASE_URL
import com.devom.app.ui.navigation.Screens.Biography
import com.devom.app.ui.navigation.Screens.BookingDetails
import com.devom.app.ui.navigation.Screens.Notifications
import com.devom.app.ui.navigation.Screens.ReviewsAndRatings
import com.devom.app.ui.screens.addbankaccount.BankAccountScreen
import com.devom.app.ui.screens.addslot.CreateSlotScreen
import com.devom.app.ui.screens.biography.BiographyScreen
import com.devom.app.ui.screens.booking.details.BookingDetailScreen
import com.devom.app.ui.screens.dashboard.DashboardScreen
import com.devom.app.ui.screens.document.UploadDocumentScreen
import com.devom.app.ui.screens.helpandsupport.HelpAndSupportDetailScreen
import com.devom.app.ui.screens.helpandsupport.HelpAndSupportScreen
import com.devom.app.ui.screens.login.LoginScreen
import com.devom.app.ui.screens.notification.NotificationScreen
import com.devom.app.ui.screens.otpscreen.VerifyOtpScreen
import com.devom.app.ui.screens.profile.EditProfileScreen
import com.devom.app.ui.screens.referandearn.ReferAndEarnScreen
import com.devom.app.ui.screens.reviews.ReviewsAndRatingsScreen
import com.devom.app.ui.screens.rituals.RitualsScreen
import com.devom.app.ui.screens.signup.DocumentUploadScreen
import com.devom.app.ui.screens.signup.RegisterMainScreen
import com.devom.app.ui.screens.signup.SignupSuccessScreen
import com.devom.app.ui.screens.transactions.TransactionDetailsScreen
import com.devom.app.ui.screens.transactions.TransactionsScreen

@Composable
fun NavigationHost(
    startDestination: String = Screens.Login.path,
    navController: NavHostController,
) {
    NavHost(
        navController = navController, startDestination = startDestination,
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) },
        popEnterTransition = { fadeIn(animationSpec = tween(300)) },
        popExitTransition = { fadeOut(animationSpec = tween(300)) },
    ) {
        composable(Screens.Login.path) {
            LoginScreen(navController)
        }
        composable(
            route = Screens.OtpScreen.path.plus("/{mobileNumber}"),
            arguments = listOf(navArgument("mobileNumber") { type = NavType.StringType })
        ) {
            VerifyOtpScreen(
                navController = navController,
                mobileNumber = it.arguments?.getString("mobileNumber")
            )
        }
        composable(Screens.SignUpSuccess.path) {
            SignupSuccessScreen(navHostController = navController)
        }

        composable(
            route = Screens.Register.path,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "${ASSET_LINK_BASE_URL}referral?phone={phone}&code={code}"
                }
            )
        ) {
            val phone = it.arguments?.getString("phone") ?: ""
            val code = it.arguments?.getString("code") ?: ""
            RegisterMainScreen(navController, phone, code = code)
        }
        composable(Screens.Document.path) {
            DocumentUploadScreen(navController)
        }
        composable(
            route = BookingDetails.path.plus("/{booking}"),
            arguments = listOf(navArgument("booking") { type = NavType.StringType })
        ) {
            BookingDetailScreen(
                navController = navController,
                bookingId = it.arguments?.getString("booking")
            )
        }
        composable(
            route = Screens.Dashboard.path
        ) {
            DashboardScreen(navController)
        }
        composable(Screens.EditProfile.path) {
            EditProfileScreen(navController)
        }
        composable(Screens.CreateSlot.path) {
            CreateSlotScreen(
                navController,
            )
        }
        composable(Notifications.path) {
            NotificationScreen(navController)
        }
        composable(Screens.UploadDocument.path) {
            UploadDocumentScreen(navController)
        }
        composable(ReviewsAndRatings.path) {
            ReviewsAndRatingsScreen(navController = navController)
        }
        composable(Biography.path) {
            BiographyScreen(navController = navController)
        }
        composable(Screens.Rituals.path) {
            RitualsScreen(navController = navController)
        }
        composable(Screens.HelpAndSupport.path) {
            HelpAndSupportScreen(navController = navController)
        }
        composable(Screens.ReferAndEarn.path) {
            ReferAndEarnScreen(navController = navController)
        }
        composable(Screens.Transactions.path) {
            TransactionsScreen(navController = navController)
        }
        composable(Screens.TransactionsDetails.path) {
            TransactionDetailsScreen(
                navController = navController,
            )
        }
        composable(
            route = Screens.HelpAndSupportDetailScreen.path.plus("/{ticketId}"),
            arguments = listOf(navArgument("ticketId") { type = NavType.StringType })
        ) {
            HelpAndSupportDetailScreen(
                navController = navController,
                ticketId = it.arguments?.getString("ticketId") ?: ""
            )
        }
        composable(Screens.BankAccountScreen.path) {
            BankAccountScreen(navController = navController)
        }
    }
}
