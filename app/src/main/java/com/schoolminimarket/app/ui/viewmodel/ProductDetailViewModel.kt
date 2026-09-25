package com.schoolminimarket.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolminimarket.app.model.AddCartItemRequest
import com.schoolminimarket.app.model.ProductDetailDto
import com.schoolminimarket.app.repository.CartRepository
import com.schoolminimarket.app.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val uiState: StateFlow<ProductDetailUiState> = _uiState

    private val _addCartState = MutableStateFlow<AddCartUiState>(AddCartUiState.Idle)
    val addCartState: StateFlow<AddCartUiState> = _addCartState

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            try {
                val dummyToken = "dummy-token"
                val product = productRepository.getProductDetail(productId, dummyToken)
                _uiState.value = ProductDetailUiState.Success(product)
            } catch (e: Exception) {
                _uiState.value = ProductDetailUiState.Error(e.localizedMessage ?: "Error fetching product detail")
            }
        }
    }

    fun addToCart(productId: String, quantity: Int = 1) {
        viewModelScope.launch {
            _addCartState.value = AddCartUiState.Loading
            try {
                val dummyToken = "dummy-token"
                val request = AddCartItemRequest(productId = productId, quantity = quantity)
                cartRepository.addOrUpdateCartItem(request, dummyToken)
                _addCartState.value = AddCartUiState.Success
            } catch (e: Exception) {
                _addCartState.value = AddCartUiState.Error(e.localizedMessage ?: "Error adding to cart")
            }
        }
    }
}

sealed class ProductDetailUiState {
    object Loading : ProductDetailUiState()
    data class Success(val product: ProductDetailDto) : ProductDetailUiState()
    data class Error(val message: String) : ProductDetailUiState()
}

sealed class AddCartUiState {
    object Idle : AddCartUiState()
    object Loading : AddCartUiState()
    object Success : AddCartUiState()
    data class Error(val message: String) : AddCartUiState()
}
