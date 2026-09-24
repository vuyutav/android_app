package com.schoolminimarket.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolminimarket.app.model.ProductDetailDto
import com.schoolminimarket.app.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val uiState: StateFlow<ProductDetailUiState> = _uiState

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
}

sealed class ProductDetailUiState {
    object Loading : ProductDetailUiState()
    data class Success(val product: ProductDetailDto) : ProductDetailUiState()
    data class Error(val message: String) : ProductDetailUiState()
}
