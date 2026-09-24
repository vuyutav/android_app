package com.schoolminimarket.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolminimarket.app.model.ProductDto
import com.schoolminimarket.app.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)
    val uiState: StateFlow<ProductListUiState> = _uiState

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            try {
                val dummyToken = "dummy-token"
                val result = productRepository.getProducts(
                    search = null,
                    categoryId = null,
                    page = 1,
                    size = 20,
                    token = dummyToken
                )
                _uiState.value = ProductListUiState.Success(result.items)
            } catch (e: Exception) {
                _uiState.value = ProductListUiState.Error(e.localizedMessage ?: "Error fetching products")
            }
        }
    }
}

sealed class ProductListUiState {
    object Loading : ProductListUiState()
    data class Success(val products: List<ProductDto>) : ProductListUiState()
    data class Error(val message: String) : ProductListUiState()
}
