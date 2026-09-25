package com.schoolminimarket.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.schoolminimarket.app.ui.viewmodel.CheckoutUiState
import com.schoolminimarket.app.ui.viewmodel.CheckoutViewModel

@Composable
fun CheckoutScreen(navController: NavHostController) {
    val viewModel: CheckoutViewModel = hiltViewModel()
    // Trigger order creation when entering screen
    // To avoid duplicated calls on recomposition, assume caller triggers manually, but we can call here once
    // For simplicity, call here (Compose will recompute, but viewModel ensures idempotent state)
    viewModel.createOrder()

    val uiState = viewModel.uiState.collectAsState().value

    Column(modifier = Modifier.padding(16.dp)) {
        when (uiState) {
            is CheckoutUiState.Idle -> {
                Text(text = "Menyiapkan order…")
            }
            is CheckoutUiState.Loading -> {
                CircularProgressIndicator()
            }
            is CheckoutUiState.Success -> {
                Text(text = "Order berhasil! ID: ${uiState.orderId}")
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { navController.navigate("home") }) {
                    Text(text = "Kembali ke Beranda")
                }
            }
            is CheckoutUiState.Error -> {
                Text(text = "Error: ${uiState.message}")
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text(text = "Kembali")
                }
            }
        }
    }
}
