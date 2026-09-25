package com.schoolminimarket.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolminimarket.app.model.CartDto
import com.schoolminimarket.app.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CartUiState>(CartUiState.Loading)
    val uiState: StateFlow<CartUiState> = _uiState

    init {
        loadCart()
    }

    private fun loadCart() {
        viewModelScope.launch {
            try {
                val dummyToken = "dummy-token"
                val cart = cartRepository.getCart(dummyToken)
                _uiState.value = CartUiState.Success(cart)
            } catch (e: Exception) {
                _uiState.value = CartUiState.Error(e.localizedMessage ?: "Error loading cart")
            }
        }
    }
}

sealed class CartUiState {
    object Loading : CartUiState()
    data class Success(val cart: CartDto) : CartUiState()
    data class Error(val message: String) : CartUiState()
}
