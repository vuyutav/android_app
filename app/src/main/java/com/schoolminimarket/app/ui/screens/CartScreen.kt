package com.schoolminimarket.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.schoolminimarket.app.model.CartItemDto
import com.schoolminimarket.app.ui.viewmodel.CartUiState
import com.schoolminimarket.app.ui.viewmodel.CartViewModel

@Composable
fun CartScreen(navController: NavHostController) {
    val viewModel: CartViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState().value

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Keranjang Belanja", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(12.dp))
        when (uiState) {
            is CartUiState.Loading -> {
                CircularProgressIndicator()
            }
            is CartUiState.Error -> {
                Text(text = "Error: ${(uiState as CartUiState.Error).message}")
            }
            is CartUiState.Success -> {
                val cart = (uiState as CartUiState.Success).cart
                if (cart.items.isEmpty()) {
                    Text(text = "Keranjang kosong")
                } else {
                    LazyColumn(contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp)) {
                        items(cart.items) { item ->
                            CartItemRow(item)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Total: Rp ${"%,d".format(cart.totalCost.toInt())}",
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { /* TODO: navigate to checkout */ }) {
                        Text(text = "Checkout")
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(item: CartItemDto) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = item.productName, style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
        Text(text = "Qty: ${item.quantity}", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
        Text(
            text = "Harga: Rp ${"%,d".format(item.price.toInt())}",
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Subtotal: Rp ${"%,d".format(item.total.toInt())}",
            style = androidx.compose.material3.MaterialTheme.typography.bodySmall
        )
    }
}
