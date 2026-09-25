package com.schoolminimarket.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolminimarket.app.model.OrderCreateResponse
import com.schoolminimarket.app.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val orderRepository: OrderRepository // placeholder, not used currently
) : ViewModel() {

    private val _uiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Loading)
    val uiState: StateFlow<PaymentUiState> = _uiState

    // In real implementation, would fetch payment URL/QR based on orderId
    fun loadPaymentInfo(orderId: String) {
        viewModelScope.launch {
            // Simulate loading delay (could be replaced with actual API call)
            try {
                // For now just succeed immediately
                _uiState.value = PaymentUiState.Success(orderId)
            } catch (e: Exception) {
                _uiState.value = PaymentUiState.Error(e.localizedMessage ?: "Error loading payment info")
            }
        }
    }
}

sealed class PaymentUiState {
    object Loading : PaymentUiState()
    data class Success(val orderId: String) : PaymentUiState()
    data class Error(val message: String) : PaymentUiState()
}
