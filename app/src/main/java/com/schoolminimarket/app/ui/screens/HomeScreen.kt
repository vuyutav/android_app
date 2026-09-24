package com.schoolminimarket.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.navigation.NavHostController
import com.schoolminimarket.app.ui.viewmodel.HomeUiState
import com.schoolminimarket.app.ui.viewmodel.HomeViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeScreen(navController: NavHostController) {
    val viewModel: HomeViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState().value

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Selamat datang di Minimarket Sekolah", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(24.dp))
        when (uiState) {
            is HomeUiState.Loading -> {
                CircularProgressIndicator()
            }
            is HomeUiState.Error -> {
                Text(text = "Error: ${(uiState as HomeUiState.Error).message}")
            }
            is HomeUiState.Success -> {
                val products = (uiState as HomeUiState.Success).products
                LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                    items(products) { product ->
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(text = product.name, style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
                            Text(text = "Rp ${"%,d".format(product.price.toInt())}", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
                            Text(text = "Stok: ${product.stock}", style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { /* TODO: navigate to product list screen */ }) {
            Text(text = "Lihat Semua Produk")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { /* TODO: navigate to order history */ }) {
            Text(text = "Riwayat Pesanan")
        }
    }
}
