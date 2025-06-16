package com.devom.app.ui.screens.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devom.Project
import com.devom.models.auth.UserRequestResponse
import com.devom.models.pandit.Review
import com.devom.utils.network.onResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ReviewsAndRatingsViewModel : ViewModel() {
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews = _reviews

    init {
        getReviews()
    }

    fun getReviews() {
        viewModelScope.launch {
            Project.pandit.getPanditReviewsUseCase.invoke().collect {
                it.onResult {
                    _reviews.value = it.data.reviews
                }
            }
        }
    }
}