package com.schoolminimarket.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolminimarket.app.model.PaymentInfoDto
import com.schoolminimarket.app.repository.OrderRepository
import com.schoolminimarket.app.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Loading)
    val uiState: StateFlow<PaymentUiState> = _uiState

    fun loadPaymentInfo(orderId: String) {
        viewModelScope.launch {
            try {
                // Fetch payment QR info
                val token = "dummy-token" // placeholder token
                val paymentInfo: PaymentInfoDto = paymentRepository.getPaymentInfo(orderId, token)
                _uiState.value = PaymentUiState.Success(paymentInfo)
                // Start polling for payment status
                pollPaymentStatus(orderId, token)
            } catch (e: Exception) {
                _uiState.value = PaymentUiState.Error(e.localizedMessage ?: "Error loading payment info")
            }
        }
    }

    private suspend fun pollPaymentStatus(orderId: String, token: String) {
        repeat(30) { // poll up to ~2 minutes (30 * 4s)
            delay(4000)
            try {
                val statusDto = orderRepository.getOrderStatus(orderId, token)
                if (statusDto.status.equals("PAID", ignoreCase = true)) {
                    _uiState.value = PaymentUiState.Paid(orderId)
                    return
                }
            } catch (_: Exception) {
                // ignore transient errors
            }
        }
    }
}

sealed class PaymentUiState {
    object Loading : PaymentUiState()
    data class Success(val paymentInfo: PaymentInfoDto) : PaymentUiState()
    data class Paid(val orderId: String) : PaymentUiState()
    data class Error(val message: String) : PaymentUiState()
}
