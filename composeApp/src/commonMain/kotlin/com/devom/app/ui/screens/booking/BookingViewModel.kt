package com.devom.app.ui.screens.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devom.Project
import com.devom.app.models.ApplicationStatus
import com.devom.models.pandit.CreateReviewInput
import com.devom.models.poojaitems.GetPoojaItemsResponse
import com.devom.models.slots.CancelBookingInput
import com.devom.models.slots.GetBookingsResponse
import com.devom.models.slots.RemoveAndUpdatePoojaItemRequest
import com.devom.models.slots.UpdateBookingStatusInput
import com.devom.network.getUser
import com.devom.utils.Application
import com.devom.utils.cachepolicy.CachePolicy
import com.devom.utils.network.onResult
import com.devom.utils.network.onResultNothing
import com.devom.utils.network.withSuccess
import com.devom.utils.network.withSuccessWithoutData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookingViewModel : ViewModel() {

    private val _bookings = MutableStateFlow<List<GetBookingsResponse>>(listOf())
    val bookings: StateFlow<List<GetBookingsResponse>> = _bookings

    private val _bookingDetailItem = MutableStateFlow<GetBookingsResponse?>(null)
    val bookingDetailItem: StateFlow<GetBookingsResponse?> = _bookingDetailItem


    fun getBookings() {
        viewModelScope.launch {
            Project.pandit.getPanditBookingsUseCase.invoke(cachePolicy = CachePolicy.CacheAndNetwork)
                .collect {
                    it.onResult {
                        _bookings.value = it.data
                    }
                }
        }
    }

    fun getBookingById(bookingId: String) {
        viewModelScope.launch {
            Project.pandit.getPanditBookingById.invoke(bookingId).collect {
                it.onResult {
                    _bookingDetailItem.value = it.data
                }
            }
        }
    }

    fun addBookingReview(booking: GetBookingsResponse, rating: Int, reviewText: String) {
        viewModelScope.launch {
            Project.pandit.createPanditReviewUseCase.invoke(
                input = CreateReviewInput(
                    bookingId = booking.bookingId.toString(),
                    poojaId = booking.poojaId,
                    panditId = booking.panditId,
                    rating = rating.toString(),
                    userId = getUser().userId.toString(),
                    reviewText = reviewText
                )
            ).collect {
                it.onResultNothing {
                    Application.showToast("Review added successfully")
                }
            }
        }
    }

    fun cancelBooking(bookingId: CancelBookingInput) {
        viewModelScope.launch {
            Project.pandit.cancelBookingUseCase.invoke(bookingId).collect {
                it.onResultNothing {
                    getBookings()
                    Application.showToast("Booking cancelled successfully")
                }
                it.onResult {
                    getBookings()
                    Application.showToast("Booking cancelled successfully")
                }
            }
        }
    }
}