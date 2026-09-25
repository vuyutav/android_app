package com.schoolminimarket.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.schoolminimarket.app.ui.viewmodel.ProductDetailUiState
import com.schoolminimarket.app.ui.viewmodel.ProductDetailViewModel

@Composable
fun ProductDetailScreen(navController: NavHostController, productId: String) {
    val viewModel: ProductDetailViewModel = hiltViewModel()
    // Trigger load when composable enters composition
    viewModel.loadProduct(productId)
    val uiState = viewModel.uiState.collectAsState().value

    Column(modifier = Modifier.padding(16.dp)) {
        when (uiState) {
            is ProductDetailUiState.Loading -> {
                CircularProgressIndicator()
            }
            is ProductDetailUiState.Error -> {
                Text(text = "Error: ${(uiState as ProductDetailUiState.Error).message}")
            }
            is ProductDetailUiState.Success -> {
                val product = (uiState as ProductDetailUiState.Success).product
                // Simple image placeholder (could use Coil later)
                Image(
                    painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                    contentDescription = "Product image",
                    modifier = Modifier
                        .height(180.dp)
                        .padding(bottom = 12.dp),
                    contentScale = ContentScale.Crop
                )
                Text(text = product.name, style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Rp ${"%,d".format(product.price.toInt())}", style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = product.description ?: "Tidak ada deskripsi", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Stok: ${product.stock}", style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(16.dp))
                // Add to cart button with state handling
                val addState = viewModel.addCartState.collectAsState().value
                when (addState) {
                    is AddCartUiState.Idle, is AddCartUiState.Success -> {
                        Button(onClick = { viewModel.addToCart(product.id) }) {
                            Text(text = "Tambah ke Keranjang")
                        }
                        if (addState is AddCartUiState.Success) {
                            Text(text = "Ditambahkan ke keranjang", style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                        }
                    }
                    is AddCartUiState.Loading -> {
                        CircularProgressIndicator()
                    }
                    is AddCartUiState.Error -> {
                        Text(text = "Error: ${(addState as AddCartUiState.Error).message}")
                    }
                }

            }
        }
    }
}
