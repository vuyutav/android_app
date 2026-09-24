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
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            try {
                // In a real app, token would be retrieved from secure storage.
                val dummyToken = "dummy-token"
                val result = productRepository.getProducts(
                    search = null,
                    categoryId = null,
                    page = 1,
                    size = 10,
                    token = dummyToken
                )
                _uiState.value = HomeUiState.Success(result.items)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.localizedMessage ?: "Error fetching products")
            }
        }
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val products: List<ProductDto>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
