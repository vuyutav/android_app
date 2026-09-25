package com.schoolminimarket.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolminimarket.app.model.OrderCreateItemDto
import com.schoolminimarket.app.model.OrderCreateRequest
import com.schoolminimarket.app.repository.OrderRepository
import com.schoolminimarket.app.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Idle)
    val uiState: StateFlow<CheckoutUiState> = _uiState

    fun createOrder(token: String = "dummy-token") {
        viewModelScope.launch {
            _uiState.value = CheckoutUiState.Loading
            try {
                val cart = cartRepository.getCart(token)
                if (cart.items.isEmpty()) {
                    _uiState.value = CheckoutUiState.Error("Keranjang kosong")
                    return@launch
                }
                val items = cart.items.map { cartItem ->
                    OrderCreateItemDto(
                        productId = cartItem.productId,
                        quantity = cartItem.quantity,
                        price = cartItem.price
                    )
                }
                val request = OrderCreateRequest(
                    items = items,
                    total = cart.totalCost,
                    paymentMethod = null
                )
                val response = orderRepository.createOrder(request, token)
                _uiState.value = CheckoutUiState.Success(response.orderId)
            } catch (e: Exception) {
                _uiState.value = CheckoutUiState.Error(e.localizedMessage ?: "Error creating order")
            }
        }
    }
}

sealed class CheckoutUiState {
    object Idle : CheckoutUiState()
    object Loading : CheckoutUiState()
    data class Success(val orderId: String) : CheckoutUiState()
    data class Error(val message: String) : CheckoutUiState()
}
